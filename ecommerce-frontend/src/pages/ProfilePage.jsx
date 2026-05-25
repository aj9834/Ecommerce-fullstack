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

export default function ProfilePage() {
  const { user, updateUser } = useAuth();
  const [profile, setProfile] = useState(user);
  const [form, setForm] = useState({ ...emptyForm, name: user?.name || "", email: user?.email || "" });
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
                <input
                  name="currentPassword"
                  type="password"
                  placeholder="Required for password change"
                  value={form.currentPassword}
                  onChange={handleChange}
                />
              </div>

              <div className={styles.field}>
                <label>New password</label>
                <input
                  name="newPassword"
                  type="password"
                  placeholder="At least 6 characters"
                  value={form.newPassword}
                  onChange={handleChange}
                />
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
