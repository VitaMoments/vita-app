// src/api/axios.ts
import axios, { AxiosError, InternalAxiosRequestConfig } from "axios";

type RetryConfig = InternalAxiosRequestConfig & { _retry?: boolean };

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

let isRefreshing = false;
let subscribers: Array<(ok: boolean) => void> = [];

function subscribe(cb: (ok: boolean) => void) {
  subscribers.push(cb);
}

function notify(ok: boolean) {
  subscribers.forEach((cb) => cb(ok));
  subscribers = [];
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryConfig | undefined;

    if (!originalRequest || !error.response) return Promise.reject(error);

    if (error.response.status !== 401) return Promise.reject(error);

    const url = originalRequest.url ?? "";
    if (url.includes("/auth/refresh")) return Promise.reject(error);

    if (originalRequest._retry) return Promise.reject(error);
    originalRequest._retry = true;

    if (isRefreshing) {
      const ok = await new Promise<boolean>((resolve) => subscribe(resolve));
      return ok ? api(originalRequest) : Promise.reject(error);
    }

    isRefreshing = true;
    try {
      await api.post("/auth/refresh");
      notify(true);
      return api(originalRequest);
    } catch (refreshErr) {
      notify(false);
      return Promise.reject(refreshErr);
    } finally {
      isRefreshing = false;
    }
  }
);

export default api;