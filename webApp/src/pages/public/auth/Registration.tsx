import React, { useState } from "react";
import { useAuth } from "../../../auth/AuthContext";
import styles from "./Auth.module.css";
import { Link, useNavigate } from "react-router-dom"

import { Button } from "../../../components/buttons/Button"
import Input, { PasswordInput } from "../../../components/input/Input"

import logoUrl from "../../../assets/logo.png";

const Registration: React.FC = () => {
    const [username, setUsername] = useState("")
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [error, setError] = useState("");
    const [pwError, setPwError] = useState<string[] | null>(null);
    const [loading, setLoading] = useState(false);

    const { register } = useAuth()
    const navigate = useNavigate();

    const hasBothPasswords = password.length > 0 && confirmPassword.length > 0;

    function validatePassword(pw: string, confirmPw: string): string[] {
      const errors: string[] = [];
      if (pw.length < 6) errors.push("Wachtwoord moet minimaal 6 tekens lang zijn.");
      if (!/[A-Z]/.test(pw)) errors.push("Wachtwoord moet minstens 1 hoofdletter bevatten.");
      if (!/[0-9]/.test(pw)) errors.push("Wachtwoord moet minstens 1 cijfer bevatten.");
      if (!/[^\w\s]/.test(pw)) errors.push("Wachtwoord moet minstens 1 leesteken bevatten (bijv. !, ?, .).");
      if (confirmPw.length > 0 && pw !== confirmPw) {errors.push("Wachtwoorden komen niet overeen.")}
      return errors;
    }

    const onPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const nextPw = e.target.value;
        setPassword(nextPw);

        const errors = validatePassword(nextPw, confirmPassword);
        setPwError(errors.length ? errors : null);
    };

    const onPasswordRepeatChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const nextConfirm = e.target.value;
        setConfirmPassword(nextConfirm);

        const errors = validatePassword(password, nextConfirm);
        setPwError(errors.length ? errors : null);
    };

    const handleSubmit = async (e: React.FormEvent) => {
      e.preventDefault();
      setError("");

      const errors = validatePassword(password, confirmPassword);
      setPwError(errors.length ? errors : null);
      if (errors.length) return;

      setLoading(true);
      try {
        await register(username, email, password);
        navigate("/portal");
      } catch (err: any) {
        console.error(err);
        setError(err.response?.data?.message || "Er is iets misgegaan bij registratie");
      } finally {
        setLoading(false);
      }
    };

  return (
    <div className={styles.container}>
    <img src={logoUrl} className={styles.logo} alt="Logo" />
      <h2 className={styles.title}>Account aanmaken</h2>
      <p className={styles.subtitle}>
        Maak een account aan om toegang te krijgen tot je persoonlijke vita account.
      </p>
      {error && <div className={styles.error}>{error}</div>}
      {pwError?.length ? (
           <div className={styles.error}>
              <ul style={{ marginTop: 8 }}>
                {pwError.map((msg, i) => (
                  <li key={i}>{msg}</li>
                ))}
              </ul>
           </div>
      ) : null}

    <form onSubmit={handleSubmit}>
        <Input
            placeholder="Username"
            name="Username"
            value={username}
            autoComplete="username"
            onChange={(e) => setUsername(e.target.value)}
            required/>
        <Input
            placeholder="Email"
            type="email"
            name="email"
            value={email}
            autoComplete="email"
            onChange={(e) => setEmail(e.target.value)}
            required/>
        <PasswordInput
            type="password"
            value={password}
            placeholder="Password"
            onChange={onPasswordChange}
            required />
        <PasswordInput
            type="password"
            value={confirmPassword}
            placeholder="Repeat password"
            onChange={onPasswordRepeatChange}
            required />
        <Button type="submit" disabled={loading || ((pwError?.length ?? 0) > 0 || !hasBothPasswords ) }>
          {loading ? "Bezig met registreren..." : "Account aanmaken"}
        </Button>
      </form>

      <hr className={styles.divider} />
      <div className={styles.link}>
        <Link to="/login">
          Al een account? Log hier in
        </Link>
      </div>
    </div>
  );
};

export default Registration;