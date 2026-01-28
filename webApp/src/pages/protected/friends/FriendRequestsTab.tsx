import React, { useCallback, useEffect, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/Card";

import { FriendService } from "../../../api/service/FriendService";
import type { UserWithContext } from "../../../data/types";
import { getUserDisplayName } from "../../../data/ui/userHelpers";

import { PagedList } from "../../../components/pagination/PagedList";

import { MdCheck, MdClose } from "react-icons/md";

const LIMIT = 20;
const TAB_KEY = "friends-invites";

type Props = { isActive: boolean };

const FriendRequestsTab: React.FC<Props> = ({ isActive }) => {
  const [query, setQuery] = useState("");
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  useEffect(() => {
    if (isActive) setReloadToken((x) => x + 1);
  }, [isActive]);

  const fetchPage = useCallback(
    ({
      limit,
      offset,
      signal,
    }: {
      limit: number;
      offset: number;
      signal?: AbortSignal;
    }) =>
      // ✅ signal los meegeven (niet in params object)
      FriendService.friendRequests(
        {
          query: query.trim() || undefined,
          limit,
          offset,
        },
        signal
      ),
    [query]
  );

  const acceptUser = useCallback(async (friendId: string) => {
    setActionLoadingId(friendId);
    try {
      await FriendService.accept(friendId);
      setReloadToken((x) => x + 1);
    } finally {
      setActionLoadingId(null);
    }
  }, []);

  const rejectUser = useCallback(async (friendId: string) => {
    setActionLoadingId(friendId);
    try {
      await FriendService.reject(friendId);
      setReloadToken((x) => x + 1);
    } finally {
      setActionLoadingId(null);
    }
  }, []);

  const revokeUser = useCallback(async (friendId: string) => {
    setActionLoadingId(friendId);
    try {
      await FriendService.revoke(friendId);
      setReloadToken((x) => x + 1);
    } finally {
      setActionLoadingId(null);
    }
  }, []);

  const listInstanceKey = `${TAB_KEY}:${query}:${reloadToken}`;

  return (
    <PagedList<UserWithContext>
      limit={LIMIT}
      resetKey={listInstanceKey}
      fetchPage={fetchPage}
      listClassName={styles.userCardList}
      empty={<p className={styles.emptyText}>No users found</p>}
      getKey={(ctx) => `${TAB_KEY}:${ctx.friendship?.uuid ?? ctx.user.uuid}`}
      controls={() => (
        <Input
          name="query"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
        />
      )}
      renderItem={(ctx) => {
        const u = ctx.user;
        const f = ctx.friendship;

        // Als er (om wat voor reden) geen friendship is, render dan zonder buttons
        if (!f || f.type !== "PENDING") {
          return (
            <Card>
              <div className={styles.cardContent}>
                {u.imageUrl ? (
                  <img src={u.imageUrl} alt="" className={styles.avatar} />
                ) : (
                  <div className={styles.avatar} />
                )}

                <div className={styles.userInfo}>
                  <span className={styles.displayName}>{getUserDisplayName(u)}</span>
                  {u.bio ? <span className={styles.bio}>{u.bio}</span> : null}
                </div>
              </div>
            </Card>
          );
        }

        const isIncoming = f.direction === "INCOMING";
        const friendId = u.uuid;

        return (
          <Card>
            <div className={styles.cardContent}>
              {u.imageUrl ? (
                <img src={u.imageUrl} alt="" className={styles.avatar} />
              ) : (
                <div className={styles.avatar} />
              )}

              <div className={styles.userInfo}>
                <span className={styles.displayName}>{getUserDisplayName(u)}</span>
                {u.bio ? <span className={styles.bio}>{u.bio}</span> : null}
              </div>

              <div className={styles.actionButtonBar}>
                {isIncoming ? (
                  <>
                    <button
                      type="button"
                      className={styles.declineButton}
                      disabled={actionLoadingId === friendId}
                      onClick={() => rejectUser(friendId)}
                      aria-busy={actionLoadingId === friendId}
                      aria-label="Reject user"
                    >
                      <MdClose />
                    </button>

                    <button
                      type="button"
                      className={styles.acceptButton}
                      disabled={actionLoadingId === friendId}
                      onClick={() => acceptUser(friendId)}
                      aria-busy={actionLoadingId === friendId}
                      aria-label="Accept user"
                    >
                      <MdCheck />
                    </button>
                  </>
                ) : (
                  <button
                    type="button"
                    className={styles.declineButton}
                    disabled={actionLoadingId === friendId}
                    onClick={() => revokeUser(friendId)}
                    aria-busy={actionLoadingId === friendId}
                    aria-label="Revoke invite"
                  >
                    <MdClose />
                  </button>
                )}
              </div>
            </div>
          </Card>
        );
      }}
    />
  );
};

export default FriendRequestsTab;
