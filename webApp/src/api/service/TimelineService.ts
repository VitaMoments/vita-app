// TimelineService.ts
import type { JSONContent } from "@tiptap/core";
import api from "../axios";

import type { FeedItem } from "../../data/types";
import { isFeedItemTimelineItem } from "../../data/types";

export type GetTimelineParams = {
  limit?: number;  // default 100
  offset?: number; // default 0
};

function normalizeParams(params: GetTimelineParams) {
  return {
    limit: params.limit ?? 100,
    offset: params.offset ?? 0,
  };
}

export const TimelineService = {
  async postContent(content: JSONContent): Promise<void> {
    await api.post("/timeline", { content });
  },

  async getTimeline(params: GetTimelineParams = {}): Promise<FeedItem.TIMELINEITEM[]> {
    const res = await api.get<FeedItem[]>("/timeline", {
      params: normalizeParams(params),
    });

    // Filter is future-proof in case backend ever returns mixed FeedItem[]
    return res.data.filter(isFeedItemTimelineItem);
  },
};
