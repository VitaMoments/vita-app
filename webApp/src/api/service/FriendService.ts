import api from "../axios";
import type { PagedResult, User, UserWithContext } from "../../data/types";

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
   * Nieuwe vrienden zoeken
   * Backend: PagedResult<User.PUBLIC>
   */
  async searchNewFriends(
    params: FriendSearchParams = {},
    signal?: AbortSignal
  ): Promise<PagedResult<User.PUBLIC>> {
    const res = await api.get<PagedResult<User.PUBLIC>>("/friends/new", {
      params: normalizeParams(params),
      signal,
    });
    return res.data;
  },

  /**
   * Je bestaande vrienden
   * Jij zegt: Backend: PagedResult<UserWithContext>
   */
  async searchFriends(
    params: FriendSearchParams = {},
    signal?: AbortSignal
  ): Promise<PagedResult<UserWithContext>> {
    const res = await api.get<PagedResult<UserWithContext>>("/friends", {
      params: normalizeParams(params),
      signal,
    });
    return res.data;
  },

  /**
   * Friend invites/requests
   * Jij zegt: Backend: PagedResult<User.CONTEXT>
   */
  async friendRequests(
    params: FriendSearchParams = {},
    signal?: AbortSignal
  ): Promise<PagedResult<UserWithContext>> {
    const res = await api.get<PagedResult<UserWithContext>>("/friends/invites", {
      params: normalizeParams(params),
      signal,
    });
    return res.data;
  },

  // Actions (laat even unknown, of typ later op Friendship/UserWithContext/etc)
  async invite(friendId: string, signal?: AbortSignal): Promise<unknown> {
    const res = await api.post("/friends/invite", { friendId }, { signal });
    return res.data;
  },

  async accept(friendId: string, signal?: AbortSignal): Promise<unknown> {
    const res = await api.post("/friends/accept", { friendId }, { signal });
    return res.data;
  },

  async reject(friendId: string, signal?: AbortSignal): Promise<unknown> {
    const res = await api.post("/friends/reject", { friendId }, { signal });
    return res.data;
  },

  async revoke(friendId: string, signal?: AbortSignal): Promise<unknown> {
    const res = await api.post("/friends/revoke", { friendId }, { signal });
    return res.data;
  },
};