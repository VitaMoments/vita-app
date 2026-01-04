import React, { useState, useEffect, useCallback } from "react";
import styles from "./SearchNewFriendsTab.module.css";

import { FriendService } from "../../../api/service/FriendService";
import type { User } from "../../../api/types/user/userDomain";

import Input from "../../../components/input/Input";
import { UserCard } from "../../../components/user_cards/UserCard";

const LIMIT = 20;

const SearchNewFriendsTab: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [query, setQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [offset, setOffset] = useState(0);

  const loadUsers = useCallback(async (signal?: AbortSignal) => {
    setLoading(true);
    setError(null);

    try {
      console.log("loadUsers start", { query, offset });

      const data = await FriendService.searchNewFriends({
        query: query.trim() || undefined,
        offset,
        limit: LIMIT,
      });

      console.log("loadUsers data", data);

      if (signal?.aborted) return;
      setUsers(data);
    } catch (e: any) {
      console.error("loadUsers error", e);
      if (signal?.aborted) return;
      setError(e?.message ?? "Failed to load users");
    } finally {
      if (signal?.aborted) return;
      setLoading(false);
    }
  }, [query, offset]);

  useEffect(() => {
    const controller = new AbortController();
    void loadUsers(controller.signal);
    return () => controller.abort();
  }, [loadUsers]);

  const onAddUser = useCallback(async (uuid: string) => {
    setActionLoadingId(uuid);
    setError(null);

    try {
      await FriendService.invite(uuid);
      await loadUsers();
    } catch (e: any) {
      setError(e?.message ?? "Failed to send invite");
    } finally {
      setActionLoadingId(null);
    }
  }, [loadUsers]);

  const onDeleteUser = useCallback((uuid: string) => {
    console.log("onDeleteUser:", uuid);
  }, []);

  return (
    <div>
      <Input
        name="query"
        value={query}
        onChange={(e) => { setQuery(e.target.value); setOffset(0); }}
        placeholder="Search..."
      />

      {loading && <p>Loading…</p>}
      {error && <p>{error}</p>}

      <p>Users size: {users.length}</p>

      <ul className={styles.userCardList}>
        {users.filter(Boolean).map((user) => (
          <li key={user.uuid}>
            <UserCard
              user={user}
              onAddUser={onAddUser}
              onDeleteUser={onDeleteUser}
              loading={actionLoadingId === user.uuid}
            />
          </li>
        ))}
      </ul>
    </div>
  );
};

export default SearchNewFriendsTab;
