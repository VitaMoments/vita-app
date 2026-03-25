import React from "react";
import styles from "./UserCard.module.css";

import type { User } from "../../data/types";
import { isUserPublic } from "../../data/types"
import { MdPersonAdd, MdPersonRemove } from "react-icons/md";

import { getUserDisplayName, getUserProfileImageUrl } from "../../data/ui/userHelpers";

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
  const name = getUserDisplayName(user);
  const imageUrl = getUserProfileImageUrl(user);

  // bio bestaat op PUBLIC/PRIVATE/ACCOUNT in jouw output, maar niet altijd gegarandeerd
  const bio = user.bio

  // uuid zit op de "echte" user, niet op CONTEXT
  const uuid = user.uuid

  return (
    <article className={styles.card}>
      <div className={styles.cardContent}>
        {imageUrl ? (
          <img src={imageUrl} alt="profile image" className={styles.avatar} />
        ) : (
          <div className={styles.avatar} />
        )}

        <div className={styles.userInfo}>
          <span className={styles.displayName}>{name}</span>
          {bio ? <span className={styles.bio}>{bio}</span> : null}
        </div>

        <div className={styles.actionButtonBar}>
          { isUserPublic(user) ? (
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
