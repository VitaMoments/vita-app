import api from "../axios";
import { UserDto } from "../types/userType";

type PostImageOptions = {
  file: File;
  fields?: Record<string, string>;
};

const postImage = async ({ file, fields = {} }: PostImageOptions): Promise<UserDto> => {
  const form = new FormData();
  form.append("file", file);

  for (const [k, v] of Object.entries(fields)) {
    form.append(k, v);
  }

  const res = await api.post<UserDto>("/profile/image", form);
  return res.data;
};

export const UserService = {
  async uploadProfilePhoto(args: {
    file: File;
    cropX: number;
    cropY: number;
    cropW: number;
    cropH: number;
    avatarSize?: number;
  }): Promise<UserDto> {
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
};
