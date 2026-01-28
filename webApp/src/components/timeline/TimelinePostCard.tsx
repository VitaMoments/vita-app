// TimelinePostCard.tsx
import React from "react";
import { generateHTML } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import type { FeedItem } from "../../data/types"
import styles from "./TimelinePostCard.module.css";
import { getUserDisplayName } from "../../data/ui/userHelpers";


export function TimelinePostCard({ item }: { item: FeedItem.TIMELINEITEM }) {
    const created = new Date(item.createdAt);
    const html = generateHTML(item.content.content, [StarterKit]);

    const authorName = getUserDisplayName(item.author);
//     const authorEmail = getUserEmail(item.author);
    const authorImage = item.author.imageUrl

    return (
        <article className={styles.card}>
        <header className={styles.header}>
            {authorImage ? (
            <img
                src={authorImage}
                alt={authorName}
                className={styles.avatar}
              />
            ) : (
              <div className={styles.avatar} />
            )}

            <div className={styles.meta}>
              <span className={styles.email}>{authorName}</span>
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
