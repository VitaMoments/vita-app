import React from "react";
import Cropper, { Area } from "react-easy-crop";
import { UserService } from "../../api/service/UserService";
import { UserDto } from "../../types/userType";
import styles from "./ProfilePhotoUploader.module.css";

type Props = {
  avatarSize?: number; // default 512 (server resizet alsnog)
  onUpdatedUser?: (user: UserDto) => void; // handig om state te updaten
};

const ACCEPTED_MIME = new Set(["image/jpeg", "image/png"]);

const ProfilePhotoUploader: React.FC<Props> = ({ avatarSize = 512, onUpdatedUser }) => {
  const [file, setFile] = React.useState<File | null>(null);
  const [imageUrl, setImageUrl] = React.useState<string | null>(null);

  const [crop, setCrop] = React.useState({ x: 0, y: 0 });
  const [zoom, setZoom] = React.useState(1);

  const [croppedAreaPixels, setCroppedAreaPixels] = React.useState<Area | null>(null);

  const [uploading, setUploading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);

  React.useEffect(() => {
    if (!file) {
      setImageUrl(null);
      return;
    }
    const url = URL.createObjectURL(file);
    setImageUrl(url);
    return () => URL.revokeObjectURL(url);
  }, [file]);

  const onPickFile = (e: React.ChangeEvent<HTMLInputElement>) => {
    setError(null);

    const f = e.target.files?.[0] ?? null;
    if (!f) return;

    if (!ACCEPTED_MIME.has(f.type)) {
      setError("Alleen JPG/JPEG en PNG zijn toegestaan.");
      return;
    }

    setFile(f);
    setCrop({ x: 0, y: 0 });
    setZoom(1);
    setCroppedAreaPixels(null);
  };

  const onCropComplete = React.useCallback((_area: Area, areaPixels: Area) => {
    // pixels in originele image-coördinaten
    setCroppedAreaPixels(areaPixels);
  }, []);

  const upload = async () => {
    if (!file || !croppedAreaPixels) {
      setError("Kies een foto en maak een uitsnede.");
      return;
    }

    setUploading(true);
    setError(null);

    try {
      const updatedUser = await UserService.uploadProfilePhoto({
        file,
        cropX: croppedAreaPixels.x,
        cropY: croppedAreaPixels.y,
        cropW: croppedAreaPixels.width,
        cropH: croppedAreaPixels.height,
        avatarSize,
      });

      onUpdatedUser?.(updatedUser);

      // optioneel: reset
      // setFile(null);
      // setImageUrl(null);
    } catch (e: any) {
      // Axios error: backend stuurt soms string terug
      const msg =
        e?.response?.data?.message ||
        (typeof e?.response?.data === "string" ? e.response.data : null) ||
        e?.message ||
        "Upload mislukt.";

      setError(String(msg));
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.row}>
        <label className={styles.fileLabel}>
          Kies profielfoto (JPG/PNG)
          <input
            className={styles.fileInput}
            type="file"
            accept="image/jpeg,image/jpg,image/png"
            onChange={onPickFile}
          />
        </label>

        <button
          type="button"
          className={styles.uploadBtn}
          onClick={upload}
          disabled={!file || !croppedAreaPixels || uploading}
        >
          {uploading ? "Uploaden..." : "Opslaan"}
        </button>
      </div>

      {imageUrl && (
        <div className={styles.cropArea}>
          <Cropper
            image={imageUrl}
            crop={crop}
            zoom={zoom}
            aspect={1}
            onCropChange={setCrop}
            onZoomChange={setZoom}
            onCropComplete={onCropComplete}
            restrictPosition={false}
          />
        </div>
      )}

      {imageUrl && (
        <div className={styles.zoomRow}>
          <span className={styles.zoomLabel}>Zoom</span>
          <input
            className={styles.zoom}
            type="range"
            min={1}
            max={3}
            step={0.01}
            value={zoom}
            onChange={(e) => setZoom(Number(e.target.value))}
          />
        </div>
      )}

      {error && <div className={styles.error}>{error}</div>}
    </div>
  );
};

export default ProfilePhotoUploader;