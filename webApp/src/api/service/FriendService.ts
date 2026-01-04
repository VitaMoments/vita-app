import api from "../axios";
import type { UserDto } from "../types/user/userDto";
import type { User, PrivateUser } from "../types/user/userDomain";
import { mapPrivateUsersDtoListToPrivateUsers, mapPublicUsersDtoListToPublicUsers, mapUsersDtoListToUsers } from "../types/user/mapUserDtoToUser";

export const FriendService = {
    async searchNewFriends(params: {
        query?: string;
        offset: number;
        limit: number;
    }): Promise<User[]> {
        console.log("test")
        const res = await api.get("/friends/new", { params });
        return mapPublicUsersDtoListToPublicUsers(res.data);
    },

    async searchFriends(params: {
        query?: string;
        offset: number;
        limit: number;
    }): Promise<PrivateUser[]> {
        const res = await api.get<PrivateUserDto[]>("/friends", { params });
        return mapPrivateUsersDtoListToPrivateUsers(res.data);
    },

    async incomingInvites(): Promise<User[]> {
        const res = await api.get("/friends/invites/incoming");
        return mapPublicUsersDtoListToPublicUsers(res.data)
    },

    async outgoingInvites(): Promise<User[]> {
        const res = await api.get("/friends/invites/outgoing");
        return mapPublicUsersDtoListToPublicUsers(res.data)
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

    async decline(friendId: string) {
        const res = await api.post("/friends/decline", { friendId });
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data;
    },
    async remove(friendId: string) {
        const res = await api.post("/friends/delete", { friendId });
        // return mapFriendshipDtoToFriendship(res.data as FriendshipDto);
        return res.data;
    }
}