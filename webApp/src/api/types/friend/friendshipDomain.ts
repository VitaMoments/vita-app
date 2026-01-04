import { FriendshipStatus } from "./friendshipStatus"
import { FriendshipDirection } from "./friendshipDirection"
import { User } from "../user/userDomain"

export type Friendship = {
    type = "PENDING" | "ACCEPTED" | "BLOCKED" | "REVOKED";
    uuid: string;
    status: FriendshipStatus;
    direction: FriendshipDirection;
    createdAt: number;
    updatedAt: number;
};