import type { FriendshipStatus } from "./friendshipStatus";
import type { FriendshipDirection } from "./friendshipDirection";

export type Friendship = PendingFriendship | AcceptedFriendship;

interface BaseFriendship {
  uuid: string;
  status: FriendshipStatus;
  createdAt: number;
  updatedAt: number;
  otherUserId: string; // in domain is dit duidelijker dan friendId
}

export type PendingFriendship = BaseFriendship & {
  type: "PENDING";
  direction: FriendshipDirection;
};

export type AcceptedFriendship = BaseFriendship & {
  type: "ACCEPTED";
  acceptedAt: number;
};
