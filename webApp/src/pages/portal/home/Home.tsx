import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { User } from "../../../data/user/userType"
import { useAuth } from "../../../auth/AuthContext";
import styles from "./Home.module.css";

import { TimelineInput } from "../../../components/input/TimelineInput"
import { Button } from "../../../components/buttons/Button"

const Home: React.FC = () => {
    const [loading, setLoading] = useState(false)
    const {user, logout} = useAuth()
    const [error, setError] = useState("")
    const navigate = useNavigate()

    const handleLogout = async (e) => {
        setLoading(true)
        try {
            await logout()
            navigate("/", { replace: true })
        } catch (err: any) {
            console.error(err)
            setError(
                err?.response?.data?.message || "Er is iets misgegaan bij het uitloggen"
            );
        } finally {
            setLoading(false)
        }
    };

    if (loading) return (<div><p>loading...</p></div>);
  return (
    <div>
      <TimelineInput />
      <h1>Portal Home</h1>
      <h2>user</h2>
       {error && <div className={styles.error}>{error}</div>}
      <Button type="submit" disabled={loading} onClick={ ()=>handleLogout()}>
        {loading ? "loading..." : "Logout"}
      </Button>
    </div>
  );
}

export default Home