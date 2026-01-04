import React, { useState, useEffect, useCallback } from "react";
import styles from "./SearchNewFriendsTab.module.css";

import { FriendService } from "../../../api/service/FriendService"
import type { User } from "../types/user/userDomain";

import Input from "../../../components/input/Input"
import { UserCard } from "../../../components/user_cards/UserCard"
import { UserCardIncomingRequest } from "../../../components/user_cards/UserCardIncomingRequest"


const IncomingFriendRequestsTab: React.FC = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null);
    const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);

    const loadUsers = useCallback(async (signal?: AbortSignal) => {
        setLoading(true);
        setError(null);

        try {
          const data = await FriendService.incomingInvites();

          if (signal?.aborted) return;
          setUsers(data);
        } catch (e: any) {
          if (signal?.aborted) return;
          setError(e?.message ?? "Failed to load users");
        } finally {
          if (signal?.aborted) return;
          setLoading(false);
        }
    }, []);

    useEffect(() => {
        const controller = new AbortController();
        loadUsers(controller.signal);
        return () => controller.abort();
    }, [loadUsers]);

    const onAccept = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid)
        setError(null)

        try {
            await FriendService.accept(uuid)
            await loadUsers()
        } catch(e: any) {
            setError(e?.message ?? "Failed to accept invite")
        } finally {
            setActionLoadingId(null)
        }

    }, []);

    const onDecline = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid)
        setError(null)

        try {
            await friendService.decline(uuid)
            await loadUsers()
        } catch(e: any) {
            setError(e?.message ?? "Failed to accept invite")
        } finally {
            setActionLoadingId(null)
        }
    }, []);

    return (
        <div>
            {loading && <p>Loading…</p>}
            <ul className={styles.userCardList}>
                { users.map((user) => (
                    <li key={user.uuid}>
                        <UserCardIncomingRequest
                        user={user}
                        onAccept={onAccept}
                        onDecline={onDecline}
                        loading={actionLoadingId == user.uuid}
                        />
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default IncomingFriendRequestsTab;