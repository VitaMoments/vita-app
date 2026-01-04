export type UserRole = "USER" | "MODERATOR" | "ADMIN";

export type UserDto = PublicUserDto | PrivateUserDto | AccountUserDto;

export interface BaseUserDto {
  type: "PUBLIC" | "PRIVATE" | "ACCOUNT";
  uuid: string;
  displayName: string;
  bio: string | null;
  imageUrl: string | null;
}

export interface PublicUserDto extends BaseUserDto {
  type: "PUBLIC";
}

export interface PrivateUserDto extends BaseUserDto {
  type: "PRIVATE";
  username: string;
  email: string;
  role: UserRole;
}

export interface AccountUserDto extends BaseUserDto {
  type: "ACCOUNT";
  username: string;
  email: string;
  alias: string | null;
  role: UserRole;
  createdAt: number;
  updatedAt: number;
  deletedAt: number | null;
}
