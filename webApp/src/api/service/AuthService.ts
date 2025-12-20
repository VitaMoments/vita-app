import api from "../axios";
import { UserDto, mapUserDtoToUser } from "../types/userType"

export const AuthService = {
    async login(email: string, password: string): Promise<User> {
      const res = await api.post<UserDto>("/auth/login", { email, password });
      return mapUserDtoToUser(res.data);
    },

    async register(email: string, password: string): Promise<User> {
      const res = await api.post<UserDto>("/auth/register", { email, password });
      return mapUserDtoToUser(res.data);
    },

    async fetchSession(): Promise<User> {
      const res = await api.get<UserDto>("/auth/session");
      return mapUserDtoToUser(res.data);
    },

    async refreshSession(): Promise<User> {
        const res = await api.post<UserDto>("/auth/refresh")  ;
        console.log(mapUserDtoToUser(res.data))
        return mapUserDtoToUser(res.data);
    },

    async logout(): Promise<Boolean> {
      const res = await api.post<Boolean>("/auth/logout");
      return res.data
    }
}


