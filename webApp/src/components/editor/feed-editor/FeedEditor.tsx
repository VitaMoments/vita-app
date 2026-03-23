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
import Heading from "@tiptap/extension-heading";
import BulletList from "@tiptap/extension-bullet-list";
import OrderedList from "@tiptap/extension-ordered-list";
import ListItem from "@tiptap/extension-list-item";
import Blockquote from "@tiptap/extension-blockquote";
import Link from "@tiptap/extension-link";

import styles from "./FeedEditor.module.css";

import type { FeedCategory, RichTextDocument } from "../../../data/types";
import { FEED_CATEGORY_META } from "../../../data/ui/feedCategoryMeta";

const MAX_CATEGORIES = 3;

type FeedEditorSubmitMode = "DRAFT" | "PUBLISH";

type FeedEditorProps = {
  initialCategories?: FeedCategory[];
  initialContent?: string | object;
  placeholder?: string;

  showCategories?: boolean;
  showDraftButton?: boolean;
  forceOpen?: boolean;
  publishLabel?: string;
  draftLabel?: string;

  onChange?: (value: {
    categories: FeedCategory[];
    document: RichTextDocument;
    text: string;
    isEmpty: boolean;
  }) => void;

  onSubmit?: (value: {
    mode: FeedEditorSubmitMode;
    categories: FeedCategory[];
    document: RichTextDocument;
    text: string;
  }) => Promise<void> | void;

  onError?: (message: string, rawError?: unknown) => void;
  onClearError?: () => void;
};

