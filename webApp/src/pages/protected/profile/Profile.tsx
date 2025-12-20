import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "../../../components/buttons/Button"
import { User } from "../../../data/user/userType"
import { useAuth } from "../../../auth/AuthContext";
import Tabs, { TabItem }  from "../../../components/tabs/Tabs"
import Info from "./Info"

import styles from "./Profile.module.css";

type ProfileTab = "info" | "friends" | "groups" | "settings";

const Profile: React.FC = () => {
    const [loading, setLoading] = useState(false)
    const {user, logout} = useAuth()
    const [error, setError] = useState("")
    const navigate = useNavigate()
    const tabs: TabItem<ProfileTab>[] = [
        { value: "info", label: "Info", content: <Info /> },
        { value: "friends", label: "Friends", content: <div>Friends content</div> },
        { value: "groups", label: "Groups", content: <div>Groups content</div> },
        { value: "settings", label: "Settings", content: <div>
            <Button type="submit"
            className={styles.logoutBtn}
            disabled={loading}
            onClick={ ()=>handleLogout()}>
                  {loading ? "loading..." : "Logout"}
              </Button>
            </div> },
    ];

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
                {user.imageUrl ? (
                    <img
                      src={user.imageUrl}
                      alt={user.email}
                      className={styles.avatar}/>
                  ) : (
                    <div className={styles.avatar} />
                  )}
                <p>{user.imageUrl}</p>
                <h4>{user.email}</h4>
                <hr />
                <Tabs tabs={tabs} defaultValue="info" ariaLabel="Profile tabs" />
            </div>
        </div>
    );

}

export default Profile

