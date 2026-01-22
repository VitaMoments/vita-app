package eu.vitamoments.app.services

import eu.vitamoments.app.data.mapper.extension_functions.toNevoScaledOrNull
import eu.vitamoments.app.data.tables.nevo.FoodGroupsTable
import eu.vitamoments.app.data.tables.nevo.NutrientsTable
import eu.vitamoments.app.data.tables.nevo.ProductNutrientsTable
import eu.vitamoments.app.data.tables.nevo.ProductsTable
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

object NevoCsvColumns {
    const val VERSION = "NEVO-versie/NEVO-version"
    const val FOODGROUP_NL = "Voedingsmiddelgroep"
    const val FOODGROUP_EN = "Food group"
    const val NEVO_CODE = "NEVO-code"
    const val NAME_NL = "Voedingsmiddelnaam/Dutch food name"
    const val NAME_EN = "Engelse naam/Food name"
    const val SYNONYM = "Synoniem"
    const val QUANTITY = "Hoeveelheid/Quantity"
    const val REMARK = "Opmerking"
    const val TRACES = "Bevat sporen van/Contains traces of"
    const val FORTIFIED = "Is verrijkt met/Is fortified with"
}

data class NevoHeaderInfo(
    val index: Map<String, Int>,
    val nutrientColumns: List<NutrientColumn>
)

data class NutrientColumn(
    val headerIndex: Int,
    val code: String,
    val unitRaw: String
)

data class NutrientHeaderParsed(
    val code: String,
    val unitRaw: String
)

fun parseNutrientHeader(header: String): NutrientHeaderParsed? {
    val idx = header.lastIndexOf(" (")
    if (idx <= 0 || !header.endsWith(")")) return null
    val code = header.substring(0, idx).trim()
    val unitRaw = header.substring(idx + 2, header.length - 1).trim()
    return NutrientHeaderParsed(code = code, unitRaw = unitRaw)
}

fun parsePipeCsvLine(line: String): List<String> {
    val out = ArrayList<String>(256)
    val sb = StringBuilder()
    var inQuotes = false
    var i = 0

    while (i < line.length) {
        val c = line[i]
        when (c) {
            '"' -> {
                // handle doubled quotes "" inside quoted field
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    sb.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            }
            '|' -> {
                if (inQuotes) sb.append(c) else {
                    out.add(sb.toString())
                    sb.setLength(0)
                }
            }
            else -> sb.append(c)
        }
        i++
    }
    out.add(sb.toString())
    return out
}


