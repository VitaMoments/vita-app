import type { FriendshipDto } from "./friendshipDto";
import type { Friendship } from "./friendshipDomain";

export const mapFriendshipDtoToFriendship = (dto: FriendshipDto): Friendship => {
  switch (dto.type) {
    case "PENDING":
      return {
        type: "PENDING",
        uuid: dto.uuid,
        status: dto.status,
        direction: dto.direction,
        createdAt: dto.createdAt,
        updatedAt: dto.updatedAt,
        otherUserId: dto.otherUserId,
      };

    case "ACCEPTED":
      return {
        type: "ACCEPTED",
        uuid: dto.uuid,
        status: dto.status,
        createdAt: dto.createdAt,
        updatedAt: dto.updatedAt,
        acceptedAt: dto.acceptedAt,
        otherUserId: dto.otherUserId,
      };

    default: {
      const _exhaustive: never = dto;
      throw new Error(`Unknown friendship dto type: ${String((dto as any)?.type)}`);
    }
  }
};
