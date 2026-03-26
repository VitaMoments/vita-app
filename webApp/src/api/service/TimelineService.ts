// TimelineService.ts
import api from "../axios";

import type { CreateTimelineItemRequest, FeedItem, TimeLineFeed } from "../../data/types";

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
  async postContent(payload: CreateTimelineItemRequest): Promise<void> {
    await api.post("/timeline", payload);
  },

  async updateContent(item: FeedItem.TIMELINEITEM): Promise<FeedItem.TIMELINEITEM> {
    const { data } = await api.put("/timeline", { item });
    return data;
  },

  async getTimeline(params: GetTimelineParams = {}): Promise<FeedItem[]> {
    const res = await api.get<FeedItem[]>("/timeline", { params: normalizeParams(params) });
    return res.data;
  },
};
