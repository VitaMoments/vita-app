// UserCardIncomingRequest.tsx
import React from "react";
import styles from "./UserCard.module.css";

import type { User } from "../../data/types";
import { getUserDisplayName } from "../../data/ui/userHelpers";

type UserCardIncomingRequestProps = {
  user: User;
  onAccept: (uuid: string) => Promise<void>;
  onDecline: (uuid: string) => Promise<void>;
  disabled?: boolean;
  loading?: boolean;
};

export function UserCardIncomingRequest({
  user,
  onAccept,
  onDecline,
  disabled = false,
  loading = false,
}: UserCardIncomingRequestProps) {
  const img = user.imageUrl;
  const name = getUserDisplayName(user);
  const uuid = user.uuid

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
        </div>

        <div className={styles.actionButtonBar}>
          <button
            type="button"
            className={styles.declineButton}
            disabled={disabled || loading}
            onClick={() => onDecline(uuid)}
            aria-busy={loading}
            aria-label="Decline"
          >
            Cancel
          </button>

          <button
            type="button"
            className={styles.acceptButton}
            disabled={disabled || loading}
            onClick={() => onAccept(uuid)}
            aria-busy={loading}
            aria-label="Accept"
          >
            Accept
          </button>
        </div>
      </div>
    </article>
  );
}