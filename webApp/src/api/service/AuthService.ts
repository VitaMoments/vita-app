// AuthService.ts
import api from "../axios";
import { User } from "../../data/types";

export const AuthService = {
  async login(email: string, password: string): Promise<User.ACCOUNT> {
    const res = await api.post<User.ACCOUNT>("/auth/login", { email, password });
    return res.data;
  },

  async register(username: string, email: string, password: string): Promise<User.ACCOUNT> {
    const res = await api.post<User.ACCOUNT>("/auth/register", { username, email, password });
    return res.data;
  },

  async fetchSession(): Promise<User.ACCOUNT> {
    const res = await api.get<User.ACCOUNT>("/auth/session");
    return res.data;
  },

  async refreshSession(): Promise<void> {
    await api.post("/auth/refresh");
  },

  async logout(): Promise<void> {
    await api.post("/auth/logout");
  },
};
