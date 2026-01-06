import api from "../axios";
import type { UserDto } from "../types/user/userDto";
import type { User, PrivateUser } from "../types/user/userDomain";
import type { Page } from "../types/pagedResult/page"
import { mapPrivateUsersDtoListToPrivateUsers, mapPublicUsersDtoListToPublicUsers, mapUsersDtoListToUsers, mapUserWithContextDtoToUserWithContext } from "../types/user/mapUserDtoToUser";

export const FriendService = {
      async searchNewFriends(params: {
        query?: string;
        offset: number;
        limit: number;
      }): Promise<{ items: User[]; limit: number; offset: number; total: number; hasMore: boolean; nextOffset: number | null }> {
        const res = await api.get<PagedResultDto<UserDto>>("/friends/new", {
          params: {
            query: params.query,
            limit: params.limit,
            offset: params.offset,
          },
        });

        return {
          ...res.data,
          items: mapPublicUsersDtoListToPublicUsers(res.data.items),
        };
      },

    async searchFriends(params: {
        query?: string;
        offset: number;
        limit: number;
    }): Promise<{ items: User[]; limit: number; offset: number; total: number; hasMore: boolean; nextOffset: number | null }> {
        const res = await api.get<PagedResultDto<UserWithContextDto>>("/friends", { params });

        console.log(res.data)
        return {
            ...res.data,
            items: res.data.items.map(mapUserWithContextDtoToUserWithContext),
        };
    },

    async friendRequests(params: {
        query?: string;
        offset: number;
        limit: number;
    }): Promise<{
        items: User[];
        limit: number;
        offset: number;
        total: number;
        hasMore: boolean;
        nextOffset: number | null;
    }> {
        const res = await api.get<PagedResultDto<UserWithContextDto>>("/friends/invites", { params });

        return {
        ...res.data,
        items: res.data.items.map(mapUserWithContextDtoToUserWithContext),
        };
    },

// Actions
    async invite(friendId: string) {
        const res = await api.post("/friends/invite", { friendId });
        // If backend returns FriendshipDto -> map it here and return Friendship
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data; // keep as-is until your DTO typing is set
    },

    async accept(friendId: string) {
        const res = await api.post("/friends/accept", { friendId });
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data;
    },

    async reject(friendId: string) {
        const res = await api.post("/friends/reject", { friendId });
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data;
    },
    async revoke(friendId: string) {
        const res = await api.post("/friends/revoke", { friendId });
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data;
    }
}