fun importNevoCsvIntoPostgres(
    csvPath: Path,
    wipeExisting: Boolean = true,
) {
    // ---- 1) Read header outside transaction
    val headerInfo = Files.newBufferedReader(csvPath).use { br ->
        val headerLine = br.readLine() ?: error("Empty CSV")
        val headers = parsePipeCsvLine(headerLine)
        val index = headers.withIndex().associate { it.value to it.index }

        val nutrientColumns = headers.withIndex()
            .mapNotNull { (i, h) ->
                // nutrient headers have pattern "CODE (unit)" and start after the meta columns,
                // but we can just parse all that match.
                val parsed = parseNutrientHeader(h) ?: return@mapNotNull null
                NutrientColumn(headerIndex = i, code = parsed.code, unitRaw = parsed.unitRaw)
            }

        NevoHeaderInfo(index = index, nutrientColumns = nutrientColumns)
    }

    fun idx(name: String): Int = headerInfo.index[name]
        ?: error("Missing required column: $name")

    // ---- 2) Pre-scan distinct food groups (deterministic groupNo)
    val groups = linkedMapOf<String, String?>() // nl -> en
    Files.newBufferedReader(csvPath).use { br ->
        br.readLine() // skip header
        var line: String?
        while (br.readLine().also { line = it } != null) {
            val row = parsePipeCsvLine(line!!)
            val nl = row[idx(NevoCsvColumns.FOODGROUP_NL)].trim()
            val en = row[idx(NevoCsvColumns.FOODGROUP_EN)].trim().ifEmpty { null }
            if (nl.isNotEmpty()) groups.putIfAbsent(nl, en)
        }
    }
    val sortedGroupNames = groups.keys.sorted()
    val groupNoByName = sortedGroupNames.withIndex().associate { (i, nl) -> nl to (i + 1) }

    // ---- 3) Transaction: schema + (optional) wipe + insert everything
    transaction {

        if (wipeExisting) {
            ProductNutrientsTable.deleteAll()
            ProductsTable.deleteAll()
            NutrientsTable.deleteAll()
            FoodGroupsTable.deleteAll()
        }

        // 3a) Upsert nutrients (code unique)
        val nutrientIdByCode = HashMap<String, UUID>(headerInfo.nutrientColumns.size)

        // Insert all nutrients fresh (because wipe is default). If you set wipeExisting=false,
        // you can extend this to "select-then-insert/update".
        headerInfo.nutrientColumns
            .distinctBy { it.code }
            .forEach { n ->
                val id = UUID.randomUUID()
                NutrientsTable.insert {
                    it[NutrientsTable.id] = id
                    it[code] = n.code
                    it[unit] = n.unitRaw
                }
                nutrientIdByCode[n.code] = id
            }

        // 3b) Insert food groups (deterministic groupNo)
        val foodGroupIdByNameNl = HashMap<String, UUID>(groups.size)

        sortedGroupNames.forEach { nameNl ->
            val id = UUID.randomUUID()
            FoodGroupsTable.insert {
                it[FoodGroupsTable.id] = id
                it[groupNo] = groupNoByName.getValue(nameNl)
                it[FoodGroupsTable.nameNl] = nameNl
                it[nameEn] = groups[nameNl]
            }
            foodGroupIdByNameNl[nameNl] = id
        }

        // 3c) Insert products + product nutrients
        val productIdByNevoCode = HashMap<Int, UUID>(8192)

        // For speed: batch up inserts of ProductNutrients
        val nutrientBatch = ArrayList<Triple<UUID, UUID, Long?>>(100_000)

        fun flushNutrientBatch() {
            if (nutrientBatch.isEmpty()) return
            ProductNutrientsTable.batchInsert(nutrientBatch, ignore = false) { (productId, nutrientId, valueScaled) ->
                this[ProductNutrientsTable.id] = UUID.randomUUID()
                this[ProductNutrientsTable.productId] = productId
                this[ProductNutrientsTable.nutrientId] = nutrientId
                this[ProductNutrientsTable.valueScaled] = valueScaled
            }
            nutrientBatch.clear()
        }

        Files.newBufferedReader(csvPath).use { br ->
            br.readLine() // header
            var line: String?
            while (br.readLine().also { line = it } != null) {
                val row = parsePipeCsvLine(line!!)

                val nevoCode = row[idx(NevoCsvColumns.NEVO_CODE)].trim().toInt()
                val productId = UUID.randomUUID()

                val groupNl = row[idx(NevoCsvColumns.FOODGROUP_NL)].trim()
                val groupId = foodGroupIdByNameNl[groupNl]
                    ?: error("Food group not found (unexpected): $groupNl")

                ProductsTable.insert {
                    it[ProductsTable.id] = productId
                    it[ProductsTable.nevoCode] = nevoCode
                    it[nevoVersion] = row[idx(NevoCsvColumns.VERSION)].trim()
                    it[foodGroupId] = groupId

                    it[nameNl] = row[idx(NevoCsvColumns.NAME_NL)].trim()
                    it[nameEn] = row[idx(NevoCsvColumns.NAME_EN)].trim().ifEmpty { null }
                    it[synonym] = row[idx(NevoCsvColumns.SYNONYM)].trim().ifEmpty { null }
                    it[quantity] = row[idx(NevoCsvColumns.QUANTITY)].trim().ifEmpty { null }
                    it[remark] = row[idx(NevoCsvColumns.REMARK)].trim().ifEmpty { null }
                    it[containsTracesOf] = row[idx(NevoCsvColumns.TRACES)].trim().ifEmpty { null }
                    it[fortifiedWith] = row[idx(NevoCsvColumns.FORTIFIED)].trim().ifEmpty { null }
                }

                productIdByNevoCode[nevoCode] = productId

                // Nutrients for this product:
                for (col in headerInfo.nutrientColumns) {
                    val cell = row.getOrNull(col.headerIndex)
                    val scaled = cell.toNevoScaledOrNull()
                    if (scaled != null) {
                        val nutrientId = nutrientIdByCode[col.code]
                            ?: error("Nutrient code not inserted: ${col.code}")
                        nutrientBatch.add(Triple(productId, nutrientId, scaled))
                    }
                }

                // flush every N rows worth of nutrient records
                if (nutrientBatch.size >= 50_000) flushNutrientBatch()
            }
        }

        flushNutrientBatch()
    }
}
