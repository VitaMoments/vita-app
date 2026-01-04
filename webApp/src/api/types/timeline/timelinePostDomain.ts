import type { User } from "../user/userDomain";
import type { JSONContent } from "@tiptap/react";

export type TimeLinePost = {
  uuid: string;
  createdAt: LocalDateTime;
  updatedAt: LocalDateTime;
  content: JSONContent;
  user: User;
  plainText: string;
  html: string;
}