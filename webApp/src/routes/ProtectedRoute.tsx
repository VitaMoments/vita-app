// src/auth/ProtectedRoute.tsx
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export const ProtectedRoute = () => {
  const { user, loading } = useAuth();

  if (loading) return <div>Even laden...</div>;
  if (!user) return <Navigate to="/login" replace />;

  return <Outlet />;
};

export const AuthRoute = () => {
  const { user, loading } = useAuth();

  if (loading) return <div />;
  if (user) return <Navigate to="/" replace />;

  return <Outlet />;
};
