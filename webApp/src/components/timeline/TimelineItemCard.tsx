// TimelinePostCard.tsx
import React, { useMemo, useState, useEffect } from "react";
import { generateHTML, EditorContent, useEditor } from "@tiptap/react";
import { HiOutlinePencilSquare } from "react-icons/hi2";
import { FaTrashCan } from "react-icons/fa6";
import { PiFlagPennantFill } from "react-icons/pi";

import StarterKit from "@tiptap/starter-kit";
import type { JSONContent } from "@tiptap/core";

import BaseDialog from "../dialog/BaseDialog";

import { TimelineService } from "../../api/service/TimelineService";
import type { FeedItem } from "../../data/types";
import styles from "./TimelineItemCard.module.css";
import { getUserDisplayName } from "../../data/ui/userHelpers";

type Props = {
    isUserItem: boolean,
    item: FeedItem.TIMELINEITEM
    };

function sanitizeTiptapJson(input: any): JSONContent {
  let node: any = input?.type ? input : input?.content;

  if (Array.isArray(node)) node = { type: "doc", content: node };

  const sanitizeNode = (n: any): any | null => {
    if (!n || typeof n !== "object") return null;
    if (typeof n.type !== "string") return null;

    const out: any = { ...n };

    if (Array.isArray(out.content)) {
      out.content = out.content.map(sanitizeNode).filter(Boolean);
    }

    if (Array.isArray(out.marks)) {
      out.marks = out.marks.filter((m: any) => m && typeof m.type === "string");
    }

    return out;
  };

  const cleaned = sanitizeNode(node);
  return cleaned ?? { type: "doc", content: [] };
}

export function TimelineItemCard({ isUserItem, item }: Props) {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [content, setContent] = useState<JSONContent>(
    sanitizeTiptapJson(item.content)
  );

  const created = new Date(item.createdAt);
  const authorName = getUserDisplayName(item.author);
  const authorImage = item.author.imageUrl;

  // 👉 Editor voor dialog
  const editor = useEditor({
    extensions: [StarterKit],
    content,
    immediatelyRender: false,
  });

  // Als dialog opent → zet huidige content
  useEffect(() => {
    if (open && editor) {
      editor.commands.setContent(content);
    }
  }, [open, content, editor]);

  const html = useMemo(() => {
    return generateHTML(content, [StarterKit]);
  }, [content]);

  return (
    <>
      <article className={styles.card}>
        <header className={styles.header}>
          {authorImage ? (
            <img src={authorImage} alt={authorName} className={styles.avatar} />
          ) : (
            <div className={styles.avatar} />
          )}

          <div className={styles.meta}>
            <span className={styles.email}>{authorName}</span>

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

          <div className={styles.optionButtonBar}>
          { isUserItem ?
              <>
                  <button
                    className={styles.editButton}
                    onClick={() => setOpen(true)}
                  >
                    <HiOutlinePencilSquare />
                  </button>
                  <button
                    className={styles.editButton}
                    onClick={() => setOpen(true)}
                  >
                    <FaTrashCan />
                  </button>
              </> :
              <button
                className={styles.editButton}
                onClick={() => setOpen(true)}
              >
                <PiFlagPennantFill />
              </button>
              }
          </div>

        </header>

        <div
          className={styles.content}
          dangerouslySetInnerHTML={{ __html: html }}
        />
      </article>

      {/* 🔹 EDIT DIALOG */}
      <BaseDialog
        open={open}
        onClose={() => !loading && setOpen(false)}
        title="Bericht bewerken"
        size="lg"
        footer={
            <>
              <button onClick={() => setOpen(false)}>Annuleren</button>

              <button
                disabled={loading}
                onClick={async () => {
                  if (!editor) return;

                  const newJson = editor.getJSON();

                  const updatedItem = {
                    ...item,
                    content: newJson,
                  };

                  try {
                    setLoading(true);
                    const data = await TimelineService.updateContent(updatedItem);
                    setContent(sanitizeTiptapJson(data.content));
                    setOpen(false);
                  } catch (e) {
                    console.error("Update failed", e);
                  } finally {
                    setLoading(false);
                  }
                }}
              >
                {loading ? "Opslaan..." : "Opslaan"}
              </button>
            </>
          }
      >
        <div style={{ border: "1px solid #ddd", padding: 12 }}>
          <EditorContent editor={editor} />
        </div>
      </BaseDialog>
    </>
  );
}
