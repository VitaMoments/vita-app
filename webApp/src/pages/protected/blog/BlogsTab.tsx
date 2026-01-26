// BlogsTab.tsx
import React, { useCallback, useEffect, useMemo, useState } from "react";
import styles from "./BlogsPage.module.css";

import { Card } from "../../../components/card/Card";
import { PagedList } from "../../../components/pagination/PagedList";

import { BlogService, type BlogListScope } from "../../../api/service/BlogService";

import type { FeedItem, BlogCategory, User } from "../../../data/types";
import { BLOG_CATEGORY_META } from "../../../data/ui/blogCategoryMeta";

const LIMIT = 20;

type Sort = "NEWEST" | "OLDEST";

type Props = {
  isActive: boolean;
  scope: BlogListScope;
  query: string;
  activeCategory: BlogCategory | null;
  sort: Sort;
  onOpenBlog: (slug: string) => void;
};

function getUserDisplayName(u: User): string {
  // PUBLIC has displayName required, others optional
  if ("displayName" in u && u.displayName) return u.displayName;
  if ("alias" in u && u.alias) return u.alias;
  if ("username" in u && u.username) return u.username;
  if ("email" in u && u.email) return u.email;
  return "Unknown";
}

function getUserImageUrl(u: User): string | null {
  if ("imageUrl" in u && u.imageUrl) return u.imageUrl;
  return null;
}

const formatDate = (millis: number) =>
  new Date(millis).toLocaleDateString(undefined, {
    year: "numeric",
    month: "short",
    day: "2-digit",
  });

const BlogsTab: React.FC<Props> = ({
  isActive,
  scope,
  query,
  activeCategory,
  sort,
  onOpenBlog,
}) => {
  const [reloadToken, setReloadToken] = useState(0);

  // trigger a reload when tab becomes active
  useEffect(() => {
    if (isActive) setReloadToken((x) => x + 1);
  }, [isActive]);

  // reset when filters change
  useEffect(() => {
    if (isActive) setReloadToken((x) => x + 1);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [scope, query, activeCategory, sort]);

  const fetchPage = useCallback(
    ({
      limit,
      offset,
      signal,
    }: {
      limit: number;
      offset: number;
      signal?: AbortSignal;
    }) =>
      BlogService.list(
        {
          scope,
          limit,
          offset,
          query: query.trim() || undefined,
          category: activeCategory ?? undefined,
          sort,
        },
        signal
      ),
    [scope, query, activeCategory, sort]
  );

  const listInstanceKey = useMemo(
    () => `blogs:${scope}:${query}:${activeCategory ?? "ALL"}:${sort}:${reloadToken}`,
    [scope, query, activeCategory, sort, reloadToken]
  );

  return (
    <PagedList<FeedItem.BLOGITEM>
      limit={LIMIT}
      resetKey={listInstanceKey}
      fetchPage={fetchPage}
      listClassName={styles.userCardList}
      empty={<p className={styles.emptyText}>No blogs found</p>}
      getKey={(b) => `blog:${b.uuid}`}
      renderItem={(blog) => {
        const date = blog.publishedAt ?? blog.createdAt;
        const authorName = getUserDisplayName(blog.author);
        const authorImg = getUserImageUrl(blog.author);

        return (
          <Card>
            <button
              type="button"
              onClick={() => onOpenBlog(blog.slug)}
              className={styles.cardContent}
              style={{ width: "100%", textAlign: "left", background: "transparent", border: 0, padding: 0 }}
            >
              {blog.coverImageUrl ? (
                <img
                  src={blog.coverImageUrl}
                  alt={blog.coverImageAlt ?? blog.title}
                  className={styles.avatar}
                  loading="lazy"
                />
              ) : (
                <div className={styles.avatar} />
              )}

              <div className={styles.userInfo}>
                <span className={styles.displayName}>{blog.title}</span>

                {blog.subtitle ? (
                  <span className={styles.bio}>{blog.subtitle}</span>
                ) : null}

                <div style={{ marginTop: 6, fontSize: 12, opacity: 0.8, display: "flex", gap: 8, flexWrap: "wrap" }}>
                  <span>{formatDate(date)}</span>
                  <span>·</span>
                  <span style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
                    {authorImg ? (
                      <img
                        src={authorImg}
                        alt=""
                        style={{ width: 18, height: 18, borderRadius: 999 }}
                      />
                    ) : null}
                    {authorName}
                  </span>
                </div>

                <div style={{ marginTop: 8, display: "flex", gap: 6, flexWrap: "wrap" }}>
                  {(blog.categories ?? []).slice(0, 3).map((c) => (
                    <span key={c} className={styles.badgeCategory}>
                      {BLOG_CATEGORY_META[c]?.label ?? c}
                    </span>
                  ))}
                  {scope === "MY_BLOGS" && blog.status !== "PUBLISHED" ? (
                    <span className={styles.badgeStatus}>{blog.status}</span>
                  ) : null}
                </div>
              </div>
            </button>
          </Card>
        );
      }}
    />
  );
};

export default BlogsTab;
