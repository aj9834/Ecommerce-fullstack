import { useState } from "react";
import { Link } from "react-router-dom";
import { forgotPassword } from "../api/authApi";
import styles from "../styles/auth.module.css";

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setResult(null);
    setLoading(true);

    try {
      const res = await forgotPassword({ email });
      setResult(res.data);
    } catch (err) {
      const data = err.response?.data;
      if (!err.response) {
        setError("Backend server is not running on port 8081");
      } else if (data?.email) {
        setError(data.email);
      } else {
        setError(data?.error || "Could not send reset email");
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
          <p className={styles.brandKicker}>Account recovery</p>
          <h1 className={styles.brandTitle}>Get a secure path back into your shopping desk.</h1>
        </div>
        <div className={styles.brandStats}>
          <div><strong>30m</strong><span>reset window</span></div>
          <div><strong>1x</strong><span>single-use token</span></div>
        </div>
      </section>

      <section className={styles.formPanel}>
        <div className={styles.formHeader}>
          <p className={styles.eyebrow}>Forgot password</p>
          <h2 className={styles.title}>Reset your password</h2>
          <p className={styles.subtitle}>Enter the email on your account to generate a reset link.</p>
        </div>

        {error && <p className={styles.errorBanner}>{error}</p>}
        {result && (
          <div className={styles.successBanner}>
            <p>{result.message}</p>
            {result.resetLink && <Link to={result.resetLink.replace(window.location.origin, "")}>Open reset page</Link>}
          </div>
        )}

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Email</label>
            <input name="email" type="email" placeholder="you@example.com" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Generating..." : "Send reset link"}
          </button>
        </form>

        <p className={styles.switchLink}>
          Remembered it? <Link to="/login">Sign in</Link>
        </p>
      </section>
    </div>
  );
}
