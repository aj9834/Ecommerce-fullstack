import { useNavigate, useSearchParams } from "react-router-dom";
import styles from "../styles/checkout.module.css";

export default function CheckoutSuccessPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const orderId = searchParams.get("orderId");
  const method = searchParams.get("method");
  const isOnline = method === "ONLINE";

  return (
    <div className={styles.page}>
      <section className={styles.successCard}>
        <div className={styles.successIcon} aria-hidden="true">✓</div>
        <h1>{isOnline ? "Payment successful" : "Order placed"}</h1>
        <p>
          Order #{orderId || "—"} has been confirmed
          {isOnline ? " and paid." : " with Cash on Delivery."}
        </p>
        <div className={styles.successActions}>
          <button onClick={() => navigate("/orders")}>View orders</button>
          <button className={styles.secondaryButton} onClick={() => navigate("/products")}>
            Continue shopping
          </button>
        </div>
      </section>
    </div>
  );
}
