import React, { useCallback, useMemo, useState } from "react";
import { useEditor, useEditorState, EditorContent } from "@tiptap/react";
import { TimelineService } from "../../api/service/TimelineService";

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
  onPosted?: () => void;
  onError?: (message: string, rawError?: unknown) => void;
  onClearError?: () => void;
};

export function TimelineInput({ onPosted, onError, onClearError }: TimelineInputProps) {
    const [isPosting, setIsPosting] = useState(false);
    const [isFocused, setIsFocused] = useState(false);

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
    onFocus: () => setIsFocused(true),
    onBlur: ({ editor }) => {
        const hasText = editor.getText().trim().length > 0;
        if (!hasText) setIsFocused(false);
    },
    onUpdate: () => {
      onClearError?.();
    },
    });

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

    const keepEditorFocus = (e: React.MouseEvent) => {
      e.preventDefault();
    };

    const sendPost = useCallback(async () => {
      if (!editor) return;

      const plain = editor.getText().trim();
      if (!plain || isPosting) return;

      setIsPosting(true);
      onClearError?.();

      try {
        await TimelineService.postContent(editor.getJSON());
        editor.commands.clearContent(true);
        onPosted?.();
      } catch (e: any) {
          const msg =
            e?.response?.data?.message ??
            e?.message ??
            "Error posting content. Please try again..";
          onError?.(msg, e);
      } finally {
        setIsPosting(false);
      }
    }, [editor, isPosting, onPosted, onError, onClearError]);

    if (!editor) return null;

    return (
    <div className={styles.wrapper}>
      {isFocused && (
        <div className={styles.toolbar} onMouseDown={keepEditorFocus}>
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
    )}

      <div className={styles.editorBox}>
        <EditorContent editor={editor} />
      </div>

       {isFocused && (
                <div className={styles.meta}>
                  <span>Enter = nieuwe paragraaf</span>
                  <span className={styles.dot}>•</span>
                  <span>Shift+Enter = nieuwe regel</span>
                </div>
              )}
    </div>
    );
}
