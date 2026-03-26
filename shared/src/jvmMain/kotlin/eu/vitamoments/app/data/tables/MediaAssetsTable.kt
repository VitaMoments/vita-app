package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.MediaPurposeType
import eu.vitamoments.app.data.models.enums.MediaReferenceType
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.tables.base.BaseUUIDTable

object MediaAssetsTable : BaseUUIDTable(name = "media_assets") {

    val referenceId = uuid(name = "reference_id")
    val referenceType = enumerationByName<MediaReferenceType>(name = "reference_type", length = 30)

    val purpose = enumerationByName<MediaPurposeType>(name = "purpose", length = 15)
    val privacy = enumerationByName<PrivacyStatus>(name = "privacy", length = 15)

    val originalFileName = varchar(name = "original_file_name", length = 255).nullable()
    val storedFileName = varchar(name = "stored_file_name", length = 255)
    val objectKey = varchar(name = "object_key", length =1000).uniqueIndex()
    val contentType = varchar(name = "content_type", length =100)
    val sizeBytes = long(name = "size_bytes")

    val width = integer(name = "width").nullable()
    val height = integer(name = "height").nullable()

    init {
        index(false, referenceId, referenceType)
        index(false, referenceType, purpose)
        index(false, privacy)
    }
}