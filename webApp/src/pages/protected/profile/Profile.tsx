import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../components/buttons/Button";
import BaseDialog from "../../../components/dialog/BaseDialog";
import Tabs, { TabItem } from "../../../components/tabs/Tabs";
import ProfilePhotoUploader from "../../../components/image/ProfilePhotoUploader";

import { getUserProfileImageUrl } from "../../../data/ui/userHelpers";

import { useAuth } from "../../../auth/AuthContext";
import type { MediaAssetResponse, User } from "../../../data/types";

import styles from "./Profile.module.css";

type ProfileTab = "info" | "settings";
type AccountUser = User.ACCOUNT;

const formatBirthDate = (value?: number | null): string => {
  if (!value) return "-";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "-";
  return d.toLocaleDateString();
};

const showOrDash = (value?: string | null): string => {
  const normalized = value?.trim();
  return normalized && normalized.length > 0 ? normalized : "-";
};

const InfoRow = ({ label, value }: { label: string; value: string }) => (
  <div className={styles.infoRow}>
    <span className={styles.infoLabel}>{label}</span>
    <span className={styles.infoValue}>{value}</span>
  </div>
);

const Profile: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const { user, logout, refreshSession } = useAuth();
  const [error, setError] = useState("");
  const [showEditImageDialog, setShowEditImageDialog] = useState(false);
  const [uploadedProfileImage, setUploadedProfileImage] =
    useState<MediaAssetResponse | null>(null);

  const navigate = useNavigate();
  const me = user as AccountUser;

  const handleLogout = async () => {
    setLoading(true);
    try {
      await logout();
      navigate("/", { replace: true });
    } catch (err: any) {
      console.error(err);
      setError(
        err?.response?.data?.message ||
          "Er is iets misgegaan bij het uitloggen"
      );
    } finally {
      setLoading(false);
    }
  };

  const displayedProfileImageUrl =
    uploadedProfileImage?.url ?? getUserProfileImageUrl(me) ?? null;

  const infoContent = (
    <div className={styles.infoSection}>
      <div className={styles.infoList}>
        <InfoRow label="Username" value={showOrDash(me.username)} />
        <InfoRow label="Email" value={showOrDash(me.email)} />
        <InfoRow label="Alias" value={showOrDash(me.alias)} />
        <InfoRow label="Bio" value={showOrDash(me.bio)} />
        <InfoRow label="First name" value={showOrDash(me.firstname)} />
        <InfoRow label="Last name" value={showOrDash(me.lastname)} />
        <InfoRow label="Phone" value={showOrDash(me.phone)} />
        <InfoRow label="Birth date" value={formatBirthDate(me.birthDate)} />
        <InfoRow label="Locale" value={showOrDash(me.locale)} />
        <InfoRow label="Time zone" value={showOrDash(me.timeZone)} />
        <InfoRow
          label="Privacy"
          value={showOrDash(me.privacyDetails)}
        />
      </div>

      <Button
        type="button"
        className={styles.primaryAction}
        onClick={() => navigate("/profile/edit")}
      >
        Edit profile
      </Button>
    </div>
  );

  const tabs: TabItem<ProfileTab>[] = useMemo(
    () => [
      { value: "info", label: "Info", content: infoContent },
      {
        value: "settings",
        label: "Settings",
        content: (
          <div className={styles.settings}>
            <Button
              type="button"
              className={styles.logoutBtn}
              disabled={loading}
              onClick={handleLogout}
            >
              {loading ? "Loading..." : "Logout"}
            </Button>
          </div>
        ),
      },
    ],
    [infoContent, loading]
  );

  return (
    <div className={styles.page}>
      <div className={styles.content}>
        {error && <p className={styles.error}>{error}</p>}

        <BaseDialog
          open={showEditImageDialog}
          onClose={() => setShowEditImageDialog(false)}
          title="Edit profile image"
          description=""
          footer={<div />}
        >
          <ProfilePhotoUploader
            userId={me.uuid}
            avatarSize={512}
            privacy={me.privacyDetails ?? "PRIVATE"}
            onUploadedMedia={(media) => {
              setUploadedProfileImage(media);
              void refreshSession().catch((err) => {
                console.error(
                  "Failed to refresh auth session after image upload",
                  err
                );
              });
              setShowEditImageDialog(false);
            }}
          />
        </BaseDialog>

        <div className={styles.header}>
          <div className={styles.imageContainer}>
            {displayedProfileImageUrl ? (
              <img
                src={displayedProfileImageUrl}
                alt={me.displayName ?? me.email ?? me.username}
                className={styles.avatar}
              />
            ) : (
              <div className={styles.avatar} />
            )}

            <div className={styles.overlay} onClick={() => setShowEditImageDialog(true)}>
              Edit
            </div>
          </div>

          <div className={styles.userInfo}>
            <h2>{me.displayName ?? me.username}</h2>
            <p>{me.email}</p>
          </div>
        </div>

        <Tabs<ProfileTab> tabs={tabs} defaultValue="info" ariaLabel="Profile tabs" />
      </div>
    </div>
  );
};

export default Profile;