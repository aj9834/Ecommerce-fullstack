import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/auth.module.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await loginUser(form);
      const { token, ...userData } = res.data;
      login(userData, token);
      navigate("/dashboard");
    } catch (err) {
      const data = err.response?.data;
      if (!err.response) {
        setErrors({ general: "Backend server is not running on port 8081" });
      } else if (data && typeof data === "object") {
        setErrors(data.error ? { general: data.error } : data);
      } else {
        setErrors({ general: "Invalid email or password" });
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
          <p className={styles.brandKicker}>Curated commerce desk</p>
          <h1 className={styles.brandTitle}>Manage buying, browsing, and carts from one sharp place.</h1>
        </div>
        <div className={styles.brandStats}>
          <div><strong>5</strong><span>starter products</span></div>
          <div><strong>24h</strong><span>secure session</span></div>
        </div>
      </section>

      <section className={styles.formPanel}>
        <div className={styles.formHeader}>
          <p className={styles.eyebrow}>Sign in</p>
          <h2 className={styles.title}>Welcome back</h2>
          <p className={styles.subtitle}>Use your account details to continue.</p>
        </div>
        {errors.general && <p id="login-error" className={styles.errorBanner}>{errors.general}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="login-email">Email</label>
            <input id="login-email" name="email" type="email" placeholder="admin@example.com" value={form.email} onChange={handleChange} required />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label htmlFor="login-password">Password</label>
            <input id="login-password" name="password" type="password" placeholder="Enter password" value={form.password} onChange={handleChange} required />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <div className={styles.formMeta}>
            <Link to="/forgot-password">Forgot password?</Link>
          </div>
          <button id="login-submit" type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Checking..." : "Continue"}
          </button>
        </form>
        <p className={styles.switchLink}>
          New here? <Link to="/register">Create an account</Link>
        </p>
      </section>
    </div>
  );
}
