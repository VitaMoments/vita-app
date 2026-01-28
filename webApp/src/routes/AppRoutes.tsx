// src/routes/AppRoutes.tsx
import React, { Suspense, lazy } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { ProtectedRoute, AuthRoute } from "./ProtectedRoute";
import { useAuth } from "../auth/AuthContext";

// Public (lazy)
const PublicHome = lazy(() => import("../pages/public/home/Home"));
const Registration = lazy(() => import("../pages/public/auth/Registration"));
const Login = lazy(() => import("../pages/public/auth/Login"));
const Privacy = lazy(() => import("../pages/public/privacy/Privacy"));
const Terms = lazy(() => import("../pages/public/terms/Terms"));

// Private (lazy)
const Home = lazy(() => import("../pages/protected/home/Home"));
const Profile = lazy(() => import("../pages/protected/profile/Profile"));
const FriendsPage = lazy(() => import("../pages/protected/friends/FriendsPage"));
const BlogsPage = lazy(() => import("../pages/protected/blog/BlogsPage"));
const CreateBlogPage = lazy(() => import("../pages/protected/blog/CreateBlogPage"));

function RootGate() {
  const { user, loading } = useAuth();
  if (loading) return <div>Loading...</div>;
  return user ? <Home /> : <PublicHome />;
}

function LoadingFallback() {
  return <div>Loading...</div>;
}

export default function AppRoutes() {
  return (
    <Suspense fallback={<LoadingFallback />}>
      <Routes>
        <Route path="/" element={<RootGate />} />

        <Route element={<AuthRoute />}>
          <Route path="/registration" element={<Registration />} />
          <Route path="/login" element={<Login />} />
        </Route>

        <Route element={<ProtectedRoute />}>
          <Route path="/profile" element={<Profile />} />
          <Route path="/friends" element={<FriendsPage />} />
          <Route path="/blogs" element={<BlogsPage />} />
          <Route path="/blogs/create" element={<CreateBlogPage />} />
        </Route>

        <Route path="/privacy" element={<Privacy />} />
        <Route path="/terms" element={<Terms />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}
