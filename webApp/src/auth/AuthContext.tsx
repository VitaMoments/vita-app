import {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import { AuthService } from "../api/service/AuthService"
import { User } from "../api/types/userType"

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  refreshSession: () => Promise<void>
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkSession = async () => {
      try {
        const sessionUser = await AuthService.fetchSession();
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
    const user = await AuthService.login(email, password);
    setUser(user);
  };

  const logout = async () => {
      await AuthService.logout();
      setUser(null)
  };

  const register = async (email: string, password: string) => {
      const user = await AuthService.register(email, password);
      setUser(user)
  };

  const refreshSession = async () => {
      const user = await AuthService.refreshSession();
      setUser(user)
  }

  const value: AuthContextValue = {
    user,
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
