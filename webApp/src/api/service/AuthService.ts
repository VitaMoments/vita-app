import api from "../axios";
import { UserDto } from "../types/userType"

export const AuthService = {
    async login(email: string, password: string): Promise<UserDto> {
      const res = await api.post<UserDto>("/auth/login", { email, password });
      return res.data;
    },

    async register(email: string, password: string): Promise<UserDto> {
      const res = await api.post<UserDto>("/auth/register", { email, password });
      return res.data;
    },

    async fetchSession(): Promise<UserDto> {
      const res = await api.get<UserDto>("/auth/session");
      return res.data;
    },

    async logout(): Promise<Boolean> {
      const res = await api.post<Boolean>("/auth/logout");
      return res.data
    }
}


