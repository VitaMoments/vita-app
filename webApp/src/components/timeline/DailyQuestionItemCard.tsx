import React, { useMemo } from "react";
import { generateHTML } from "@tiptap/core";
import StarterKit from "@tiptap/starter-kit";

import type { FeedItem } from "../../data/types";
import { getUserDisplayName, getUserProfileImageUrl } from "../../data/ui/userHelpers";

import styles from "./DailyQuestionItemCard.module.css";

type Props = {
  item: FeedItem.DAILYQUESTIONITEM;
};

export function DailyQuestionItemCard({ item }: Props) {
  const authorName = getUserDisplayName(item.author);
  const authorImage = getUserProfileImageUrl(item.author);
  const created = new Date(item.createdAt);

  const html = useMemo(() => {
    const content = item.content?.content as any;
    if (!content) return "";

    try {
      return generateHTML(content, [StarterKit]);
    } catch {
      return "";
    }
  }, [item.content]);

  return (
    <article className={styles.card}>
      <header className={styles.header}>
        {authorImage ? (
          <img src={authorImage} alt={authorName} className={styles.avatar} />
        ) : (
          <div className={styles.avatar} />
        )}

        <div className={styles.meta}>
          <span className={styles.authorName}>{authorName}</span>
          <span className={styles.date}>
            {created.toLocaleString("nl-NL", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
              hour: "2-digit",
              minute: "2-digit",
            })}
          </span>
        </div>
      </header>

      <span className={styles.badge}>Daily question</span>
      <h3 className={styles.question}>{item.question}</h3>

      {item.categories != undefined && item.categories.length > 0 && (
        <p className={styles.metaText}>{item.categories.join(" • ")}</p>
      )}

      {item.selectedAnswer && (
        <p className={styles.selectedAnswer}>Gekozen antwoord: {item.selectedAnswer}</p>
      )}

      {html ? (
        <div className={styles.content} dangerouslySetInnerHTML={{ __html: html }} />
      ) : null}
    </article>
  );
}

