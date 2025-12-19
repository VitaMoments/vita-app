import type { JSONContent } from "@tiptap/core";
import api from "../axios";
import { TimeLinePostDto, TimeLinePost, mapTimeLinePostDtoListToTimeLinePostList } from "../types/timelinePostType";

export const TimelineService = {
    async postContent(content: JSONContent): Promise<void> {
        await api.post("/timeline", { content });
    },

    async fetchTimelinePosts(params: {
      label: string;
      offset: number;
      limit: number;
    }): Promise<TimeLinePost[]> {
      const response = await api.get<TimeLinePostDto[]>("/timeline", {
        params
      });
      return mapTimeLinePostDtoListToTimeLinePostList(response.data);
    }
}
