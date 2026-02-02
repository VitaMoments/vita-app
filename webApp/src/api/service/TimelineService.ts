// TimelineService.ts
import type { JSONContent } from "@tiptap/core";
import api from "../axios";

import type { FeedItem, TimeLineFeed } from "../../data/types";
import { isFeedItemTimelineitem } from "../../data/types";

export type GetTimelineParams = {
  feed?: TimeLineFeed; // default FRIENDS
  limit?: number;      // default 100
  offset?: number;     // default 0
};

function normalizeParams(params: GetTimelineParams) {
  return {
    feed: params.feed ?? "FRIENDS",
    limit: params.limit ?? 100,
    offset: params.offset ?? 0,
  };
}

export const TimelineService = {
  async postContent(document: JSONContent): Promise<void> {
    await api.post("/timeline", { document });
  },

  async getTimeline(params: GetTimelineParams = {}): Promise<FeedItem.TIMELINEITEM[]> {
    const res = await api.get<FeedItem.TIMELINEITEM[]>("/timeline", {
      params: normalizeParams(params),
    });
    return res.data;
  },
};
