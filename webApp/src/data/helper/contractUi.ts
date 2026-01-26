import type { UserContract } from "../types";

export const dateOrNull = (ms?: number | null) => (ms == null ? null : new Date(ms));

export const userDisplayName = (u: UserContract) => {
  switch (u.type) {
    case UserContract.Type.ACCOUNT:
    case UserContract.Type.PRIVATE:
      return u.displayName ?? u.username;
    case UserContract.Type.PUBLIC:
      return u.displayName;
    case UserContract.Type.CONTEXT:
      return userDisplayName(u.user);
    default: {
      const _never: never = u;
      return _never;
    }
  }
};

export const isAccount = (u: UserContract): u is UserContract.ACCOUNT =>
  u.type === UserContract.Type.ACCOUNT;

export const isUserContext = (u: UserContract): u is UserContract.CONTEXT =>
  u.type === UserContract.Type.CONTEXT;