// src/pages/public/auth/Login.tsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import styles from "./Auth.module.css";

import Input, { PasswordInput } from "../../../components/input/Input";

import { Button } from "../../../components/buttons/Button";
import { useAuth } from "../../../auth/AuthContext";

import logoUrl from "../../../assets/logo.png";

const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await login(email, password); // -> zet user in context
      navigate("/portal", { replace: true });
    } catch (err: any) {
      console.error(err);
      setError(
        err?.response?.data?.message || "Er is iets misgegaan bij het inloggen"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <img src={logoUrl} className={styles.logo} alt="Logo" />
      <h2 className={styles.title}>Inloggen</h2>
      <p className={styles.subtitle}>
        Log in om je persoonlijke health-portal te openen.
      </p>

      {error && <div className={styles.error}>{error}</div>}

      <form onSubmit={handleSubmit}>
        <Input
            placeholder="email"
            type="email"
            name="email"
            value={email}
            autoComplete="email"
            onChange={(e) => setEmail(e.target.value)}
            required/>
        <PasswordInput
            type="password"
            value={password}
            placeholder="password"
            onChange={(e)=> setPassword(e.target.value)}
            required />

        <Button type="submit" disabled={loading}>
          {loading ? "loading..." : "Login"}
        </Button>
      </form>

      <hr className={styles.divider} />

      <div className={styles.link}>
        <Link to="/registration">Nog geen account? Meld je hier aan</Link>
      </div>
    </div>
  );
};

export default Login;
