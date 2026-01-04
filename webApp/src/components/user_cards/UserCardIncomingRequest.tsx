import React from "react";
import styles from "./UserCard.module.css";

import User from "../../api/types/userDomain/User";

type UserCardIncomingRequestProps = & {
    user: User;
    onAccept: (uuid: string) => Promise<void>;
    onDecline: (uuid: string) => Promise<void>;
    disabled?: boolean;
    loading?: boolean;
};

export function UserCardIncomingRequest({ user, onAccept, onDecline, disabled = false, loading = false }: UserCardIncomingRequestProps) {

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
        </div>

        <div className={styles.actionButtonBar}>
            <button
                type="button"
                className={styles.declineButton}
                disabled={disabled || loading}
                onClick={() => onDecline(user.uuid)}
                aria-busy={loading}
                aria-label="Decline">
                Cancel
            </button>
            <button
                type="button"
                className={styles.acceptButton}
                disabled={disabled || loading}
                onClick={() => onAccept(user.uuid)}
                aria-busy={loading}
                aria-label="Accept">
                Accept
            </button>
        </div>
      </div>
    </article>
  );
}