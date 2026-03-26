import { streakStages, StreakStage } from "./streakStages";
import { StreakSummary } from "../types";

export function didAnswerToday(streak: StreakSummary | null): boolean {
  const value = streak?.lastAnsweredAt;
  if (value == null) return false;

  const lastAnswered = new Date(value);
  if (Number.isNaN(lastAnswered.getTime())) return false;

  const now = new Date();

  return (
    lastAnswered.getUTCFullYear() === now.getUTCFullYear() &&
    lastAnswered.getUTCMonth() === now.getUTCMonth() &&
    lastAnswered.getUTCDate() === now.getUTCDate()
  );
}

export function getStreakStage(days: number | null): StreakStage {
    if (days == null || days < 0) { return streakStages[0]; }

    return (
        streakStages.find((stage) => {
        const meetsMin = days >= stage.minDays;
        const meetsMax = stage.maxDays == null || days <= stage.maxDays;
        return meetsMin && meetsMax;
        }) ?? streakStages[0]
    );
}

export function getNextStage(days: number | null): StreakStage | null {
    const daysNotNull = days ?? 0;
    return (
        streakStages.find((stage) => stage.minDays > daysNotNull) ?? null
    );
}

export function getStageProgress(days: number | null) {
    const daysNotNull = days ?? 0;
    const current = getStreakStage(daysNotNull);
    const next = getNextStage(daysNotNull);

    if (!next || current.maxDays == null) {
        return {
        progress: 100,
        daysUntilNextStage: 0,
        };
    }

    const stageRange = next.minDays - current.minDays;
    const progressed = daysNotNull - current.minDays;
    const progress = Math.max(0, Math.min(100, (progressed / stageRange) * 100));

    return {
        progress,
        daysUntilNextStage: Math.max(0, next.minDays - daysNotNull),
    };
}

export function getStageVisual(asset: string): string {
  switch (asset) {
      case "soil":
          return "🟫";
    case "seed":
      return "🌰";
    case "sprout":
      return "🌱";
    case "smallPlant":
      return "🪴";
    case "youngPlant":
      return "🌿";
    case "bush":
      return "🌳";
    case "smallTree":
      return "🌲";
    case "bigTree":
      return "🌳";
    case "fullTree":
      return "🌳";
    default:
      return "🟫";
  }
}