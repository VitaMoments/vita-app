export interface UserDto {
  uuid: string;
  email: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt: LocalDateTime | null;
  imageUrl: string | null;
}

export interface PublicUserDto {
    uuid: string;
    email: string;
    imageUrl: string | null;
    }

export interface User {
    uuid: string;
    email: String;
    imageUrl: String | null;
    }

export const mapPublicUserDtoToUser = (dto: PublicUserDto): User => {
    return {
        uuid: dto.uuid,
        email: dto.email,
        imageUrl: dto.imageUrl
        }
    }

export const mapUserDtoToUser = (dto: UserDto): User => {
    return {
        uuid: dto.uuid,
        email: dto.email,
        imageUrl: dto.imageUrl
        }
    }