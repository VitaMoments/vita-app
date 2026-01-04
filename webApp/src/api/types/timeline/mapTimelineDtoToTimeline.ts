import type { TimeLinePostDto } from "./timelinePostDto";
import type { TimeLinePost } from "./timelinePostDomain";
import { mapUserDtoToUser } from "../user/mapUserDtoToUser";

export const mapTimeLinePostDtoToTimeLinePost = (dto: TimeLinePostDto): TimeLinePost => ({
  uuid: dto.uuid,
  createdAt: dto.createdAt,
  updatedAt: dto.updatedAt,
  deletedAt: dto.deletedAt,
  content: dto.content,
  user: mapUserDtoToUser(dto.userDto),
  plainText: dto.plainText,
  html: dto.html,
});

export const mapTimeLinePostDtoListToTimeLinePosts = (dtos: TimeLinePostDto[]): TimeLinePost[] =>
  dtos.map(mapTimeLinePostDtoToTimeLinePost);