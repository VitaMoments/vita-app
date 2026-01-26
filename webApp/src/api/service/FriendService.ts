import api from "../axios";
import { UserContract } from "../types";          // <-- géén import type, want je gebruikt UserContract.Type mogelijk in guards
import type { PagedResult } from "../types";       // <-- type-only

export type FriendSearchParams = {
  query?: string;
  offset?: number;
  limit?: number;
};

function normalizeParams(params: FriendSearchParams) {
  return {
    query: params.query,
    offset: params.offset ?? 0,
    limit: params.limit ?? 20,
  };
}

export const FriendService = {
  /**
   * Nieuwe vrienden zoeken (meestal PUBLIC users)
   * Backend: PagedResult<UserContract>
   */
  async searchNewFriends(params: FriendSearchParams = {}): Promise<PagedResult<UserContract>> {
    const res = await api.get<PagedResult<UserContract>>("/friends/new", {
      params: normalizeParams(params),
    });

    // ✅ contract-first: geen mapping
    return res.data;
  },

  /**
   * Je bestaande vrienden (waarschijnlijk CONTEXT, dus inclusief friendship info)
   * Backend: PagedResult<UserContract> (met type CONTEXT items)
   */
  async searchFriends(params: FriendSearchParams = {}): Promise<PagedResult<UserContract>> {
    const res = await api.get<PagedResult<UserContract>>("/friends", {
      params: normalizeParams(params),
    });

    return res.data;
  },

  /**
   * Friend invites/requests
   * Backend: PagedResult<UserContract> (met CONTEXT items)
   */
  async friendRequests(params: FriendSearchParams = {}): Promise<PagedResult<UserContract>> {
    const res = await api.get<PagedResult<UserContract>>("/friends/invites", {
      params: normalizeParams(params),
    });

    return res.data;
  },

  // Actions — tip: typ deze zodra je endpoint dat terugstuurt (bijv FriendshipDto/Contract)
  async invite(friendId: string): Promise<unknown> {
    const res = await api.post("/friends/invite", { friendId });
    return res.data;
  },

  async accept(friendId: string): Promise<unknown> {
    const res = await api.post("/friends/accept", { friendId });
    return res.data;
  },

  async reject(friendId: string): Promise<unknown> {
    const res = await api.post("/friends/reject", { friendId });
    return res.data;
  },

  async revoke(friendId: string): Promise<unknown> {
    const res = await api.post("/friends/revoke", { friendId });
    return res.data;
  },
};