import type { JSONContent } from "@tiptap/core";
import type { TimeLineFeed } from "../types/timeline/timelineFeed";
import { toApiFeedParam } from "../types/timeline/timelineFeed";
import type { TimeLinePost } from "../types/timeline/timelinePostDomain";
import type { TimeLinePostDto } from "../types/timeline/timelinePostDto";
import { mapTimeLinePostDtoListToTimeLinePosts } from "../types/timeline/mapTimelineDtoToTimeline";
import api from "../axios";

export type GetTimelineParams = {
  feed?: TimeLineFeed; // default FRIENDS
  limit?: number;      // default 100
  offset?: number;     // default 0
};

export const TimelineService = {
    async postContent(content: JSONContent): Promise<void> {
         await api.post("/timeline", { content });
    },

    async getTimeline(params: GetTimelineParams = {}): Promise<TimeLinePost[]> {
        const feed = params.feed ?? "FRIENDS";
        const limit = params.limit ?? 100;
        const offset = params.offset ?? 0;

        const res = await api.get<TimeLinePostDto[]>("/timeline", {
          params: {
            feed: toApiFeedParam(feed),
            limit,
            offset,
          },
        });

        return mapTimeLinePostDtoListToTimeLinePosts(res.data);
    },
};



// import type { JSONContent } from "@tiptap/core";
// import api from "../axios";
// import { TimeLinePostDto, TimeLinePost, mapTimeLinePostDtoListToTimeLinePostList } from "../types/timelinePostType";
//
// export const TimelineService = {
//     async postContent(content: JSONContent): Promise<void> {
//         await api.post("/timeline", { content });
//     },
//
//     async fetchTimelinePosts(params: {
//       label: string;
//       offset: number;
//       limit: number;
//     }): Promise<TimeLinePost[]> {
//       const response = await api.get<TimeLinePostDto[]>("/timeline", {
//         params
//       });
//       return mapTimeLinePostDtoListToTimeLinePostList(response.data);
//     }
// }
