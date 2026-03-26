// AuthService.ts
import api from "../axios";
import type { UserWithContext } from "../../data/types";

export const AuthService = {
  login(email: string, password: string) {
    return api.post<UserWithContext>("/auth/login", { email, password }).then((r) => r.data);
  },

  register(username: string, email: string, password: string) {
    return api.post<UserWithContext>("/auth/register", { username, email, password }).then((r) => r.data);
  },

  fetchSession() {
    return api.get<UserWithContext>("/auth/session").then((r) => r.data);
  },

  refreshSession() {
    return api.post("/auth/refresh").then(() => {});
  },

  logout() {
    return api.post("/auth/logout").then(() => {});
  },
};
