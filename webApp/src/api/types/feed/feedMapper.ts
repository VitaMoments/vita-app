// feedMapper.ts

import type { FeedItemDto, BlogItemDto, TimelineItemDto } from "./feedDto";
import type { FeedItem, BlogItem, TimelineItem } from "./feedDomain";
import { mapUserDtoToUser } from "../user/userMapper";

/**
 * Maps shared base fields from any FeedItemDto to a domain-ready base object.
 */
function mapFeedItemBase(dto: FeedItemDto) {
  return {
    uuid: dto.uuid,
    author: mapUserDtoToUser(dto.author),
    content: dto.content,
    privacy: dto.privacy,
    createdAt: dto.createdAt,
    updatedAt: dto.updatedAt,
    deletedAt: dto.deletedAt ?? null,
  };
}

/**
 * DTO -> Domain (union)
 */
export function mapFeedItemDtoToDomain(dto: FeedItemDto): FeedItem {
  switch (dto.type) {
    case "BLOGITEM":
      return mapBlogItemDtoToDomain(dto);
    case "TIMELINEITEM":
      return mapTimelineItemDtoToDomain(dto);
    default: {
      // Exhaustiveness check
      const _never: never = dto;
      return _never;
    }
  }
}

/**
 * DTO -> Domain (BlogItem)
 */
export function mapBlogItemDtoToDomain(dto: BlogItemDto): BlogItem {
  return {
    ...mapFeedItemBase(dto),
    type: "BLOGITEM",

    title: dto.title,
    subtitle: dto.subtitle ?? null,
    slug: dto.slug,

    coverImageUrl: dto.coverImageUrl ?? null,
    coverImageAlt: dto.coverImageAlt ?? null,

    html: dto.html,
    status: dto.status,
    publishedAt: dto.publishedAt ?? null,

    categories: dto.categories ?? [],
  };
}

/**
 * DTO -> Domain (TimelineItem)
 */
export function mapTimelineItemDtoToDomain(dto: TimelineItemDto): TimelineItem {
  return {
    ...mapFeedItemBase(dto),
    type: "TIMELINEITEM",

    plainText: dto.plainText,
    html: dto.html,
  };
}

/**
 * Convenience helpers for arrays
 */
export function mapFeedItemsDtoToDomain(items: FeedItemDto[]): FeedItem[] {
  return items.map(mapFeedItemDtoToDomain);
}

export function mapBlogItemsDtoToDomain(items: BlogItemDto[]): BlogItem[] {
  return items.map(mapBlogItemDtoToDomain);
}

export function mapTimelineItemsDtoToDomain(items: TimelineItemDto[]): TimelineItem[] {
  return items.map(mapTimelineItemDtoToDomain);
}

/**
 * Optional: Domain -> DTO
 * Useful if you ever store domain objects locally and need to send them back.
 * If you don't need it, you can delete everything below.
 */

function mapFeedItemBaseToDto(base: FeedItem["type"] extends any ? any : never) {
  // Intentionally left out: domain->dto base mapping is usually app-specific
  // (because you often have different payloads for create/update).
  // Keep your write DTOs separate: CreateBlogItemRequest, UpdateBlogItemRequest, etc.
}
