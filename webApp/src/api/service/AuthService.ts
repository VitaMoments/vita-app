import api from "../axios";
import { AccountUserDto } from "../../data/types"

export const AuthService = {
  async login(email: string, password: string): Promise<UserContract.ACCOUNT> {
    const res = await api.post<UserContract.ACCOUNT>("/auth/login", { email, password });
    return res.data;
  },

  async register(username: string, email: string, password: string): Promise<UserContract.ACCOUNT> {
    const res = await api.post<UserContract.ACCOUNT>("/auth/register", { username, email, password });
    return res.data;
  },

  async fetchSession(): Promise<UserContract.ACCOUNT> {
    const res = await api.get<UserContract.ACCOUNT>("/auth/session");
    return res.data;
  },

  async refreshSession(): Promise<UserContract.ACCOUNT> {
    const res = await api.post<UserContract.ACCOUNT>("/auth/refresh");
    return res.data;
  },

  async logout(): Promise<boolean> {
    const res = await api.post<boolean>("/auth/logout");
    return res.data;
  },
};


