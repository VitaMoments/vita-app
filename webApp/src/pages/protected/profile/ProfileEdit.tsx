import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

import { Button } from "../../../components/buttons/Button";
import Input from "../../../components/input/Input";
import { useAuth } from "../../../auth/AuthContext";
import { UserService } from "../../../api/service/UserService";
import type { PrivacyStatus, User } from "../../../data/types";

import styles from "./ProfileEdit.module.css";

type AccountUser = User.ACCOUNT;

type EditableProfileFields = {
  alias: string;
  bio: string;
  firstname: string;
  lastname: string;
  phone: string;
  birthDate: string;
  locale: string;
  timeZone: string;
  privacyDetails: PrivacyStatus;
};

const toDateInputValue = (value?: number | null): string => {
  if (!value) return "";
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return "";
  return d.toISOString().slice(0, 10);
};

const ProfileEdit: React.FC = () => {
  const { user, refreshSession } = useAuth();
  const navigate = useNavigate();
  const me = user as AccountUser;

  const [form, setForm] = useState<EditableProfileFields>({
    alias: me.alias ?? "",
    bio: me.bio ?? "",
    firstname: me.firstname ?? "",
    lastname: me.lastname ?? "",
    phone: me.phone ?? "",
    birthDate: toDateInputValue(me.birthDate),
    locale: me.locale ?? "",
    timeZone: me.timeZone ?? "",
    privacyDetails: me.privacyDetails ?? "PRIVATE",
  });

  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onChange =
    <K extends keyof EditableProfileFields>(key: K) =>
    (
      e: React.ChangeEvent<
        HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement
      >,
    ) => {
      setForm((prev) => ({
        ...prev,
        [key]: e.target.value,
      }));
    };

  const toNullable = (newValue: string): string | null => {
    const normalized = newValue.trim();
    return normalized.length > 0 ? normalized : null;
  };

  const handleSave = async () => {
    setSaving(true);
    setError(null);

    try {
      await UserService.updateMyAccount({
        alias: toNullable(form.alias),
        bio: toNullable(form.bio),
        firstname: toNullable(form.firstname),
        lastname: toNullable(form.lastname),
        phone: toNullable(form.phone),
        birthDate: toNullable(form.birthDate),
        locale: toNullable(form.locale),
        timeZone: toNullable(form.timeZone),
        privacyDetails: form.privacyDetails,
      });

      await refreshSession();
      navigate("/profile", { replace: true });
    } catch (err: any) {
      const message =
        err?.response?.data?.message ||
        (typeof err?.response?.data === "string" ? err.response.data : null) ||
        err?.message ||
        "Opslaan mislukt";
      setError(String(message));
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.header}>
          <h2 className={styles.title}>Edit profile info</h2>
          <p className={styles.help}>
            Velden zijn vooraf ingevuld. Maak een veld leeg om de waarde te
            verwijderen.
          </p>
        </div>

        {error && <p className={styles.error}>{error}</p>}

        <Input
          label="Alias"
          placeholder="Alias"
          value={form.alias}
          onChange={onChange("alias")}
        />

        <div className={styles.formGroup}>
          <label htmlFor="bio">Bio</label>
          <textarea
            id="bio"
            placeholder="Bio"
            value={form.bio}
            onChange={onChange("bio")}
            rows={4}
            className={styles.textarea}
          />
        </div>

        <Input
          label="First name"
          placeholder="First name"
          value={form.firstname}
          onChange={onChange("firstname")}
        />
        <Input
          label="Last name"
          placeholder="Last name"
          value={form.lastname}
          onChange={onChange("lastname")}
        />
        <Input
          label="Phone"
          placeholder="Phone"
          value={form.phone}
          onChange={onChange("phone")}
        />
        <Input
          label="Birth date"
          type="date"
          value={form.birthDate}
          onChange={onChange("birthDate")}
        />
        <Input
          label="Locale"
          placeholder="bijv. nl-NL"
          value={form.locale}
          onChange={onChange("locale")}
        />
        <Input
          label="Time zone"
          placeholder="bijv. Europe/Amsterdam"
          value={form.timeZone}
          onChange={onChange("timeZone")}
        />

        <div className={styles.formGroup}>
          <label htmlFor="privacyDetails">Privacy details</label>
          <select
            id="privacyDetails"
            value={form.privacyDetails}
            onChange={onChange("privacyDetails")}
            className={styles.select}
          >
            <option value="PRIVATE">PRIVATE</option>
            <option value="FRIENDS_ONLY">FRIENDS_ONLY</option>
            <option value="OPEN">OPEN</option>
          </select>
        </div>

        <div className={styles.actions}>
          <Button
            type="button"
            className={styles.secondaryBtn}
            onClick={() => navigate("/profile")}
          >
            Cancel
          </Button>
          <Button type="button" disabled={saving} onClick={handleSave}>
            {saving ? "Saving..." : "Save"}
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ProfileEdit;
