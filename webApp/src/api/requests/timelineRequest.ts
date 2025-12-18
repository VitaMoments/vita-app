import api from "../axios"
import { TimeLinePostDto, TimeLinePost, mapTimeLinePostDtoListToTimeLinePostList } from "../types/timelinePostType";

export async function fetchTimelinePosts(params: {
  label: string;
  offset: number;
  limit: number;
}): Promise<TimeLinePost[]> {
  const response = await api.get<TimeLinePostDto[]>("/timeline", {
    params
  });
  return mapTimeLinePostDtoListToTimeLinePostList(response.data);
}

