import { useEffect, useState } from "react";
import { getProfile, updateProfile } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/profile.module.css";

const emptyForm = {
  name: "",
  email: "",
  currentPassword: "",
  newPassword: "",
};

const formatDate = (value) =>
  value
    ? new Date(value).toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "Not available";

const getInitials = (name = "") =>
  name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0]?.toUpperCase())
    .join("") || "U";

const normalizeErrors = (data) => {
  if (!data || typeof data !== "object") return { general: "Profile update failed." };
  if (data.error) return { general: data.error };
  return data;
};

const EyeIcon = ({ hidden }) => (
  <svg aria-hidden="true" viewBox="0 0 24 24" focusable="false">
    {hidden ? (
      <>
        <path d="M3 3l18 18" />
        <path d="M10.7 10.7a2 2 0 0 0 2.6 2.6" />
        <path d="M9.9 5.2A9.6 9.6 0 0 1 12 5c5 0 8.5 4.4 9.7 6.2a1.5 1.5 0 0 1 0 1.6 17.9 17.9 0 0 1-2.4 3" />
        <path d="M6.6 6.8a18.5 18.5 0 0 0-4.3 4.4 1.5 1.5 0 0 0 0 1.6C3.5 14.6 7 19 12 19a9.3 9.3 0 0 0 4.3-1" />
      </>
    ) : (
      <>
        <path d="M2.3 11.2C3.5 9.4 7 5 12 5s8.5 4.4 9.7 6.2a1.5 1.5 0 0 1 0 1.6C20.5 14.6 17 19 12 19s-8.5-4.4-9.7-6.2a1.5 1.5 0 0 1 0-1.6Z" />
        <path d="M12 9a3 3 0 1 1 0 6 3 3 0 0 1 0-6Z" />
      </>
    )}
  </svg>
);

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState(user);
  const [form, setForm] = useState({ ...emptyForm, name: user?.name || "", email: user?.email || "" });
  const [visiblePasswords, setVisiblePasswords] = useState({ currentPassword: false, newPassword: false });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState("");

  useEffect(() => {
    getProfile()
      .then((res) => {
        const { token, ...profileData } = res.data;
        setProfile(profileData);
        setForm({ ...emptyForm, name: profileData.name || "", email: profileData.email || "" });
        updateUser(profileData, token);
      })
      .catch(() => setErrors({ general: "Failed to load profile." }))
      .finally(() => setLoading(false));
  }, [updateUser]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "", general: "" });
    setSuccess("");
  };

  const togglePasswordVisibility = (fieldName) => {
    setVisiblePasswords((current) => ({ ...current, [fieldName]: !current[fieldName] }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setErrors({});
    setSuccess("");

    try {
      const payload = {
        name: form.name,
        email: form.email,
        currentPassword: form.currentPassword || null,
        newPassword: form.newPassword || null,
      };
      const res = await updateProfile(payload);
      const { token, ...profileData } = res.data;

      setProfile(profileData);
      setForm({ ...emptyForm, name: profileData.name || "", email: profileData.email || "" });
      updateUser(profileData, token);
      setSuccess("Profile updated.");
    } catch (err) {
      setErrors(normalizeErrors(err.response?.data));
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className={styles.center}>Loading profile...</div>;

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div>
          <p className={styles.eyebrow}>Account</p>
          <h1>Profile</h1>
        </div>
        <span className={styles.role}>{profile?.role}</span>
      </div>

      {errors.general && <div className={styles.errorBanner}>{errors.general}</div>}
      {success && <div className={styles.success}>{success}</div>}

      <div className={styles.grid}>
        <aside className={styles.summary}>
          <div className={styles.avatar}>{getInitials(profile?.name)}</div>
          <h2>{profile?.name}</h2>
          <p>{profile?.email}</p>

          <div className={styles.metaList}>
            <div>
              <span>User ID</span>
              <strong>#{profile?.userId}</strong>
            </div>
            <div>
              <span>Joined</span>
              <strong>{formatDate(profile?.createdAt)}</strong>
            </div>
            <div>
              <span>Last updated</span>
              <strong>{formatDate(profile?.updatedAt)}</strong>
            </div>
          </div>
        </aside>

        <section className={styles.panel}>
          <div className={styles.panelHeader}>
            <h2>Update profile</h2>
            <p>Change your name, email, or password.</p>
          </div>

          <form className={styles.form} onSubmit={handleSubmit}>
            <div className={styles.field}>
              <label>Full name</label>
              <input name="name" value={form.name} onChange={handleChange} />
              {errors.name && <span>{errors.name}</span>}
            </div>

            <div className={styles.field}>
              <label>Email</label>
              <input name="email" type="email" value={form.email} onChange={handleChange} />
              {errors.email && <span>{errors.email}</span>}
            </div>

            <div className={styles.passwordGrid}>
              <div className={styles.field}>
                <label>Current password</label>
                <div className={styles.passwordControl}>
                  <input
                    name="currentPassword"
                    type={visiblePasswords.currentPassword ? "text" : "password"}
                    placeholder="Required for password change"
                    value={form.currentPassword}
                    onChange={handleChange}
                  />
                  <button
                    type="button"
                    className={styles.eyeButton}
                    onClick={() => togglePasswordVisibility("currentPassword")}
                    aria-label={`${visiblePasswords.currentPassword ? "Hide" : "Show"} current password`}
                    title={`${visiblePasswords.currentPassword ? "Hide" : "Show"} current password`}
                  >
                    <EyeIcon hidden={visiblePasswords.currentPassword} />
                  </button>
                </div>
              </div>

              <div className={styles.field}>
                <label>New password</label>
                <div className={styles.passwordControl}>
                  <input
                    name="newPassword"
                    type={visiblePasswords.newPassword ? "text" : "password"}
                    placeholder="At least 6 characters"
                    value={form.newPassword}
                    onChange={handleChange}
                  />
                  <button
                    type="button"
                    className={styles.eyeButton}
                    onClick={() => togglePasswordVisibility("newPassword")}
                    aria-label={`${visiblePasswords.newPassword ? "Hide" : "Show"} new password`}
                    title={`${visiblePasswords.newPassword ? "Hide" : "Show"} new password`}
                  >
                    <EyeIcon hidden={visiblePasswords.newPassword} />
                  </button>
                </div>
                {errors.newPassword && <span>{errors.newPassword}</span>}
              </div>
            </div>

            <div className={styles.actions}>
              <button type="button" className={styles.secondary} onClick={() => setForm({ ...emptyForm, name: profile?.name || "", email: profile?.email || "" })}>
                Reset
              </button>
              <button type="submit" className={styles.primary} disabled={saving}>
                {saving ? "Saving..." : "Save changes"}
              </button>
            </div>
          </form>
        </section>
      </div>
    </div>
  );
}
