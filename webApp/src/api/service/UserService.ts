// UserService.ts
import api from "../axios";
import type { User } from "../../data/types";

type PostImageOptions = {
  file: File;
  fields?: Record<string, string>;
};

const postImage = async ({ file, fields = {} }: PostImageOptions): Promise<User> => {
  const form = new FormData();
  form.append("file", file);

  for (const [k, v] of Object.entries(fields)) {
    form.append(k, v);
  }

  const res = await api.post<User>("/profile/image", form);
  return res.data;
};

export type GetUserParams = {
  query: string;
  offset: number;
  limit: number;
};

export const UserService = {
  /**
   * searchUsers(): backend returns User[] (mixed: PUBLIC/PRIVATE/ACCOUNT/CONTEXT)
   */
  async searchUsers(params: {
    query?: string;
    offset: number;
    limit: number;
  }): Promise<User[]> {
    const res = await api.get<User[]>("/users/search", { params });
    return res.data;
  },

  async uploadProfilePhoto(args: {
    file: File;
    cropX: number;
    cropY: number;
    cropW: number;
    cropH: number;
    avatarSize?: number;
  }): Promise<User> {
    const { file, cropX, cropY, cropW, cropH, avatarSize } = args;

    return await postImage({
      file,
      fields: {
        cropX: String(Math.round(cropX)),
        cropY: String(Math.round(cropY)),
        cropW: String(Math.round(cropW)),
        cropH: String(Math.round(cropH)),
        ...(avatarSize ? { avatarSize: String(Math.round(avatarSize)) } : {}),
      },
    });
  },

  /**
   * fetchNewFriends(): backend returns User.PUBLIC[]
   */
  async fetchNewFriends(params: {
    query: string;
    offset: number;
    limit: number;
  }): Promise<User.PUBLIC[]> {
    const res = await api.get<User.PUBLIC[]>("/friends/search", { params });
    return res.data;
  },
};
