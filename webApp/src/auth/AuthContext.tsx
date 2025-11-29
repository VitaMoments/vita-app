// src/auth/AuthContext.tsx
import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import {
  login as apiLogin,
  register as apiRegister,
  fetchSession,
  logoutRequest,
  UserDto,
} from "../api/authApi";

export type User = UserDto; // of je eigen User type

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkSession = async () => {
      try {
        const sessionUser = await fetchSession();
        setUser(sessionUser);
      } catch (err: any) {
        if (err?.response?.status !== 401) {
          console.error("Auth session check failed", err);
        }
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    checkSession();
  }, []);

  const login = async (email: string, password: string) => {
    const user = await apiLogin(email, password);
    setUser(user);
  };

  const logout = async () => {
      await logoutRequest();
      setUser(null)
  };

  const register = async (email: string, password, string) => {
      const user = await apiRegister(email, password);
      setUser(user)
  };

  const value: AuthContextValue = {
    user,
    loading,
    register,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
