// src/api/service/MediaService.ts
import api from "../axios";
import type {
  MediaAssetResponse,
  MediaPurposeType,
  MediaReferenceType,
  PrivacyStatus,
} from "../../data/types";

export type MediaSummary = MediaAssetResponse;
export type MediaUploadResponse = MediaAssetResponse;

export type UploadMediaArgs = {
  file: File;
  referenceId: string;
  referenceType: MediaReferenceType;
  purpose: MediaPurposeType;
  privacy: PrivacyStatus;
  extraFields?: Record<string, string | number | boolean | null | undefined>;
  signal?: AbortSignal;
};

export type DeleteMediaArgs = {
  mediaId: string;
  signal?: AbortSignal;
};

function appendPrimitiveField(
  form: FormData,
  key: string,
  value: string | number | boolean | null | undefined
) {
  if (value === null || value === undefined) return;
  form.append(key, String(value));
}

function buildMediaFormData({
  file,
  referenceId,
  referenceType,
  purpose,
  privacy,
  extraFields = {},
}: Omit<UploadMediaArgs, "signal">): FormData {
  const form = new FormData();

  form.append("file", file);
  form.append("referenceId", referenceId);
  form.append("referenceType", referenceType);
  form.append("purpose", purpose);
  form.append("privacy", privacy);

  for (const [key, value] of Object.entries(extraFields)) {
    appendPrimitiveField(form, key, value);
  }

  return form;
}

function mediaUrl(mediaId: string): string {
  return `/api/media/${encodeURIComponent(mediaId)}`;
}

export const MediaService = {
  async uploadMedia(args: UploadMediaArgs): Promise<MediaUploadResponse> {
    const form = buildMediaFormData(args);

    const res = await api.post<MediaUploadResponse>("/media", form, {
      signal: args.signal,
    });

    return res.data;
  },

  async uploadUserProfileImage(args: {
    file: File;
    userId: string;
    privacy: PrivacyStatus;
    extraFields?: Record<string, string | number | boolean | null | undefined>;
    signal?: AbortSignal;
  }): Promise<MediaUploadResponse> {
    return this.uploadMedia({
      file: args.file,
      referenceId: args.userId,
      referenceType: "USER" as MediaReferenceType,
      purpose: "PROFILE" as MediaPurposeType,
      privacy: args.privacy,
      extraFields: args.extraFields,
      signal: args.signal,
    });
  },

  async uploadUserCoverImage(args: {
    file: File;
    userId: string;
    privacy: PrivacyStatus;
    extraFields?: Record<string, string | number | boolean | null | undefined>;
    signal?: AbortSignal;
  }): Promise<MediaUploadResponse> {
    return this.uploadMedia({
      file: args.file,
      referenceId: args.userId,
      referenceType: "USER" as MediaReferenceType,
      purpose: "COVER" as MediaPurposeType,
      privacy: args.privacy,
      extraFields: args.extraFields,
      signal: args.signal,
    });
  },

  async uploadBlogCoverImage(args: {
    file: File;
    blogId: string;
    referenceType: MediaReferenceType;
    privacy: PrivacyStatus;
    extraFields?: Record<string, string | number | boolean | null | undefined>;
    signal?: AbortSignal;
  }): Promise<MediaUploadResponse> {
    return this.uploadMedia({
      file: args.file,
      referenceId: args.blogId,
      referenceType: args.referenceType,
      purpose: "COVER" as MediaPurposeType,
      privacy: args.privacy,
      extraFields: args.extraFields,
      signal: args.signal,
    });
  },

  async uploadBlogImage(args: {
    file: File;
    blogId: string;
    referenceType: MediaReferenceType;
    privacy: PrivacyStatus;
    extraFields?: Record<string, string | number | boolean | null | undefined>;
    signal?: AbortSignal;
  }): Promise<MediaUploadResponse> {
    return this.uploadMedia({
      file: args.file,
      referenceId: args.blogId,
      referenceType: args.referenceType,
      purpose: "POST" as MediaPurposeType,
      privacy: args.privacy,
      extraFields: args.extraFields,
      signal: args.signal,
    });
  },

  async deleteMedia(args: DeleteMediaArgs): Promise<void> {
    await api.delete(`/media/${encodeURIComponent(args.mediaId)}`, {
      signal: args.signal,
    });
  },

  async fetchMediaBlob(args: {
    mediaId: string;
    signal?: AbortSignal;
  }): Promise<Blob> {
    const res = await api.get(mediaUrl(args.mediaId), {
      responseType: "blob",
      signal: args.signal,
    });

    return res.data as Blob;
  },

  async fetchMediaObjectUrl(args: {
    mediaId: string;
    signal?: AbortSignal;
  }): Promise<string> {
    const blob = await this.fetchMediaBlob(args);
    return URL.createObjectURL(blob);
  },

  mediaUrl,
};