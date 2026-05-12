import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/dashboard.module.css";

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  return (
    <div className={styles.page}>
      <div className={styles.hero}>
        <p className={styles.greeting}>Good day, {user?.name}</p>
        <h1 className={styles.heading}>What are you shopping for today?</h1>
        <p className={styles.sub}>
          Browse the catalog, manage your cart, and keep your store work moving.
        </p>
        <button className={styles.cta} onClick={() => navigate("/products")}>
          Browse products
        </button>
      </div>

      <div className={styles.cards}>
        <div className={styles.card} onClick={() => navigate("/products")}>
          <div className={styles.cardIcon}></div>
          <h3 className={styles.cardTitle}>Products</h3>
          <p className={styles.cardDesc}>Search and compare the full catalog</p>
        </div>
        <div className={styles.card} onClick={() => navigate("/cart")}>
          <div className={styles.cardIcon}></div>
          <h3 className={styles.cardTitle}>My cart</h3>
          <p className={styles.cardDesc}>Review quantities and checkout totals</p>
        </div>
        <div className={styles.card}>
          <div className={styles.cardIcon}></div>
          <h3 className={styles.cardTitle}>Orders</h3>
          <p className={styles.cardDesc}>Track purchase activity and history</p>
        </div>
      </div>
    </div>
  );
}
