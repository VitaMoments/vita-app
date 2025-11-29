@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package nl.fbdevelopment.healthyplatform.data.repository

import kotlinx.serialization.json.JsonObject
import nl.fbdevelopment.healthyplatform.data.entities.TimeLinePostEntity
import nl.fbdevelopment.healthyplatform.data.entities.UserEntity
import nl.fbdevelopment.healthyplatform.data.mapper.enitity.toDomain
import nl.fbdevelopment.healthyplatform.data.models.domain.message.TimeLinePost
import nl.fbdevelopment.healthyplatform.data.tables.UsersTable
import nl.fbdevelopment.healthyplatform.dbHelpers.dbQuery
import org.jetbrains.exposed.v1.core.eq
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

class JVMTimeLineRepository() : TimeLineRepository {
    override suspend fun createPost(
        userId: Uuid,
        content: JsonObject
    ): RepositoryResponse<TimeLinePost> = dbQuery {
        val userEntity = UserEntity.find{ UsersTable.id eq userId.toJavaUuid() }.firstOrNull() ?: return@dbQuery RepositoryResponse.Error.InvalidData("UserId", "This userId is not registered as User")

        val entity = TimeLinePostEntity.new {
            this.createdBy = userEntity
            this.content = content
        }

        RepositoryResponse.Success(
            body = entity.toDomain()
        )
    }

    override suspend fun getTimeLine(
        userId: Uuid,
        limit: Int,
        offset: Long
    ): RepositoryResponse<List<TimeLinePost>> = dbQuery{
        RepositoryResponse.Success(emptyList())
    }

}