import api from "../axios";
// import { UserDto, mapUserDtoToUser } from "../types/userType"
import type { AccountUserDto } from "../types/user/userDto";
import type { User } from "../types/user/userDomain";
import { mapAccountUserDtoToAccountUser, mapUserDtoToUser } from "../types/user/mapUserDtoToUser";

export const AuthService = {
    async login(email: string, password: string): Promise<User> {
        const res = await api.post<AccountUserDto>("/auth/login", { email, password });
        console.log(res)
        return mapAccountUserDtoToAccountUser(res.data);
    },

    async register(username: string, email: string, password: string): Promise<User> {
        const res = await api.post<AccountUserDto>("/auth/register", { username, email, password });
        return mapAccountUserDtoToAccountUser(res.data);
    },

    async fetchSession(): Promise<User> {
        const res = await api.get<AccountUserDto>("/auth/session");
        return mapAccountUserDtoToAccountUser(res.data);
    },

    async refreshSession(): Promise<User> {
        const res = await api.post<AccountUserDto>("/auth/refresh");
        return mapAccountUserDtoToAccountUser(res.data);
    },

    async logout(): Promise<boolean> {
        const res = await api.post<boolean>("/auth/logout");
        return res.data;
    },
}


