import React, { useCallback, useEffect, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/Card"

import { FriendService } from "../../../api/service/FriendService";
import type { User } from "../../../api/types/user/userDomain";

import { PagedList } from "../../../components/pagination/PagedList";

import { MdCheck, MdClose } from "react-icons/md";

const LIMIT = 20;
const TAB_KEY = "friends-invites";

type Props = { isActive: boolean };

const FriendRequestsTab: React.FC<Props> = ({ isActive }) => {
    const [query, setQuery] = useState("");
    const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
    const [reloadToken, setReloadToken] = useState(0);
    const [disabled, setDisabled] = useState(false)

    useEffect(() => {
        if (isActive) setReloadToken((x) => x + 1);
    }, [isActive]);

  const fetchPage = useCallback(
    ({ limit, offset, signal }: { limit: number; offset: number; signal?: AbortSignal }) =>
      FriendService.friendRequests({
        query: query.trim() || undefined,
        limit,
        offset,
        signal
      }),
    [query]
  );

    const acceptUser = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid);
        try {
            await FriendService.accept(uuid);
            setReloadToken(x => x + 1);
        } finally {
            setActionLoadingId(null);
        }
    }, []);

    const rejectUser = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid);
        try {
            await FriendService.reject(uuid);
            setReloadToken(x => x + 1);
        } finally {
            setActionLoadingId(null);
        }
    }, []);

    const revokeUser = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid);
        try {
            await FriendService.revoke(uuid);
            setReloadToken(x => x + 1);
        } finally {
            setActionLoadingId(null);
        }
    }, []);

  const listInstanceKey = `${TAB_KEY}:${query}:${reloadToken}`;

  return (
    <PagedList<User>
      limit={LIMIT}
      resetKey={listInstanceKey}
      fetchPage={fetchPage}
      listClassName={styles.userCardList}
      empty={<p className={styles.emptyText}>No users found</p>}
      getKey={(u) => `${TAB_KEY}:${u.friendship?.uuid ?? u.user.uuid}` }
      controls={({ refresh }) => (
        <Input
          name="query"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
        />
      )}
      renderItem={(user) => {
           const date = new Date(user.friendship?.createdAt ?? 0);
           const isIncoming = user.friendship.direction === "INCOMING"
          return (
            <Card>
                <div className={styles.cardContent}>
                    {user.user.imageUrl ? (
                    <img src={user.imageUrl} alt="profile image" className={styles.avatar} />
                    ) : (
                    <div className={styles.avatar} />
                    )}

                    <div className={styles.userInfo}>
                      <span className={styles.displayName}>{user.user.displayName}</span>
                      {user.user.bio ? <span className={styles.bio}>{user.user.bio}</span> : null}
                    </div>

                    <div className={styles.actionButtonBar}>
                        {isIncoming ? (
                            <>
                                <button
                                    type="button"
                                    className={styles.declineButton}
                                    disabled={disabled || actionLoadingId === user.uuid}
                                    onClick={() => rejectUser(user.user.uuid)}
                                    aria-busy={actionLoadingId === user.uuid}
                                    aria-label="reject user">
                                    <MdClose />
                                </button>
                                <button
                                    type="button"
                                    className={styles.acceptButton}
                                    disabled={disabled || actionLoadingId === user.uuid}
                                    onClick={() => acceptUser(user.user.uuid)}
                                    aria-busy={actionLoadingId === user.uuid}
                                    aria-label="Add friend">
                                    <MdCheck />
                                </button>
                            </>
                            ) : (
                                <button
                                  type="button"
                                  className={styles.declineButton}
                                  disabled={disabled || actionLoadingId === user.uuid}
                                  onClick={() => revokeUser(user.user.uuid)}
                                  aria-busy={actionLoadingId === user.uuid}
                                  aria-label="Add friend"
                                >
                                  <MdClose />
                                </button>
                            )}
                    </div>
                </div>
            </Card>
          )}
      }
    />
  );
};

export default FriendRequestsTab;