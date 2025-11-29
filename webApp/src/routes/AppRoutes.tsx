// src/routes/AppRoutes.tsx
import { Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute, AuthRoute } from "./ProtectedRoute";

// Public
import PublicHome from "../pages/public/home/Home";
import Registration from "../pages/public/auth/Registration";
import Login from "../pages/public/auth/Login";

// Private
import Home from "../pages/portal/home/Home";
import Profile from "../pages/portal/profile/Profile";

export default function AppRoutes() {
  return (
    <Routes>
        <Route element={<AuthRoute />}>
            <Route index element={<PublicHome />} />
            <Route path="/registration" element={<Registration />} />
            <Route path="/login" element={<Login />} />
          </Route>

          <Route path="/portal" element={<ProtectedRoute />}>
            <Route index element={<Home />} />
            <Route path="profile" element={<Profile />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
