import api from "../axios";
import { User } from "../../data/types"

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

  async refreshSession(): Promise<User.ACCOUNT> {
    const res = await api.post<User.ACCOUNT>("/auth/refresh");
    return res.data;
  },

  async logout(): Promise<boolean> {
    const res = await api.post<boolean>("/auth/logout");
    return res.data;
  },
};


