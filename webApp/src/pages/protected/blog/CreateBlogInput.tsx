import React, { useState } from "react";
import Input from "../../../components/input/Input";
import { FeedEditor } from "../../../components/editor/feed-editor/FeedEditor";
import styles from "./CreateBlogInput.module.css";

import type { FeedCategory, RichTextDocument } from "../../../data/types";
import { BlogService } from "../../../api/service/BlogService";

type CreateBlogInputProps = {
  onCreated?: (blogIdOrSlug: string) => void;
  onError?: (message: string, rawError?: unknown) => void;
  onClearError?: () => void;
};

export const CreateBlogInput: React.FC<CreateBlogInputProps> = ({
  onCreated,
  onError,
  onClearError,
}) => {
  const [title, setTitle] = useState("");
  const [subtitle, setSubtitle] = useState("");
  const [coverImageUrl, setCoverImageUrl] = useState("");
  const [coverImageAlt, setCoverImageAlt] = useState("");

  return (
    <div className={styles.wrapper}>
      <div className={styles.headerFields}>
        <Input
          name="title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Title"
        />

        <Input
          className={styles.subtitleInput}
          value={subtitle}
          onChange={(e) => setSubtitle(e.target.value)}
          placeholder="Subtitle (optional)…"
        />
      </div>

      <div className={styles.row}>
        <Input
          className={styles.smallInput}
          value={coverImageUrl}
          onChange={(e) => setCoverImageUrl(e.target.value)}
          placeholder="Cover image URL (optional)…"
        />
        <Input
          className={styles.smallInput}
          value={coverImageAlt}
          onChange={(e) => setCoverImageAlt(e.target.value)}
          placeholder="Cover image alt text (optional)…"
        />
      </div>

      <FeedEditor
        forceOpen={true}
        placeholder="Write your blog post…"
        showCategories={true}
        showDraftButton={true}
        publishLabel="Publish"
        draftLabel="Save Draft"
        onClearError={onClearError}
        onError={onError}
        onSubmit={async ({ mode, categories, document, text }) => {
          const t = title.trim();

          if (!t) {
            onError?.("Title is required.");
            return;
          }

          if (mode === "PUBLISH" && !text.trim()) {
            onError?.("Content is required to publish.");
            return;
          }

          const status: "DRAFT" | "PUBLISHED" =
            mode === "PUBLISH" ? "PUBLISHED" : "DRAFT";

          const payload = {
            title: t,
            subtitle: subtitle.trim() || null,
            coverImageUrl: coverImageUrl.trim() || null,
            coverImageAlt: coverImageAlt.trim() || null,
            categories,
            document: document as RichTextDocument,
            status,
          };

          const res = await BlogService.create(payload);
          onCreated?.((res as any).slug ?? (res as any).id);
        }}
      />
    </div>
  );
};