import api from "../axios"

export async function login(email: string, password: string): Promise<UserDto> {
  const res = await api.post<UserDto>("/auth/login", { email, password });
  return res.data;
}

export async function fetchSession(): Promise<UserDto> {
  const res = await api.get<UserDto>("/auth/session");
  return res.data;
}

export async function logoutRequest(): Promise<void> {
  await api.post("/auth/logout");
}