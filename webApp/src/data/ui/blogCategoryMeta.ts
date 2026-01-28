import type { ComponentType } from "react";
import { BlogCategory } from "../types";

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

export type BlogCategoryMeta = {
  label: string;
  description?: string;
  icon?: ComponentType<{ className?: string }>;
};

export const BLOG_CATEGORY_META = {
  [BlogCategory.MENTAL]: {
    label: "Mental Health",
    description: "Mindset, stress, focus, emotions",
    icon: MdPsychology,
  },
  [BlogCategory.PHYSICAL]: {
    label: "Physical Health",
    description: "Training, movement, recovery",
    icon: MdFitnessCenter,
  },
  [BlogCategory.FOOD]: {
    label: "Food & Nutrition",
    description: "Meals, nutrition, healthy habits",
    icon: MdRestaurant,
  },
  [BlogCategory.LIFESTYLE]: {
    label: "Lifestyle",
    description: "Routine, balance, daily life",
    icon: MdSpa,
  },

  [BlogCategory.MINDFULNESS]: {
    label: "Mindfulness",
    description: "Presence, calm, awareness",
    icon: MdSelfImprovement,
  },
  [BlogCategory.HABITS]: {
    label: "Habits & Routines",
    description: "Systems that stick",
    icon: MdChecklist,
  },
  [BlogCategory.SLEEP]: {
    label: "Sleep & Rest",
    description: "Sleep quality, rest, recovery",
    icon: MdBedtime,
  },
  [BlogCategory.ENERGY]: {
    label: "Energy & Vitality",
    description: "Energy management and stamina",
    icon: MdBolt,
  },

  [BlogCategory.RELATIONSHIPS]: {
    label: "Relationships",
    description: "Connection and communication",
    icon: MdHandshake,
  },
  [BlogCategory.COMMUNITY]: {
    label: "Community",
    description: "Belonging and support",
    icon: MdGroups,
  },
  [BlogCategory.PURPOSE]: {
    label: "Purpose & Meaning",
    description: "Values, direction, meaning",
    icon: MdTrackChanges,
  },

  [BlogCategory.PERSONAL_GROWTH]: {
    label: "Personal Growth",
    description: "Learning and improvement",
    icon: MdTrendingUp,
  },
  [BlogCategory.REFLECTION]: {
    label: "Reflection",
    description: "Review, journaling, insights",
    icon: MdAutoStories,
  },
} satisfies Record<BlogCategory, BlogCategoryMeta>;
