import type { UserRole } from "./userDto";
import type { Friendship } from "../friend/friendshipdomain"

export type PublicUser = {
  type: "PUBLIC";
  uuid: string;
  displayName: string;
  bio: string | null;
  imageUrl: string | null;
};

export type PrivateUser = {
  type: "PRIVATE";
  uuid: string;
  displayName: string;
  bio: string | null;
  imageUrl: string | null;
  username: string;
  email: string;
  role: UserRole;
};

export type AccountUser = {
  type: "ACCOUNT";
  uuid: string;
  displayName: string;
  bio: string | null;
  imageUrl: string | null;
  username: string;
  email: string;
  role: UserRole;
  alias: string | null;
  createdAt: number;
  updatedAt: number;
  deletedAt: number | null;
};

export type UserWithContext = {
  type: "CONTEXT";
  user: PublicUser | PrivateUser | AccountUser;
  friendship: Friendship | null;
};

export type User = PublicUser | PrivateUser | AccountUser | UserWithContext;
