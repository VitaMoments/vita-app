import React, { useCallback, useMemo, useState } from "react";
import { useEditor, useEditorState, EditorContent } from "@tiptap/react";

import Document from "@tiptap/extension-document";
import Paragraph from "@tiptap/extension-paragraph";
import Text from "@tiptap/extension-text";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import HardBreak from "@tiptap/extension-hard-break";
import History from "@tiptap/extension-history";
import Placeholder from "@tiptap/extension-placeholder";

import api from "../../api/axios";
import styles from "./TimelineInput.module.css";

type TimelineInputProps = {
  onPosted?: () => void; // optioneel: refresh timeline na submit
};

export function TimelineInput({ onPosted }: TimelineInputProps) {
  const [isPosting, setIsPosting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const editor = useEditor({
    extensions: [
      Document,
      Paragraph,
      Text,
      Bold,
      Italic,
      Underline,
      HardBreak,
      History,
      Placeholder.configure({ placeholder: "Write a message…" }),
    ],
    content: "",
    editorProps: {
      attributes: {
        class: styles.editor,
        "aria-label": "Timeline bericht invoer",
      },
    },
    onUpdate: () => {
      if (error) setError(null);
    },
  });

  // 🔥 Dit zorgt voor rerenders wanneer selectie/marks veranderen
  const { isBold, isItalic, isUnderline, textLen } = useEditorState({
    editor,
    selector: ({ editor }) => ({
      isBold: editor.isActive("bold"),
      isItalic: editor.isActive("italic"),
      isUnderline: editor.isActive("underline"),
      textLen: editor.getText().trim().length,
    }),
  });

  const canSubmit = useMemo(() => {
    return !!editor && textLen > 0 && !isPosting;
  }, [editor, textLen, isPosting]);

  const toggle = useCallback(
    (action: "bold" | "italic" | "underline") => {
      if (!editor) return;
      const chain = editor.chain().focus();
      if (action === "bold") chain.toggleBold().run();
      if (action === "italic") chain.toggleItalic().run();
      if (action === "underline") chain.toggleUnderline().run();
    },
    [editor]
  );

    const sendPost = useCallback(async () => {
      if (!editor) return;

      const plain = editor.getText().trim();
      if (!plain || isPosting) return;

      setIsPosting(true);
      setError(null);

      try {
        const contentJson = editor.getJSON();
        await api.post("/timeline", { content: contentJson }); // let op: /timeline (geen /api want baseURL heeft die al)
        editor.commands.clearContent(true);
        onPosted?.();
      } catch (e: any) {
        setError(e?.response?.data?.message ?? "Plaatsen mislukt. Probeer het opnieuw.");
      } finally {
        setIsPosting(false);
      }
    }, [editor, isPosting, onPosted]);

  if (!editor) return null;

  return (
    <div className={styles.wrapper}>
      <div className={styles.toolbar}>
        <button
          type="button"
          className={`${styles.toolBtn} ${isBold ? styles.active : ""}`}
          onClick={() => toggle("bold")}
          aria-pressed={isBold}
          title="Vet (Ctrl+B)"
        >
          <span className={styles.iconBold} aria-hidden="true">
            B
          </span>
        </button>

        <button
          type="button"
          className={`${styles.toolBtn} ${isItalic ? styles.active : ""}`}
          onClick={() => toggle("italic")}
          aria-pressed={isItalic}
          title="Cursief (Ctrl+I)"
        >
          <span className={styles.iconItalic} aria-hidden="true">
            I
          </span>
        </button>

        <button
          type="button"
          className={`${styles.toolBtn} ${isUnderline ? styles.active : ""}`}
          onClick={() => toggle("underline")}
          aria-pressed={isUnderline}
          title="Onderstrepen (Ctrl+U)"
        >
          <span className={styles.iconUnderline} aria-hidden="true">
            U
          </span>
        </button>

        <div className={styles.spacer} />

        <button
          type="button"
          className={styles.submitBtn}
          onClick={sendPost}
          disabled={!canSubmit}
        >
          {isPosting ? "Plaatsen…" : "Plaatsen"}
        </button>
      </div>

      <div className={styles.editorBox}>
        <EditorContent editor={editor} />
      </div>

      <div className={styles.meta}>
        <span>Enter = nieuwe paragraaf</span>
        <span className={styles.dot}>•</span>
        <span>Shift+Enter = nieuwe regel</span>
      </div>

      {error ? <div className={styles.error}>{error}</div> : null}
    </div>
  );
}
