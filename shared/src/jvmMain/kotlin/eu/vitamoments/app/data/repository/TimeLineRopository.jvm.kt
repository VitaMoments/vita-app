@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)

package eu.vitamoments.app.data.repository

import kotlinx.serialization.json.JsonObject
import eu.vitamoments.app.data.entities.TimeLinePostEntity
import eu.vitamoments.app.data.entities.UserEntity
import eu.vitamoments.app.data.mapper.enitity.toDomain
import eu.vitamoments.app.data.models.domain.message.TimeLinePost
import eu.vitamoments.app.data.tables.UsersTable
import eu.vitamoments.app.dbHelpers.dbQuery
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
        val postsEntities = TimeLinePostEntity.all()
        RepositoryResponse.Success(postsEntities.map { it.toDomain() })
    }

}