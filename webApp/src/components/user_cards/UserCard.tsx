import React from "react";
import styles from "./UserCard.module.css";

import User from "../../api/types/userDomain/User";
import { MdPersonAdd, MdPersonRemove } from "react-icons/md";

type UserCardProps = {
    user: User;
    onAddUser: (uuid: string) => Promise<void>;
    onDeleteUser: (uuid: string) => Promise<void>;
    disabled?: boolean;
    loading?: boolean;
};

export function UserCard({ user, onAddUser, onDeleteUser, disabled = false, loading = false }: UserCardProps) {
  const isPublic = user.type === "PUBLIC";

  return (
    <article className={styles.card}>
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
          {isPublic ? (
            <button
              type="button"
              disabled={disabled || loading}
              onClick={() => onAddUser(user.uuid)}
              aria-busy={loading}
              aria-label="Add friend"
            >
              <MdPersonAdd className={styles.colorPrimary} />
            </button>
          ) : (
            <button
              type="button"
              disabled={disabled || loading}
              onClick={() => onDeleteUser(user.uuid)}
              aria-busy={loading}
              aria-label="Remove friend"
            >
              <MdPersonRemove className={styles.colorError} />
            </button>
          )}

          {/* Optional: eenvoudige indicator, kan je later stylen */}
          {loading ? <span className={styles.loadingDot}>…</span> : null}
        </div>
      </div>
    </article>
  );
}
