// src/api/service/BlogService.ts
import api from "../axios";

import type { BlogCategory, FeedItem, RichTextDocument } from "../../data/types";

/**
 * Frontend request shape (until you create the Kotlin contract).
 * Keep this aligned with what your backend expects.
 */
export type CreateBlogRequest = {
  title: string;
  subtitle?: string | null;
  coverImageUrl?: string | null;
  coverImageAlt?: string | null;
  categories: BlogCategory[];
  content: RichTextDocument;
  mode: "DRAFT" | "PUBLISH";

  // Optional: if you later add privacy to the create flow.
  // privacy?: PrivacyStatus;
};

export type BlogListScope = "FOLLOWING" | "DISCOVER" | "MY_BLOGS";

export type GetBlogsParams = {
  scope: BlogListScope;
  limit?: number;
  offset?: number;

  query?: string;
  category?: BlogCategory;

  // Optional: add sorting later (keep strings so backend can evolve)
  sort?: "NEWEST" | "OLDEST";
};

export type PagedResult<T> = {
  items: T[];
  limit: number;
  offset: number;
  total: number;
  hasMore: boolean;
  nextOffset?: number | null;
};

function normalizeListParams(params: GetBlogsParams) {
  return {
    scope: params.scope,
    limit: params.limit ?? 20,
    offset: params.offset ?? 0,
    query: params.query?.trim() || undefined,
    category: params.category ?? undefined,
    sort: params.sort ?? "NEWEST",
  };
}

export const BlogService = {
  /**
   * Create a blog (draft/publish).
   * Default endpoint: POST /blogs
   */
  async create(body: CreateBlogRequest, signal?: AbortSignal): Promise<FeedItem.BLOGITEM> {
    const res = await api.post<FeedItem.BLOGITEM>("/blogs", body, { signal });
    return res.data;
  },

  /**
   * List blogs with paging.
   * Default endpoint: GET /blogs
   *
   * Expected backend response:
   * { items, limit, offset, total, hasMore, nextOffset }
   */
  async list(params: GetBlogsParams, signal?: AbortSignal): Promise<PagedResult<FeedItem.BLOGITEM>> {
    const res = await api.get<PagedResult<FeedItem.BLOGITEM>>("/blogs", {
      params: normalizeListParams(params),
      signal,
    });
    return res.data;
  },

  /**
   * Get blog detail by slug.
   * Default endpoint: GET /blogs/:slug
   */
  async getBySlug(slug: string, signal?: AbortSignal): Promise<FeedItem.BLOGITEM> {
    const res = await api.get<FeedItem.BLOGITEM>(`/blogs/${encodeURIComponent(slug)}`, { signal });
    return res.data;
  },

  /**
   * Optional: category counts (if you add it server-side)
   * Default endpoint: GET /blogs/categories
   */
  async getCategoryCounts(
    params: { scope?: BlogListScope; query?: string } = {},
    signal?: AbortSignal
  ): Promise<Array<{ category: BlogCategory; count: number }>> {
    const res = await api.get<Array<{ category: BlogCategory; count: number }>>("/blogs/categories", {
      params: {
        scope: params.scope,
        query: params.query?.trim() || undefined,
      },
      signal,
    });
    return res.data;
  },
};
