import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "../../../components/buttons/Button"
import { User } from "../../../data/user/userType"
import { useAuth } from "../../../auth/AuthContext";
import styles from "./Profile.module.css";

const Profile: React.FC = () => {
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

    return (
        <div>
            <div className={styles.content}>
                {error && <p style={{ color: "red" }}>{error}</p>}
                <h1>Profile</h1>
                {user.imageUrl ? (
                    <img
                      src={user.imageUrl}
                      alt={user.email}
                      className={styles.avatar}/>
                  ) : (
                    <div className={styles.avatar} />
                  )}
                <h4>{user.email}</h4>
                    {error && <div className={styles.error}>{error}</div>}
                <Button type="submit" className={styles.logoutBtn} disabled={loading} onClick={ ()=>handleLogout()}>
                    {loading ? "loading..." : "Logout"}
                </Button>
            </div>
        </div>
    );

}

export default Profile

