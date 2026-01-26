// src/data/ui/userHelpers.ts
import type { User } from "../types";

/**
 * If a User is wrapped in a CONTEXT, unwrap to the actual inner user.
 */
export function unwrapUser(u: User): User {
  return u.type === "CONTEXT" ? u.user : u;
}

/**
 * Best-effort display name for any User variant.
 */
export function getUserDisplayName(u: User): string {
  const base = unwrapUser(u);

  if ("displayName" in base && typeof base.displayName === "string" && base.displayName.length > 0) {
    return base.displayName;
  }
  if ("alias" in base && typeof base.alias === "string" && base.alias.length > 0) {
    return base.alias;
  }
  if ("username" in base && typeof base.username === "string" && base.username.length > 0) {
    return base.username;
  }
  if ("email" in base && typeof base.email === "string" && base.email.length > 0) {
    return base.email;
  }
  return "Unknown";
}

/**
 * Email is only available on PRIVATE/ACCOUNT.
 * Returns "" if not available.
 */
export function getUserEmail(u: User): string {
  const base = unwrapUser(u);
  return "email" in base && typeof base.email === "string" ? base.email : "";
}

/**
 * Image URL is available on most variants, but not guaranteed.
 * Returns null if not available.
 */
export function getUserImageUrl(u: User): string | null {
  const base = unwrapUser(u);

  return "imageUrl" in base && typeof base.imageUrl === "string" && base.imageUrl.length > 0
    ? base.imageUrl
    : null;
}
