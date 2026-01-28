import React, { useCallback, useEffect, useMemo, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/Card";

import { FriendService } from "../../../api/service/FriendService";
import { User } from "../../../data/types";
import { getUserDisplayName } from "../../../data/ui/userHelpers";

import { PagedList } from "../../../components/pagination/PagedList";

import { MdPersonAdd } from "react-icons/md";

const LIMIT = 20;
const TAB_KEY = "friends-new";

type Props = { isActive: boolean };

type PublicUser = User.PUBLIC;

const isPublicUser = (u: User): u is PublicUser => u.type === User.Type.PUBLIC;

const SearchNewFriendsTab: React.FC<Props> = ({ isActive }) => {
  const [query, setQuery] = useState("");
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [reloadToken, setReloadToken] = useState(0);

  useEffect(() => {
    if (isActive) setReloadToken((x) => x + 1);
  }, [isActive]);

  const fetchPage = useCallback(
    async ({
      limit,
      offset,
      signal,
    }: {
      limit: number;
      offset: number;
      signal?: AbortSignal;
    }) => {
      // We halen op als "User" (union) en filteren naar PUBLIC
      const res = await FriendService.searchNewFriends(
        {
          query: query.trim() || undefined,
          limit,
          offset,
        },
        signal
      );

      // res.items kan union bevatten; UI wil PUBLIC-only
      const publicItems = res.items.filter(isPublicUser);

      return {
        ...res,
        items: publicItems,
      };
    },
    [query]
  );

  const inviteUser = useCallback(async (uuid: string) => {
    setActionLoadingId(uuid);
    try {
      await FriendService.invite(uuid);
      setReloadToken((x) => x + 1);
    } finally {
      setActionLoadingId(null);
    }
  }, []);

  const listInstanceKey = useMemo(
    () => `${TAB_KEY}:${query}:${reloadToken}`,
    [query, reloadToken]
  );

  return (
    <PagedList<PublicUser>
      limit={LIMIT}
      resetKey={listInstanceKey}
      fetchPage={fetchPage}
      listClassName={styles.userCardList}
      empty={<p className={styles.emptyText}>No users found</p>}
      getKey={(u) => `${TAB_KEY}:${u.uuid}`}
      controls={() => (
        <Input
          name="query"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search..."
        />
      )}
      renderItem={(u) => (
        <Card>
          <div className={styles.cardContent}>
            {u.imageUrl ? (
              <img src={u.imageUrl} alt="" className={styles.avatar} />
            ) : (
              <div className={styles.avatar} />
            )}

            <div className={styles.userInfo}>
              <span className={styles.displayName}>{u.displayName}</span>
              {u.bio ? <span className={styles.bio}>{u.bio}</span> : null}
            </div>

            <div className={styles.actionButtonBar}>
              <button
                type="button"
                disabled={actionLoadingId === u.uuid}
                onClick={() => inviteUser(u.uuid)}
                aria-busy={actionLoadingId === u.uuid}
                aria-label="Add friend"
              >
                <MdPersonAdd className={styles.colorPrimary} />
              </button>
            </div>
          </div>
        </Card>
      )}
    />
  );
};

export default SearchNewFriendsTab;
