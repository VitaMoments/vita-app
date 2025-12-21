export interface UserDto {
  uuid: string;
  username: string;
  email: string;
  alias: string | null;
  bio: string | null;
  role: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt: LocalDateTime | null;
  imageUrl: string | null;
}

export interface PublicUserDto {
    uuid: string;
    username: string;
    email: string;
    alias: string | null;
    bio: string | null;
    role: string;
    imageUrl: string | null;
    }

export interface User {
    username: string;
    uuid: string;
    email: string;
    alias: string | null;
    bio: string | null;
    role: string;
    imageUrl: string | null;
    }

export const mapPublicUserDtoToUser = (dto: PublicUserDto): User => {
    return {
        uuid: dto.uuid,
        username: dto.username,
        email: dto.email,
        alias: dto.alias,
        bio: dto.bio,
        role: dto.role,
        imageUrl: dto.imageUrl
        }
    }

export const mapUserDtoToUser = (dto: UserDto): User => {
    return {
        uuid: dto.uuid,
        username: dto.username,
        email: dto.email,
        alias: dto.alias,
        bio: dto.bio,
        role: dto.role,
        imageUrl: dto.imageUrl
        }
    }