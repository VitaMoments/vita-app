import type { ComponentType } from "react";
import { FeedCategory } from "../types";

import {
  MdPsychology,
  MdFitnessCenter,
  MdRestaurant,
  MdSpa,
  MdSelfImprovement,
  MdChecklist,
  MdBedtime,
  MdBolt,
  MdHandshake,
  MdGroups,
  MdTrackChanges,
  MdTrendingUp,
  MdAutoStories,
} from "react-icons/md";

export type FeedCategoryMeta = {
  label: string;
  description?: string;
  icon?: ComponentType<{ className?: string }>;
};

export const FEED_CATEGORY_META = {
  [FeedCategory.MENTAL]: {
    label: "Mental Health",
    description: "Mindset, stress, focus, emotions",
    icon: MdPsychology,
  },
  [FeedCategory.PHYSICAL]: {
    label: "Physical Health",
    description: "Training, movement, recovery",
    icon: MdFitnessCenter,
  },
  [FeedCategory.FOOD]: {
    label: "Food & Nutrition",
    description: "Meals, nutrition, healthy habits",
    icon: MdRestaurant,
  },
  [FeedCategory.LIFESTYLE]: {
    label: "Lifestyle",
    description: "Routine, balance, daily life",
    icon: MdSpa,
  },

  [FeedCategory.MINDFULNESS]: {
    label: "Mindfulness",
    description: "Presence, calm, awareness",
    icon: MdSelfImprovement,
  },
  [FeedCategory.HABITS]: {
    label: "Habits & Routines",
    description: "Systems that stick",
    icon: MdChecklist,
  },
  [FeedCategory.SLEEP]: {
    label: "Sleep & Rest",
    description: "Sleep quality, rest, recovery",
    icon: MdBedtime,
  },
  [FeedCategory.ENERGY]: {
    label: "Energy & Vitality",
    description: "Energy management and stamina",
    icon: MdBolt,
  },

  [FeedCategory.RELATIONSHIPS]: {
    label: "Relationships",
    description: "Connection and communication",
    icon: MdHandshake,
  },
  [FeedCategory.COMMUNITY]: {
    label: "Community",
    description: "Belonging and support",
    icon: MdGroups,
  },
  [FeedCategory.PURPOSE]: {
    label: "Purpose & Meaning",
    description: "Values, direction, meaning",
    icon: MdTrackChanges,
  },

  [FeedCategory.PERSONAL_GROWTH]: {
    label: "Personal Growth",
    description: "Learning and improvement",
    icon: MdTrendingUp,
  },
  [FeedCategory.REFLECTION]: {
    label: "Reflection",
    description: "Review, journaling, insights",
    icon: MdAutoStories,
  },
} satisfies Record<FeedCategory, FeedCategoryMeta>;
