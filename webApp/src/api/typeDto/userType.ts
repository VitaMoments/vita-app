export interface UserDto {
  uuid: string;
  email: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt: LocalDateTime | null;
  imageUrl: string | null;
}

export interface User {
    email: String;
    imageUrl: String | null;
    }

export const mapUserDtoToUser = (userDto: UserDto): User => {
    return {
        email: userDto.email,
        imageUrl: userDto.imageUrl
        }
    }