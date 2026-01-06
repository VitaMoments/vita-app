import React, { useCallback, useEffect, useState } from "react";
import styles from "./FriendsPage.module.css";

import Input from "../../../components/input/Input";
import { Card } from "../../../components/card/card"

import { FriendService } from "../../../api/service/FriendService";
import type { User } from "../../../api/types/user/userDomain";

import { PagedList } from "../../../components/pagination/PagedList";

import { MdPersonAdd, MdPersonRemove } from "react-icons/md";


const LIMIT = 20;
const TAB_KEY = "friends-new";

type Props = { isActive: boolean }

const SearchNewFriendsTab: React.FC<Props> = ({ isActive }) => {
    const [query, setQuery] = useState("");
    const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
    const [reloadToken, setReloadToken] = useState(0);

    useEffect(() => {
        if (isActive) setReloadToken((x) => x + 1);
    }, [isActive]);

    const fetchPage = useCallback(
        ({ limit, offset, signal }: { limit: number; offset: number; signal?: AbortSignal }) =>
        FriendService.searchNewFriends({
            query: query.trim() || undefined,
            limit,
            offset,
            signal
        }),
        [query]
    );

    const inviteUser = useCallback(async (uuid: string) => {
        setActionLoadingId(uuid);
        try {
            await FriendService.invite(uuid);
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
      getKey={(u) => u.uuid}
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
                <img src={user.imageUrl} alt="profile image" className={styles.avatar} />
                ) : (
                <div className={styles.avatar} />
                )}

                <div className={styles.userInfo}>
                  <span className={styles.displayName}>{user.displayName}</span>
                  {user.bio ? <span className={styles.bio}>{user.bio}</span> : null}
                </div>

                <div className={styles.actionButtonBar}>
                    <button
                      type="button"
                      disabled={actionLoadingId === user.uuid}
                      onClick={() => inviteUser(user.uuid)}
                      aria-busy={actionLoadingId === user.uuid}
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
