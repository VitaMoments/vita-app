// AuthService.ts
import api from "../axios";
import { User } from "../../data/types";

export const AuthService = {
  login(email: string, password: string) {
    return api.post<User.ACCOUNT>("/auth/login", { email, password }).then(r => r.data);
  },

  register(username: string, email: string, password: string) {
    return api.post<User.ACCOUNT>("/auth/register", { username, email, password }).then(r => r.data);
  },

  fetchSession() {
    return api.get<User.ACCOUNT>("/auth/session").then(r => r.data);
  },

  refreshSession() {
    return api.post("/auth/refresh").then(() => {});
  },

  logout() {
    return api.post("/auth/logout").then(() => {});
  },
};
