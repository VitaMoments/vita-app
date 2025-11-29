import api from "./axios";

export type UserDto = {
  uuid: string;
  email: string;
  createdAt: string;
  updatedAt: string;
  deletedAt: string | null;
  imageUrl: string | null;
};

export async function login(email: string, password: string): Promise<UserDto> {
  const res = await api.post<UserDto>("/auth/login", { email, password });
  return res.data;
}

export async function register(email: string, password: string): Promise<UserDto> {
  const res = await api.post<UserDto>("/auth/register", { email, password });
  return res.data;
}

export async function fetchSession(): Promise<UserDto> {
  const res = await api.get<UserDto>("/auth/session");
  return res.data;
}

export async function logoutRequest(): Promise<Boolean> {
  const res = await api.post<Boolean>("/auth/logout");
  return res.data
}