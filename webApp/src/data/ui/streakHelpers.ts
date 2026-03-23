import { streakStages, StreakStage } from "./streakStages";

export function getStreakStage(days: number): StreakStage {
  return (
    streakStages.find((stage) => {
      const meetsMin = days >= stage.minDays;
      const meetsMax = stage.maxDays == null || days <= stage.maxDays;
      return meetsMin && meetsMax;
    }) ?? streakStages[0]
  );
}

export function getNextStage(days: number): StreakStage | null {
  return (
    streakStages.find((stage) => stage.minDays > days) ?? null
  );
}

export function getStageProgress(days: number) {
  const current = getStreakStage(days);
  const next = getNextStage(days);

  if (!next || current.maxDays == null) {
    return {
      progress: 100,
      daysUntilNextStage: 0,
    };
  }

  const stageRange = next.minDays - current.minDays;
  const progressed = days - current.minDays;
  const progress = Math.max(0, Math.min(100, (progressed / stageRange) * 100));

  return {
    progress,
    daysUntilNextStage: Math.max(0, next.minDays - days),
  };
}

export function getStageVisual(asset: string): string {
  switch (asset) {
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
      return "🌱";
  }
}