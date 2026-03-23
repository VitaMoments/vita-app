export type StreakStage = {
  key: string;
  minDays: number;
  maxDays: number | null;
  title: string;
  description: string;
  asset: string;
};

export const streakStages: StreakStage[] = [
  {
    key: "seed",
    minDays: 0,
    maxDays: 6,
    title: "Zaadje",
    description: "Elke grote groei begint klein.",
    asset: "seed",
  },
  {
    key: "sprout",
    minDays: 7,
    maxDays: 29,
    title: "Kiem",
    description: "Je begint zichtbaar momentum op te bouwen.",
    asset: "sprout",
  },
  {
    key: "small-plant",
    minDays: 30,
    maxDays: 99,
    title: "Plantje",
    description: "Je routine krijgt wortels.",
    asset: "smallPlant",
  },
  {
    key: "young-plant",
    minDays: 100,
    maxDays: 249,
    title: "Jonge plant",
    description: "Je blijft groeien en wordt sterker.",
    asset: "youngPlant",
  },
  {
    key: "bush",
    minDays: 250,
    maxDays: 499,
    title: "Struik",
    description: "Je bent al ver gekomen.",
    asset: "bush",
  },
  {
    key: "small-tree",
    minDays: 500,
    maxDays: 749,
    title: "Kleine boom",
    description: "Je groei is stevig en zichtbaar.",
    asset: "smallTree",
  },
  {
    key: "big-tree",
    minDays: 750,
    maxDays: 999,
    title: "Grote boom",
    description: "Je streak is indrukwekkend.",
    asset: "bigTree",
  },
  {
    key: "full-tree",
    minDays: 1000,
    maxDays: null,
    title: "Volledige boom",
    description: "Een uitzonderlijke mijlpaal.",
    asset: "fullTree",
  },
];