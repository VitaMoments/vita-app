import { BlogCategory } from "../types";

/**
 * UI-only metadata for BlogCategory.
 * - You still send ONLY `BlogCategory[]` to the backend.
 * - This is purely for labels/icons/descriptions in the UI.
 */
export const BLOG_CATEGORY_META = {
  [BlogCategory.MENTAL]: { label: "Mental Health" },
  [BlogCategory.PHYSICAL]: { label: "Physical Health" },
  [BlogCategory.FOOD]: { label: "Food & Nutrition" },
  [BlogCategory.LIFESTYLE]: { label: "Lifestyle" },

  [BlogCategory.MINDFULNESS]: { label: "Mindfulness" },
  [BlogCategory.HABITS]: { label: "Habits & Routines" },
  [BlogCategory.SLEEP]: { label: "Sleep & Rest" },
  [BlogCategory.ENERGY]: { label: "Energy & Vitality" },

  [BlogCategory.RELATIONSHIPS]: { label: "Relationships" },
  [BlogCategory.COMMUNITY]: { label: "Community" },
  [BlogCategory.PURPOSE]: { label: "Purpose & Meaning" },

  [BlogCategory.PERSONAL_GROWTH]: { label: "Personal Growth" },
  [BlogCategory.REFLECTION]: { label: "Reflection" },
} satisfies Record<
  BlogCategory,
  {
    label: string;
    description?: string;
    icon?: string;
  }
>;
