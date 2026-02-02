import type { User } from "../types";
import { isUserAccount } from "../types/typeGuards";

export function getUserDisplayName(u: User): string {
  return (
    (u.displayName ?? "") ||
    (typeof (u as any).username === "string" ? (u as any).username : "") ||
    (u.uuid ?? "")
  );
}

export function getUserEmail(u: User): string {
    if (isUserAccount(u)) {
        const email = u.email
        }
    return ""
    }
