// TimelinePostCard.tsx
import React from "react";
import { generateHTML } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import type { TimeLinePost } from "../../api/types/timelinePostType";
import { toAbsoluteUrl } from "../../utils/urls";
import styles from "./TimelinePostCard.module.css";

type TimelinePostCardProps = {
  post: TimeLinePost;
};

export function TimelinePostCard({ post }: TimelinePostCardProps) {
    const created = new Date(post.createdAt);

    const html = generateHTML(post.content, [StarterKit]);

    return (
        <article className={styles.card}>
        <header className={styles.header}>
            {post.user.imageUrl ? (
            <img
                src={post.user.imageUrl}
                alt={post.user.email}
                className={styles.avatar}
              />
            ) : (
              <div className={styles.avatar} />
            )}

            <div className={styles.meta}>
              <span className={styles.email}>{post.user.email}</span>
              <span className={styles.date}>
                {created.toLocaleDateString("nl-NL", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </span>
            </div>
        </header>

          <div
            className={styles.content}
            dangerouslySetInnerHTML={{ __html: html }}
          />
    </article>
  );
}
