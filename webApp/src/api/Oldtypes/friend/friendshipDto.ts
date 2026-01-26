import type { FriendshipStatus } from "./friendshipStatus";
import type { FriendshipDirection } from "./friendshipDirection";

export type FriendshipDto = PendingFriendshipDto | AcceptedFriendshipDto;

interface BaseFriendshipDto {
  uuid: string;
  status: FriendshipStatus; // redundant maar ok
  createdAt: number;
  updatedAt: number;
  otherUserId: string; // jouw otherUserId in backend
}

export interface PendingFriendshipDto extends BaseFriendshipDto {
  type: "PENDING";
  direction: FriendshipDirection; // INCOMING | OUTGOING
}

export interface AcceptedFriendshipDto extends BaseFriendshipDto {
  type: "ACCEPTED";
  acceptedAt: number;
}
