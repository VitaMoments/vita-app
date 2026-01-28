import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../components/buttons/Button";
import BaseDialog from "../../../components/dialog/BaseDialog";
import Tabs, { TabItem } from "../../../components/tabs/Tabs";
import ProfilePhotoUploader from "../../../components/image/ProfilePhotoUploader";

import { useAuth } from "../../../auth/AuthContext";
import type { User } from "../../../data/types";

import styles from "./Profile.module.css";

type ProfileTab = "info" | "friends" | "groups" | "settings";

// Als jouw auth-user altijd ACCOUNT is:
type AccountUser = User.ACCOUNT;

const Profile: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { user, logout, refreshSession } = useAuth();
  const [error, setError] = useState("");
  const [showEditImageDialog, setShowEditImageDialog] = useState(false);

  const navigate = useNavigate();

  // ✅ Narrow (als jouw session user altijd ACCOUNT is)
  const me = user as AccountUser;

  const handleLogout = async () => {
    setLoading(true);
    try {
      await logout();
      navigate("/", { replace: true });
    } catch (err: any) {
      console.error(err);
      setError(err?.response?.data?.message || "Er is iets misgegaan bij het uitloggen");
    } finally {
      setLoading(false);
    }
  };

  const tabs: TabItem<ProfileTab>[] = useMemo(
    () => [
      { value: "info", label: "Info", content: <div>Info</div> },
      { value: "friends", label: "Friends", content: <div>friends content</div> },
      { value: "groups", label: "Groups", content: <div>Groups content</div> },
      {
        value: "settings",
        label: "Settings",
        content: (
          <div>
            <Button
              type="button"
              className={styles.logoutBtn}
              disabled={loading}
              onClick={handleLogout}
            >
              {loading ? "loading..." : "Logout"}
            </Button>
          </div>
        ),
      },
    ],
    [loading]
  );

  return (
    <div>
      <div className={styles.content}>
        {error && <p style={{ color: "red" }}>{error}</p>}

        <BaseDialog
          open={showEditImageDialog}
          onClose={() => setShowEditImageDialog(false)}
          title="Edit Image"
          description=""
          footer={<div />}
        >
          <ProfilePhotoUploader
            avatarSize={512}
            onUpdatedUser={() => {
              refreshSession();
              setShowEditImageDialog(false);
            }}
          />
        </BaseDialog>

        <div className={styles.imageContainer}>
          {me.imageUrl ? (
            <img src={me.imageUrl} alt={me.email} className={styles.avatar} />
          ) : (
            <div className={styles.avatar} />
          )}

          <div className={styles.middle}>
            <div className={styles.text} onClick={() => setShowEditImageDialog(true)}>
              {me.displayName ?? me.username ?? "Edit"}
            </div>
          </div>
        </div>

        <h4>{me.email}</h4>
        <hr />

        <Tabs<ProfileTab> tabs={tabs} defaultValue="info" ariaLabel="Profile tabs" />
      </div>
    </div>
  );
};

export default Profile;
