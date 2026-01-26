// CreateBlogInput.tsx
import React, { useCallback, useMemo, useState } from "react";
import { useEditor, useEditorState, EditorContent } from "@tiptap/react";

import Input from "../../../components/input/Input";

import Document from "@tiptap/extension-document";
import Paragraph from "@tiptap/extension-paragraph";
import Text from "@tiptap/extension-text";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import HardBreak from "@tiptap/extension-hard-break";
import History from "@tiptap/extension-history";
import Placeholder from "@tiptap/extension-placeholder";

// richer set
import Heading from "@tiptap/extension-heading";
import BulletList from "@tiptap/extension-bullet-list";
import OrderedList from "@tiptap/extension-ordered-list";
import ListItem from "@tiptap/extension-list-item";
import Blockquote from "@tiptap/extension-blockquote";
import Link from "@tiptap/extension-link";

import styles from "./CreateBlogInput.module.css";

// ✅ generated types (barrel)
import type { BlogCategory, RichTextDocument } from "../../../data/types";

// ✅ UI-only metadata for labels/icons/descriptions
import { BLOG_CATEGORY_META } from "../../../data/ui/blogCategoryMeta";

// ✅ service
import { BlogService } from "../../../api/service/BlogService";

type CreateBlogInputProps = {
  onCreated?: (blogIdOrSlug: string) => void;
  onError?: (message: string, rawError?: unknown) => void;
  onClearError?: () => void;
};

const MAX_CATEGORIES = 3;

