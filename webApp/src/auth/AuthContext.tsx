import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import { AuthService } from "../api/service/AuthService";
import { User, UserWithContext, StreakSummary } from "../data/types";
import { setOnAuthFailed } from "../api/axios";

type AuthContextValue = {
  user: User | null;
  streak: StreakSummary | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (
    username: string,
    email: string,
    password: string,
  ) => Promise<void>;
  refreshSession: () => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [streak, setStreak] = useState<StreakSummary | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setOnAuthFailed(() => {
      clearContext();
      //       window.location.href = "/login";
    });

    return () => setOnAuthFailed(null);
  }, []);

  function handleContext(context: UserWithContext) {
    setUser(context.user);
    console.log("Setting streak from context", context.streak);
    setStreak(context.streak ?? null);
  }

  function clearContext() {
    setUser(null);
    setStreak(null);
  }

  useEffect(() => {
    const checkSession = async () => {
      try {
        const context = await AuthService.fetchSession();
        handleContext(context);
      } catch (err: any) {
        if (err?.response?.status !== 401) {
          console.error("Auth session check failed", err);
        }
        clearContext();
      } finally {
        setLoading(false);
      }
    };

    checkSession();
  }, []);

  const login = async (email: string, password: string) => {
    const context = await AuthService.login(email, password);
    handleContext(context);
  };

  const logout = async () => {
    await AuthService.logout();
    clearContext();
  };

  const register = async (
    username: string,
    email: string,
    password: string,
  ) => {
    const context = await AuthService.register(username, email, password);
    handleContext(context);
  };

  const refreshSession = async () => {
    await AuthService.refreshSession();
    const context = await AuthService.fetchSession();
    handleContext(context);
  };

  const value: AuthContextValue = {
    user,
    streak,
    loading,
    register,
    login,
    logout,
    refreshSession,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
