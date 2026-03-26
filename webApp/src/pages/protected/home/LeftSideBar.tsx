import React from "react";
import styles from "./LeftSideBar.module.css";

import type { User } from "../../../data/types";
import { getUserDisplayName, getUserProfileImageUrl } from "../../../data/ui/userHelpers";
import { StreakCard } from "../../../components/card/StreakCard";
import { Button } from "../../../components/buttons/Button";

type LeftSideBarProps = {
  user: User;
  followingCount?: number;
  followersCount?: number;
  activitiesCount?: number;
  onOpenDailyQuestion?: () => void;
};

export function LeftSideBar({
  user,
  followingCount = 0,
  followersCount = 0,
  activitiesCount = 0,
  onOpenDailyQuestion,
}: LeftSideBarProps) {
  const name = getUserDisplayName(user);
  const profileImageAssetUrl = getUserProfileImageUrl(user);

  return (
    <article className={styles.card}>
      <div className={styles.avatarWrapper}>
        {profileImageAssetUrl ? (
          <img src={profileImageAssetUrl} alt={name} className={styles.avatar} />
        ) : (
          <div className={styles.avatarPlaceholder} aria-hidden="true" />
        )}
      </div>

      <div className={styles.cardBody}>
        <h2 className={styles.displayName}>{name}</h2>

        <div className={styles.stats}>
          <div className={styles.statItem}>
            <span className={styles.statLabel}>Volgend</span>
            <span className={styles.statValue}>{followingCount}</span>
          </div>

          <div className={styles.statDivider} />

          <div className={styles.statItem}>
            <span className={styles.statLabel}>Volgers</span>
            <span className={styles.statValue}>{followersCount}</span>
          </div>

          <div className={styles.statDivider} />

          <div className={styles.statItem}>
            <span className={styles.statLabel}>Streak</span>
            <span className={styles.statValue}>{activitiesCount}</span>
          </div>
        </div>

        <div className={styles.horizontalDivider} />
        <StreakCard streakDays={18} />
        <Button onClick={onOpenDailyQuestion}>Vraag van de dag</Button>
      </div>
    </article>
  );
}