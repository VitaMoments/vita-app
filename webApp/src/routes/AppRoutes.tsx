// src/routes/AppRoutes.tsx
import { Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute, AuthRoute } from "./ProtectedRoute";
import { useAuth } from "../auth/AuthContext";

// Public
import PublicHome from "../pages/public/home/Home";
import Registration from "../pages/public/auth/Registration";
import Login from "../pages/public/auth/Login";
import Privacy from "../pages/public/privacy/Privacy"
import Terms from "../pages/public/terms/Terms"

// Private
import Home from "../pages/protected/home/Home";
import Profile from "../pages/protected/profile/Profile";
import FriendsPage from "../pages/protected/friends/FriendsPage";

function RootGate() {
    const { user, loading } = useAuth();
    if (loading) return <div>Loading... </div>;
    return user ? <Home /> : <PublicHome />;
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<RootGate />} />

      <Route element={<AuthRoute />}>
        <Route path="/registration" element={<Registration />} />
        <Route path="/login" element={<Login />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route path="/profile" element={<Profile />} />
        <Route path="/friends" element={<FriendsPage />} />
      </Route>

      <Route path="/privacy" element={<Privacy />} />
      <Route path="/terms" element={<Terms />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}