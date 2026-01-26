// feedDomain.ts

import type { User } from "../user/userDomain";
import type { RichTextDocument } from "../common/types";
import type { BlogCategory, BlogStatus, PrivacyStatus } from "./feedDto";

// ===== Base FeedItem =====
export interface FeedItemBase {
  uuid: string;
  author: User;
  content: RichTextDocument;
  privacy: PrivacyStatus;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt?: LocalDateTime | null;
}

// ===== BlogItem =====
export interface BlogItem extends FeedItemBase {
  type: "BLOGITEM";

  title: string;
  subtitle?: string | null;
  slug: string;

  coverImageUrl?: string | null;
  coverImageAlt?: string | null;

  html: string;
  status: BlogStatus;
  publishedAt?: LocalDateTime | null;

  // ✅ categories in domain too
  categories: BlogCategory[];
}

// ===== TimelineItem =====
export interface TimelineItem extends FeedItemBase {
  type: "TIMELINEITEM";

  plainText: string;
  html: string;
}

// ===== Union =====
export type FeedItem = BlogItem | TimelineItem;

// ===== Type guards (handig) =====
export const isBlogItem = (x: FeedItem): x is BlogItem => x.type === "BLOGITEM";
export const isTimelineItem = (x: FeedItem): x is TimelineItem => x.type === "TIMELINEITEM";
