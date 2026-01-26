import type { UserDto } from "../user/userDto"
import type { JSONContent } from "@tiptap/react";

export type TimeLinePostDto = {
  uuid: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  deletedAt: LocalDateTime | null;
  content: JSONContent;
  userDto: UserDto;
  plainText: string;
  html: string;
}