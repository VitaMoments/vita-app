import type { UserDto, PublicUserDto, PrivateUserDto, AccountUserDto } from "./userDto";
import type { User, PublicUser, PrivateUser, AccountUser } from "./userDomain";

export const mapUserDtoToUser = (dto: UserDto): User => {
  switch (dto.type) {
    case "PUBLIC":
      return mapPublicUserDtoToPublicUser(dto);
    case "PRIVATE":
      return mapPrivateUserDtoToPrivateUser(dto);
    case "ACCOUNT":
      return mapAccountUserDtoToAccountUser(dto);
    default: {
      // zorgt dat je direct ziet wat er mis binnenkomt
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


export const mapPrivateUsersDtoListToPrivateUsers = (dtos: PrivateUserDto[]): PrivateUser[] =>
  dtos.map(mapPrivateUserDtoToPrivateUser);

export const mapPublicUsersDtoListToPublicUsers = (dtos: PublicUserDto[]): PublicUser[] =>
  dtos.map(mapPublicUserDtoToPublicUser);

export const mapUsersDtoListToUsers = (dtos: UserDto[]): User[] => dtos.map(mapUserDtoToUser);
