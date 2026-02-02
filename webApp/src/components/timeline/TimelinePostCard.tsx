// TimelinePostCard.tsx
import React, { useMemo } from "react";
import { generateHTML } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import type { JSONContent } from "@tiptap/core";

import type { FeedItem } from "../../data/types";
import styles from "./TimelinePostCard.module.css";
import { getUserDisplayName } from "../../data/ui/userHelpers";

// Optioneel: voeg hier EXACT dezelfde extensions toe als in je editor (als je die gebruikt)
// import Underline from "@tiptap/extension-underline";
// import Link from "@tiptap/extension-link";

function sanitizeTiptapJson(input: any): JSONContent {
  // unwrap mogelijke wrapper { content: ... }
  let node: any = input?.type ? input : input?.content;

  // als je per ongeluk een array krijgt, wrap hem als doc
  if (Array.isArray(node)) node = { type: "doc", content: node };

  const sanitizeNode = (n: any): any | null => {
    if (!n || typeof n !== "object") return null;
    if (typeof n.type !== "string") return null; // <- dit voorkomt "Unknown node type: undefined"

    const out: any = { ...n };

    if (Array.isArray(out.content)) {
      out.content = out.content
        .map(sanitizeNode)
        .filter(Boolean);
    }

    if (Array.isArray(out.marks)) {
      out.marks = out.marks.filter((m: any) => m && typeof m.type === "string");
    }

    return out;
  };

  const cleaned = sanitizeNode(node);
  return cleaned ?? { type: "doc", content: [] };
}

export function TimelinePostCard({ item }: { item: FeedItem.TIMELINEITEM }) {
  const created = new Date(item.createdAt);

  const authorName = getUserDisplayName(item.author);
  const authorImage = item.author.imageUrl;

  const html = useMemo(() => {
    const json = sanitizeTiptapJson(item.content);

    // Debug: als het nog fout gaat, zie je meteen de payload
    // console.log("TIPTAP JSON", JSON.stringify(json, null, 2));

    return generateHTML(json, [
      StarterKit,
      // Underline,
      // Link,
    ]);
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
          <span className={styles.email}>{authorName}</span>

          {/* toLocaleDateString negeert vaak hour/minute; toLocaleString is veiliger */}
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

      <div className={styles.content} dangerouslySetInnerHTML={{ __html: html }} />
    </article>
  );
}