export const CreateBlogInput: React.FC<CreateBlogInputProps> = ({
  onCreated,
  onError,
  onClearError,
}) => {
  const [isSaving, setIsSaving] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  // fields
  const [title, setTitle] = useState("");
  const [subtitle, setSubtitle] = useState("");
  const [coverImageUrl, setCoverImageUrl] = useState("");
  const [coverImageAlt, setCoverImageAlt] = useState("");

  // categories
  const [categories, setCategories] = useState<BlogCategory[]>([]);

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
      Placeholder.configure({ placeholder: "Write your blog post…" }),
    ],
    content: "",
    editorProps: {
      attributes: {
        class: styles.editor,
        "aria-label": "Blog editor",
      },
    },
    onFocus: () => setIsFocused(true),
    onBlur: ({ editor }) => {
      const hasAny =
        title.trim().length > 0 ||
        subtitle.trim().length > 0 ||
        editor.getText().trim().length > 0;
      if (!hasAny) setIsFocused(false);
    },
    onUpdate: () => onClearError?.(),
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

  const canPublish = useMemo(() => {
    const t = title.trim().length > 0;
    const c = !!editor && textLen > 0;
    return t && c && !isSaving;
  }, [title, editor, textLen, isSaving]);

  const keepEditorFocus = (e: React.MouseEvent) => e.preventDefault();

  const toggleCategory = (cat: BlogCategory) => {
    setCategories((prev) => {
      const exists = prev.includes(cat);
      if (exists) return prev.filter((x) => x !== cat);
      if (prev.length >= MAX_CATEGORIES) return prev;
      return [...prev, cat];
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

  const saveBlog = useCallback(
    async (mode: "DRAFT" | "PUBLISH") => {
      if (!editor) return;
      if (isSaving) return;

      const t = title.trim();
      const bodyText = editor.getText().trim();

      if (!t) {
        onError?.("Title is required.");
        return;
      }

      // publish requires content; draft may be empty (change if you want)
      if (mode === "PUBLISH" && !bodyText) {
        onError?.("Content is required to publish.");
        return;
      }

      setIsSaving(true);
      onClearError?.();

      try {
        // ✅ Convert TipTap JSON into your contract RichTextDocument
        const tiptapJson = editor.getJSON();
        const content: RichTextDocument = { content: tiptapJson as any };

        const payload = {
          title: t,
          subtitle: subtitle.trim() || null,
          coverImageUrl: coverImageUrl.trim() || null,
          coverImageAlt: coverImageAlt.trim() || null,
          categories, // ✅ BlogCategory[]
          content, // ✅ RichTextDocument
          mode, // backend: draft vs publish (until you define the contract)
        };

        const res = await BlogService.create(payload as any);
        onCreated?.((res as any).slug ?? (res as any).id);
      } catch (e: any) {
        const msg =
          e?.response?.data?.message ??
          e?.message ??
          "Error saving blog. Please try again.";
        onError?.(msg, e);
      } finally {
        setIsSaving(false);
      }
    },
    [
      editor,
      isSaving,
      title,
      subtitle,
      coverImageUrl,
      coverImageAlt,
      categories,
      onCreated,
      onError,
      onClearError,
    ]
  );

  if (!editor) return null;

  const ALL_CATEGORIES = useMemo(
    () => Object.keys(BLOG_CATEGORY_META) as BlogCategory[],
    []
  );

  return (
    <div className={styles.wrapper}>
      <div className={styles.headerFields}>
        <Input
          name="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Title"
          onFocus={() => setIsFocused(true)}
        />

        <Input
          className={styles.subtitleInput}
          value={subtitle}
          onChange={(e) => setSubtitle(e.target.value)}
          placeholder="Subtitle (optional)…"
          onFocus={() => setIsFocused(true)}
        />
      </div>

      <div className={styles.row}>
        <Input
          className={styles.smallInput}
          value={coverImageUrl}
          onChange={(e) => setCoverImageUrl(e.target.value)}
          placeholder="Cover image URL (optional)…"
          onFocus={() => setIsFocused(true)}
        />
        <Input
          className={styles.smallInput}
          value={coverImageAlt}
          onChange={(e) => setCoverImageAlt(e.target.value)}
          placeholder="Cover image alt text (optional)…"
          onFocus={() => setIsFocused(true)}
        />
      </div>

      <div className={styles.categories}>
        <div className={styles.categoriesHeader}>
          <span className={styles.categoriesTitle}>Categories</span>
          <span className={styles.categoriesHint}>
            Choose up to {MAX_CATEGORIES}
          </span>
        </div>

        <div className={styles.categoryChips}>
          {ALL_CATEGORIES.map((c) => {
            const active = categories.includes(c);
            const meta = BLOG_CATEGORY_META[c];
            const label = meta?.label ?? c;

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
                {label}
              </button>
            );
          })}
        </div>
      </div>

      {isFocused && (
        <div className={styles.toolbar} onMouseDown={keepEditorFocus}>
          {/* headings */}
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

          {/* formatting */}
          <button
            type="button"
            className={`${styles.toolBtn} ${isBold ? styles.active : ""}`}
            onClick={() => cmd("bold")}
            aria-pressed={isBold}
            title="Bold (Ctrl+B)"
          >
            <span className={styles.iconBold} aria-hidden="true">
              B
            </span>
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isItalic ? styles.active : ""}`}
            onClick={() => cmd("italic")}
            aria-pressed={isItalic}
            title="Italic (Ctrl+I)"
          >
            <span className={styles.iconItalic} aria-hidden="true">
              I
            </span>
          </button>
          <button
            type="button"
            className={`${styles.toolBtn} ${isUnderline ? styles.active : ""}`}
            onClick={() => cmd("underline")}
            aria-pressed={isUnderline}
            title="Underline (Ctrl+U)"
          >
            <span className={styles.iconUnderline} aria-hidden="true">
              U
            </span>
          </button>

          <span className={styles.divider} />

          {/* lists & quote */}
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

          {/* link */}
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

          {/* status actions */}
          <button
            type="button"
            className={styles.secondaryBtn}
            onClick={() => saveBlog("DRAFT")}
            disabled={isSaving}
            title="Save as draft"
          >
            {isSaving ? "Saving…" : "Save Draft"}
          </button>

          <button
            type="button"
            className={styles.submitBtn}
            onClick={() => saveBlog("PUBLISH")}
            disabled={!canPublish}
            title="Publish"
          >
            {isSaving ? "Publishing…" : "Publish"}
          </button>
        </div>
      )}

      <div className={styles.editorBox}>
        <EditorContent editor={editor} />
      </div>

      {isFocused && (
        <div className={styles.meta}>
          <span>Enter = new paragraph</span>
          <span className={styles.dot}>•</span>
          <span>Shift+Enter = new line</span>
          <span className={styles.dot}>•</span>
          <span>Max {MAX_CATEGORIES} categories</span>
        </div>
      )}
    </div>
  );
};
