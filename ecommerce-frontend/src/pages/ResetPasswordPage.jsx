import { useMemo, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../api/authApi";
import styles from "../styles/auth.module.css";

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = useMemo(() => searchParams.get("token") || "", [searchParams]);
  const [form, setForm] = useState({ password: "", confirmPassword: "" });
  const [errors, setErrors] = useState({});
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "", general: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess("");

    if (!token) {
      setErrors({ general: "Password reset link is missing a token" });
      return;
    }

    if (form.password !== form.confirmPassword) {
      setErrors({ confirmPassword: "Passwords do not match" });
      return;
    }

    setLoading(true);
    try {
      await resetPassword({ token, password: form.password });
      setSuccess("Password updated. Redirecting to sign in...");
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      const data = err.response?.data;
      if (!err.response) {
        setErrors({ general: "Backend server is not running on port 8081" });
      } else if (typeof data === "object") {
        setErrors(data);
      } else {
        setErrors({ general: "Password reset link is invalid or expired" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <section className={styles.brandPanel}>
        <span className={styles.brandMark}>Mercato</span>
        <div>
          <p className={styles.brandKicker}>New credentials</p>
          <h1 className={styles.brandTitle}>Choose a fresh password and keep moving.</h1>
        </div>
        <div className={styles.brandStats}>
          <div><strong>6+</strong><span>characters</span></div>
          <div><strong>Now</strong><span>updated instantly</span></div>
        </div>
      </section>

      <section className={styles.formPanel}>
        <div className={styles.formHeader}>
          <p className={styles.eyebrow}>Reset password</p>
          <h2 className={styles.title}>Set a new password</h2>
          <p className={styles.subtitle}>Use a password you have not used before.</p>
        </div>

        {errors.general && <p className={styles.errorBanner}>{errors.general}</p>}
        {errors.error && <p className={styles.errorBanner}>{errors.error}</p>}
        {success && <p className={styles.successBanner}>{success}</p>}

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>New password</label>
            <input name="password" type="password" placeholder="At least 6 characters" value={form.password} onChange={handleChange} />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <div className={styles.field}>
            <label>Confirm password</label>
            <input name="confirmPassword" type="password" placeholder="Repeat password" value={form.confirmPassword} onChange={handleChange} />
            {errors.confirmPassword && <span className={styles.error}>{errors.confirmPassword}</span>}
          </div>
          <button type="submit" className={styles.btn} disabled={loading || !token}>
            {loading ? "Saving..." : "Update password"}
          </button>
        </form>

        <p className={styles.switchLink}>
          Need another link? <Link to="/forgot-password">Start again</Link>
        </p>
      </section>
    </div>
  );
}
