import React, { useCallback, useEffect, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/card";

import { FriendService } from "../../../api/service/FriendService";
import type { User } from "../../../api/types/user/userDomain";

import { PagedList } from "../../../components/pagination/PagedList";

const LIMIT = 20;
const TAB_KEY = "friends";

type Props = { isActive: boolean }

const FriendsTab: React.FC<Props> = ({ isActive }) => {
    const [query, setQuery] = useState("");
    const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
    const [reloadToken, setReloadToken] = useState(0);

     useEffect(() => {
            if (isActive) setReloadToken((x) => x + 1);
        }, [isActive]);

    const fetchPage = useCallback(
        ({limit, offset, signal}: {limit: number; offset: number; signal?: AbortSignal}) =>
        FriendService.searchFriends({
            query: query.trim() || undefined,
            limit,
            offset,
            signal
            }),
        [query]
        );

    const listInstanceKey = `${TAB_KEY}:${query}:${reloadToken}`;

    return (
        <PagedList<User>
        limit={LIMIT}
        resetKey={listInstanceKey}
        fetchPage={fetchPage}
        listClassName={styles.userCardList}
        empty={<p className={styles.emptyText}>No users found</p>}
        getKey={(u) => `${TAB_KEY}:${u.friendship?.uuid ?? u.user.uuid}`}
        controls={({ refresh }) => (
            <Input
            name="query"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search..."
            // TIP: als je na invite wil herladen: roep refresh() aan
            // dit is bv. handig als je een 'Refresh' knop wil
            />
        )}
        renderItem={(user) => (
            <Card>
                <div className={styles.cardContent}>
                    {user.imageUrl ? (
                    <img src={user.user.imageUrl} alt="profile image" className={styles.avatar} />
                    ) : (
                    <div className={styles.avatar} />
                    )}

                    <div className={styles.userInfo}>
                    <span className={styles.displayName}>{user.user.displayName}</span>
                    {user.user.bio ? <span className={styles.bio}>{user.user.bio}</span> : null}
                    </div>
                </div>
            </Card>
          )}
        />
        )
    }
export default FriendsTab;