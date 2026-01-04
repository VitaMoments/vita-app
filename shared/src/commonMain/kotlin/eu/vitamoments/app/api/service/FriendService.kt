@file:OptIn(ExperimentalUuidApi::class)

package eu.vitamoments.app.api.service

import eu.vitamoments.app.data.enums.FriendshipStatus
import eu.vitamoments.app.data.models.dto.user.FriendInviteDto
import io.ktor.client.statement.HttpResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface FriendService {
    suspend fun searchNewFriends(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun searchFriends(query: String?, limit: Int = 20, offset: Int = 0) : HttpResponse
    suspend fun invite(
        body: FriendInviteDto
    ) : HttpResponse
    suspend fun setFriendshipStatus(
        body: FriendInviteDto
    ) : HttpResponse
    suspend fun deleteFriendship(
        body: FriendInviteDto
    ) : HttpResponse
}