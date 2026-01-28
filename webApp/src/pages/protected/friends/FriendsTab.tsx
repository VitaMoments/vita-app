import React, { useCallback, useEffect, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/Card";

import { FriendService } from "../../../api/service/FriendService";
import type { UserWithContext } from "../../../data/types";
import { getUserDisplayName } from "../../../data/ui/userHelpers";

import { PagedList } from "../../../components/pagination/PagedList";

const LIMIT = 20;
const TAB_KEY = "friends";

type Props = { isActive: boolean };

const FriendsTab: React.FC<Props> = ({ isActive }) => {
  const [query, setQuery] = useState("");
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
      FriendService.searchFriends(
        {
          query: query.trim() || undefined,
          limit,
          offset,
        },
        signal
      ),
    [query]
  );

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

        return (
          <Card>
            <div className={styles.cardContent}>
              {u.imageUrl ? (
                <img src={u.imageUrl} alt="" className={styles.avatar} />
              ) : (
                <div className={styles.avatar} />
              )}

              <div className={styles.userInfo}>
                <span className={styles.displayName}>
                  {getUserDisplayName(u)}
                </span>
                {u.bio ? <span className={styles.bio}>{u.bio}</span> : null}
              </div>
            </div>
          </Card>
        );
      }}
    />
  );
};

export default FriendsTab;
