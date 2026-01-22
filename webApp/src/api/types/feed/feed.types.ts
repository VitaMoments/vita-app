// feed.types.ts

import type { UserDto } from "../user/userDto";
import type { RichTextDocument } from "../common/types";

// ===== Enums =====
export type BlogStatus = "DRAFT" | "PUBLISHED" | "ARCHIVED";
export type PrivacyStatus = "OPEN" | "FRIENDS_ONLY" | "PRIVATE";

// ===== Categories (V1) =====
export type BlogCategory =
  | "MENTAL"
  | "PHYSICAL"
  | "FOOD"
  | "LIFESTYLE"
  | "MINDFULNESS"
  | "HABITS"
  | "SLEEP"
  | "ENERGY"
  | "RELATIONSHIPS"
  | "COMMUNITY"
  | "PURPOSE"
  | "PERSONAL_GROWTH"
  | "REFLECTION";

export const BLOG_CATEGORY_META: Record<
  BlogCategory,
  {
    label: string;
    description?: string;
    icon?: string;
  }
> = {
  MENTAL: { label: "Mental Health" },
  PHYSICAL: { label: "Physical Health" },
  FOOD: { label: "Food & Nutrition" },
  LIFESTYLE: { label: "Lifestyle" },

  MINDFULNESS: { label: "Mindfulness" },
  HABITS: { label: "Habits & Routines" },
  SLEEP: { label: "Sleep & Rest" },
  ENERGY: { label: "Energy & Vitality" },

  RELATIONSHIPS: { label: "Relationships" },
  COMMUNITY: { label: "Community" },
  PURPOSE: { label: "Purpose & Meaning" },

  PERSONAL_GROWTH: { label: "Personal Growth" },
  REFLECTION: { label: "Reflection" },
};

// ===== Base FeedItemDto =====
export interface FeedItemDtoBase {
  uuid: string;
  author: UserDto;
  content: RichTextDocument;
  privacy: PrivacyStatus;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt?: LocalDateTime | null;
}

// ===== BlogItemDto =====
export interface BlogItemDto extends FeedItemDtoBase {
  type: "BLOGITEM";

  title: string;
  subtitle?: string | null;
  slug: string;

  coverImageUrl?: string | null;
  coverImageAlt?: string | null;

  // NOTE: for performance you may want to remove `html` from list endpoints later
  html: string;

  status: BlogStatus;
  publishedAt?: LocalDateTime | null;

  // ✅ categories used by sidebar filters & discovery
  categories: BlogCategory[];
}

// ===== TimelineItemDto =====
export interface TimelineItemDto extends FeedItemDtoBase {
  type: "TIMELINEITEM";

  plainText: string;
  html: string;
}

// ===== Union =====
export type FeedItemDto = BlogItemDto | TimelineItemDto;

// ===== Type guards (handig) =====
export const isBlogItemDto = (x: FeedItemDto): x is BlogItemDto => x.type === "BLOGITEM";
export const isTimelineItemDto = (x: FeedItemDto): x is TimelineItemDto => x.type === "TIMELINEITEM";
