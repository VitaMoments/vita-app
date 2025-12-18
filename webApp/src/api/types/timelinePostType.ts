import { User, PublicUserDto, mapPublicUserDtoToUser } from "./userType"
import type { JSONContent } from "@tiptap/react";

export interface TimeLinePostDto {
  uuid: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt: LocalDateTime | null;
  content: JSONContent;
  userDto: PublicUserDto;
  plainText: string;
  html: string;
}

export interface TimeLinePost {
  uuid: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  content: JSONContent;
  user: PublicUser;
  plainText: string;
  html: string;
}

export const mapTimeLinePostDtoToTimeLinePost = (dto: TimeLinePostDto): TimeLinePost => {
    return {
        uuid: dto.uuid,
        createdAt: dto.createdAt,
        updatedAt: dto.updatedAt,
        content: dto.content,
        user: mapPublicUserDtoToUser(dto.userDto),
        plainText: dto.plainText,
        html: dto.plainText
        }
    }

export const mapTimeLinePostDtoListToTimeLinePostList = (
  dtos: TimeLinePostDto[]
): TimeLinePost[] => dtos.map(mapTimeLinePostDtoToTimeLinePost)