import React from "react";
import styles from "./UserCard.module.css";

import type { User } from "../../data/types";
import { getUserDisplayName, getUserImageUrl, unwrapUser } from "../../data/ui/userHelpers";
import { MdPersonAdd, MdPersonRemove } from "react-icons/md";

type UserCardProps = {
  user: User;
  onAddUser: (uuid: string) => Promise<void>;
  onDeleteUser: (uuid: string) => Promise<void>;
  disabled?: boolean;
  loading?: boolean;
};

export function UserCard({
  user,
  onAddUser,
  onDeleteUser,
  disabled = false,
  loading = false,
}: UserCardProps) {
  // In jouw model is "PUBLIC" meestal "nog geen friend / discover"
  // Andere types kunnen friend-context of private/account zijn
  const base = unwrapUser(user);
  const isPublic = base.type === "PUBLIC";

  const img = getUserImageUrl(user);
  const name = getUserDisplayName(user);

  // bio bestaat op PUBLIC/PRIVATE/ACCOUNT in jouw output, maar niet altijd gegarandeerd
  const bio =
    "bio" in base && typeof base.bio === "string" && base.bio.length > 0 ? base.bio : null;

  // uuid zit op de "echte" user, niet op CONTEXT
  const uuid = "uuid" in base ? base.uuid : "";

  return (
    <article className={styles.card}>
      <div className={styles.cardContent}>
        {img ? (
          <img src={img} alt="profile image" className={styles.avatar} />
        ) : (
          <div className={styles.avatar} />
        )}

        <div className={styles.userInfo}>
          <span className={styles.displayName}>{name}</span>
          {bio ? <span className={styles.bio}>{bio}</span> : null}
        </div>

        <div className={styles.actionButtonBar}>
          {isPublic ? (
            <button
              type="button"
              disabled={disabled || loading || !uuid}
              onClick={() => onAddUser(uuid)}
              aria-busy={loading}
              aria-label="Add friend"
            >
              <MdPersonAdd className={styles.colorPrimary} />
            </button>
          ) : (
            <button
              type="button"
              disabled={disabled || loading || !uuid}
              onClick={() => onDeleteUser(uuid)}
              aria-busy={loading}
              aria-label="Remove friend"
            >
              <MdPersonRemove className={styles.colorError} />
            </button>
          )}

          {loading ? <span className={styles.loadingDot}>…</span> : null}
        </div>
      </div>
    </article>
  );
}