export const FeedEditor: React.FC<FeedEditorProps> = ({
  initialCategories = [],
  initialContent = "",
  placeholder = "Write something…",
  showCategories = true,
  showDraftButton = true,
  publishLabel = "Publish",
  draftLabel = "Save Draft",
  forceOpen = false,
  onChange,
  onSubmit,
  onError,
  onClearError,
}) => {
  const [isSaving, setIsSaving] = useState(false);
  const [isFocused, setIsFocused] = useState(false);
  const [categories, setCategories] = useState<FeedCategory[]>(initialCategories);

  const ALL_CATEGORIES = useMemo(
    () => Object.keys(FEED_CATEGORY_META) as FeedCategory[],
    []
  );

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
      Heading.configure({ levels: [1, 2, 3] }),
      BulletList,
      OrderedList,
      ListItem,
      Blockquote,
      Link.configure({
        openOnClick: false,
        autolink: true,
        linkOnPaste: true,
      }),
      Placeholder.configure({ placeholder }),
    ],
    content: initialContent,
    editorProps: {
      attributes: {
        class: styles.editor,
        "aria-label": "Rich text editor",
      },
    },
    onFocus: () => setIsFocused(true),
    onBlur: () => {
      setIsFocused(false);
    },
    onUpdate: ({ editor }) => {
      onClearError?.();

      const text = editor.getText().trim();
      const document: RichTextDocument = { content: editor.getJSON() as any };

      onChange?.({
        categories,
        document,
        text,
        isEmpty: text.length === 0,
      });
    },
  });

  const {
    isBold,
    isItalic,
    isUnderline,
    isH1,
    isH2,
    isH3,
    isBulletList,
    isOrderedList,
    isBlockquote,
    isLink,
    textLen,
  } = useEditorState({
    editor,
    selector: ({ editor }) => ({
      isBold: editor.isActive("bold"),
      isItalic: editor.isActive("italic"),
      isUnderline: editor.isActive("underline"),
      isH1: editor.isActive("heading", { level: 1 }),
      isH2: editor.isActive("heading", { level: 2 }),
      isH3: editor.isActive("heading", { level: 3 }),
      isBulletList: editor.isActive("bulletList"),
      isOrderedList: editor.isActive("orderedList"),
      isBlockquote: editor.isActive("blockquote"),
      isLink: editor.isActive("link"),
      textLen: editor.getText().trim().length,
    }),
  });

  const hasContent = textLen > 0 || categories.length > 0;
  const isExpanded = forceOpen || isFocused || hasContent;

  const canPublish = useMemo(() => {
    return !!editor && textLen > 0 && !isSaving;
  }, [editor, textLen, isSaving]);

  const keepEditorFocus = (e: React.MouseEvent) => e.preventDefault();

  const toggleCategory = (cat: FeedCategory) => {
    setCategories((prev) => {
      const exists = prev.includes(cat);

      const next = exists
        ? prev.filter((x) => x !== cat)
        : prev.length >= MAX_CATEGORIES
          ? prev
          : [...prev, cat];

      if (editor) {
        const text = editor.getText().trim();
        const document: RichTextDocument = { content: editor.getJSON() as any };

        onChange?.({
          categories: next,
          document,
          text,
          isEmpty: text.length === 0,
        });
      }

      return next;
    });
  };

  const cmd = useCallback(
    (
      action:
        | "bold"
        | "italic"
        | "underline"
        | "h1"
        | "h2"
        | "h3"
        | "bullet"
        | "ordered"
        | "blockquote"
        | "link"
        | "unlink"
    ) => {
      if (!editor) return;

      const chain = editor.chain().focus();

      switch (action) {
        case "bold":
          chain.toggleBold().run();
          break;
        case "italic":
          chain.toggleItalic().run();
          break;
        case "underline":
          chain.toggleUnderline().run();
          break;
        case "h1":
          chain.toggleHeading({ level: 1 }).run();
          break;
        case "h2":
          chain.toggleHeading({ level: 2 }).run();
          break;
        case "h3":
          chain.toggleHeading({ level: 3 }).run();
          break;
        case "bullet":
          chain.toggleBulletList().run();
          break;
        case "ordered":
          chain.toggleOrderedList().run();
          break;
        case "blockquote":
          chain.toggleBlockquote().run();
          break;
        case "link": {
          const existing = editor.getAttributes("link")?.href as string | undefined;
          const next = window.prompt("Link URL", existing ?? "https://");
          if (!next) return;
          chain.extendMarkRange("link").setLink({ href: next }).run();
          break;
        }
        case "unlink":
          chain.unsetLink().run();
          break;
      }
    },
    [editor]
  );

  const submit = useCallback(
    async (mode: FeedEditorSubmitMode) => {
      if (!editor || isSaving) return;

      const text = editor.getText().trim();
      const document: RichTextDocument = { content: editor.getJSON() as any };

      if (mode === "PUBLISH" && !text) {
        onError?.("Content is required to publish.");
        return;
      }

      try {
        setIsSaving(true);
        onClearError?.();

        await onSubmit?.({
          mode,
          categories,
          document,
          text,
        });
      } catch (e: any) {
        const msg = e?.message ?? "Error saving content. Please try again.";
        onError?.(msg, e);
      } finally {
        setIsSaving(false);
      }
    },
    [editor, isSaving, onSubmit, categories, onError, onClearError]
  );

  if (!editor) return null;

  return (
    <div className={styles.wrapper}>
      {showCategories && isExpanded && (
        <div className={styles.categories}>
          <div className={styles.categoriesHeader}>
            <span className={styles.categoriesTitle}>Categories</span>
            <span className={styles.categoriesHint}>
              Choose up to {MAX_CATEGORIES}
            </span>
          </div>

          <div className={styles.categoryChips} onMouseDown={keepEditorFocus}>
            {ALL_CATEGORIES.map((c) => {
              const active = categories.includes(c);
              const meta = FEED_CATEGORY_META[c];
              const label = meta?.label ?? c;
              const Icon = meta?.icon;

              return (
                <button
                  key={c}
                  type="button"
                  onClick={() => toggleCategory(c)}
                  className={`${styles.categoryChip} ${
                    active ? styles.categoryChipActive : ""
                  }`}
                  title={meta?.description ?? label}
                  aria-pressed={active}
                >
                  {Icon ? <Icon className={styles.categoryChipIcon} /> : null}
                  {label}
                </button>
              );
            })}
          </div>
        </div>
      )}

      {isExpanded && (
        <div className={styles.toolbar} onMouseDown={keepEditorFocus}>
          <button
            type="button"
            className={`${styles.toolBtn} ${isH1 ? styles.active : ""}`}
            onClick={() => cmd("h1")}
            aria-pressed={isH1}
            title="Heading 1"
          >
            H1
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isH2 ? styles.active : ""}`}
            onClick={() => cmd("h2")}
            aria-pressed={isH2}
            title="Heading 2"
          >
            H2
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isH3 ? styles.active : ""}`}
            onClick={() => cmd("h3")}
            aria-pressed={isH3}
            title="Heading 3"
          >
            H3
          </button>

          <span className={styles.divider} />

          <button
            type="button"
            className={`${styles.toolBtn} ${isBold ? styles.active : ""}`}
            onClick={() => cmd("bold")}
            aria-pressed={isBold}
            title="Bold"
          >
            <span className={styles.iconBold}>B</span>
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isItalic ? styles.active : ""}`}
            onClick={() => cmd("italic")}
            aria-pressed={isItalic}
            title="Italic"
          >
            <span className={styles.iconItalic}>I</span>
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isUnderline ? styles.active : ""}`}
            onClick={() => cmd("underline")}
            aria-pressed={isUnderline}
            title="Underline"
          >
            <span className={styles.iconUnderline}>U</span>
          </button>

          <span className={styles.divider} />

          <button
            type="button"
            className={`${styles.toolBtn} ${isBulletList ? styles.active : ""}`}
            onClick={() => cmd("bullet")}
            aria-pressed={isBulletList}
            title="Bullet list"
          >
            • List
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isOrderedList ? styles.active : ""}`}
            onClick={() => cmd("ordered")}
            aria-pressed={isOrderedList}
            title="Ordered list"
          >
            1. List
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isBlockquote ? styles.active : ""}`}
            onClick={() => cmd("blockquote")}
            aria-pressed={isBlockquote}
            title="Blockquote"
          >
            “ ”
          </button>

          <span className={styles.divider} />

          <button
            type="button"
            className={`${styles.toolBtn} ${isLink ? styles.active : ""}`}
            onClick={() => cmd(isLink ? "unlink" : "link")}
            aria-pressed={isLink}
            title={isLink ? "Remove link" : "Add link"}
          >
            {isLink ? "Unlink" : "Link"}
          </button>

          <div className={styles.spacer} />

          {showDraftButton && (
            <button
              type="button"
              className={styles.secondaryBtn}
              onClick={() => submit("DRAFT")}
              disabled={isSaving}
            >
              {isSaving ? "Saving…" : draftLabel}
            </button>
          )}

          <button
            type="button"
            className={styles.submitBtn}
            onClick={() => submit("PUBLISH")}
            disabled={!canPublish}
          >
            {isSaving ? "Publishing…" : publishLabel}
          </button>
        </div>
      )}

      <div
        className={`${styles.editorBox} ${
          isExpanded ? styles.editorBoxExpanded : styles.editorBoxCollapsed
        }`}
      >
        <EditorContent editor={editor} />
      </div>

      {isExpanded && (
        <div className={styles.meta}>
          <span>Enter = new paragraph</span>
          <span className={styles.dot}>•</span>
          <span>Shift+Enter = new line</span>
          {showCategories && (
            <>
              <span className={styles.dot}>•</span>
              <span>Max {MAX_CATEGORIES} categories</span>
            </>
          )}
        </div>
      )}
    </div>
  );
};