import type { UserDto, PublicUserDto, PrivateUserDto, AccountUserDto, UserWithContextDto } from "./userDto";
import type { User, PublicUser, PrivateUser, AccountUser, UserWithContext } from "./userDomain";
import { mapFriendshipDtoToFriendship } from "../friend/mapFriendshipDtoToFriendship";

export const mapUserDtoToUser = (dto: UserDto): User => {
  switch (dto.type) {
    case "PUBLIC":
        return mapPublicUserDtoToPublicUser(dto);
    case "PRIVATE":
        return mapPrivateUserDtoToPrivateUser(dto);
    case "ACCOUNT":
        return mapAccountUserDtoToAccountUser(dto);
    case "CONTEXT":
        return mapUserWithContextDtoToUserWithContext(dto)
    default: {
        const _exhaustive: never = dto;
        throw new Error(`Unknown user dto type: ${(dto as any)?.type}`);
    }
  }
};


export const mapPublicUserDtoToPublicUser = (dto: PublicUserDto): PublicUser => ({
  type: "PUBLIC",
  uuid: dto.uuid,
  displayName: dto.displayName,
  bio: dto.bio,
  imageUrl: dto.imageUrl,
});

export const mapPrivateUserDtoToPrivateUser = (dto: PrivateUserDto): PrivateUser => ({
  type: "PRIVATE",
  uuid: dto.uuid,
  displayName: dto.displayName,
  bio: dto.bio,
  imageUrl: dto.imageUrl,
  username: dto.username,
  email: dto.email,
  role: dto.role,
});

export const mapAccountUserDtoToAccountUser = (dto: AccountUserDto): AccountUser => ({
  type: "ACCOUNT",
  uuid: dto.uuid,
  displayName: dto.displayName,
  bio: dto.bio,
  imageUrl: dto.imageUrl,
  username: dto.username,
  email: dto.email,
  role: dto.role,
  alias: dto.alias,
  createdAt: dto.createdAt,
  updatedAt: dto.updatedAt,
  deletedAt: dto.deletedAt,
});

export const mapUserWithContextDtoToUserWithContext = (
  dto: UserWithContextDto
): UserWithContext => ({
  type: "CONTEXT",
  user: mapUserDtoToUser(dto.user),
  friendship: dto.friendship ? mapFriendshipDtoToFriendship(dto.friendship) : null,
});



export const mapPrivateUsersDtoListToPrivateUsers = (dtos: PrivateUserDto[]): PrivateUser[] =>
  dtos.map(mapPrivateUserDtoToPrivateUser);

export const mapPublicUsersDtoListToPublicUsers = (dtos: PublicUserDto[]): PublicUser[] =>
  dtos.map(mapPublicUserDtoToPublicUser);

export const mapUsersDtoListToUsers = (dtos: UserDto[]): User[] => dtos.map(mapUserDtoToUser);
