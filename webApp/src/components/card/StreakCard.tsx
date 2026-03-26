import React from "react";
import styles from "./StreakCard.module.css";
import {
  getNextStage,
  getStageProgress,
  getStreakStage,
  getStageVisual,
} from "../../data/ui/streakHelpers";

type Props = {
  streakDays: number;
};

export function StreakCard({ streakDays }: Props) {
  const stage = getStreakStage(streakDays);
  const nextStage = getNextStage(streakDays);
  const { progress, daysUntilNextStage } = getStageProgress(streakDays);

  return (
    <section className={styles.card}>
      <div className={styles.streakLabel}>Jouw streak</div>

      <div className={styles.summaryRow}>
        <div className={styles.visual}>{getStageVisual(stage.asset)}</div>

        <div className={styles.streakCount}>
          <span className={styles.streakNumber}>{streakDays}</span>
          <span className={styles.streakUnit}>dagen</span>
        </div>
      </div>

      <div className={styles.stageTitle}>{stage.title}</div>
      <div className={styles.stageDescription}>{stage.description}</div>

      <div className={styles.progressSection}>
        <div className={styles.progressTop}>
          <span>Voortgang</span>
          {nextStage ? (
            <span>
              Nog {daysUntilNextStage} dagen tot {nextStage.title}
            </span>
          ) : (
            <span>Maximale fase bereikt</span>
          )}
        </div>

        <div className={styles.progressBar}>
          <div
            className={styles.progressFill}
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>
    </section>
  );
}
