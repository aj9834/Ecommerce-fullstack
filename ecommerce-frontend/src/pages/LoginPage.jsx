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
      } else if (typeof data === "object") {
        setErrors(data);
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
        {errors.general && <p className={styles.errorBanner}>{errors.general}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Email</label>
            <input name="email" type="email" placeholder="admin@example.com" value={form.email} onChange={handleChange} />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label>Password</label>
            <input name="password" type="password" placeholder="Enter password" value={form.password} onChange={handleChange} />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
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
