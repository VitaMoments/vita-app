import React, { useState, useEffect, useCallback } from "react";
import Input from "../../../components/input/Input"

import { FriendService } from "../../../api/service/FriendService"
import type { User } from "../../../api/types/user/userDomain";
import { UserCard } from "../../../components/user_cards/UserCard"
import styles from "./SearchNewFriendsTab.module.css";

const FriendsTab: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [query, setQuery] = useState("")
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null);
    const [offset, setOffset] = useState(0)

    const loadUsers = useCallback(async (signal?: AbortSignal) => {
        setLoading(true)
        setError(null)

        try {
            const data = await FriendService.searchFriends({
                query: query.trim() || undefined,
                offset: offset,
                limit: 20
                });
                if (signal?.aborted) return;
                setUsers(data)
            } catch(e: any) {
                if (signal?.aborted) return;
                setError(e?.message ?? "Failed to load friends");
            } finally {
                if (signal?.aborted) return;
                setLoading(false);
            }
        }, [query, offset]);

    useEffect(() => {
        const controller = new AbortController();
        loadUsers(controller.signal)
        return () => controller.abort();
        }, [loadUsers]);

    return (
        <div>
            <Input
                name="query"
                value={query}
                onChange={(e) => { setQuery(e.target.value); setOffset(0); }}
                placeholder="Search..." />

            {loading && <p>Loading…</p>}
            <ul className={styles.userCardList}>
                  {users.filter(Boolean).map((user) => (
                    <li key={user.uuid}>
                      <UserCard user={user} />
                    </li>
                  ))}
            </ul>
        </div>
    );
}

export default FriendsTab;