import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { getOrders } from "../api/OrderApi";
import styles from "../styles/orders.module.css";

const formatCurrency = (value) =>
  `Rs. ${Number(value || 0).toLocaleString("en-IN", { maximumFractionDigits: 2 })}`;

const formatDate = (value) =>
  value
    ? new Date(value).toLocaleString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      })
    : "";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const location = useLocation();
  const navigate = useNavigate();
  const message = location.state?.message;

  useEffect(() => {
    getOrders()
      .then((res) => setOrders(res.data))
      .catch(() => setError("Failed to load orders."))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div id="orders-loading" className={styles.center}>Loading orders...</div>;

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <h1 id="orders-title">My Orders</h1>
        <button onClick={() => navigate("/products")}>Shop More</button>
      </div>

      {message && <div id="orders-success" className={styles.success}>{message}</div>}
      {error && <div id="orders-error" className={styles.error}>{error}</div>}

      {!error && orders.length === 0 && (
        <div id="orders-empty" className={styles.empty}>
          <h2>No orders yet</h2>
          <p>Your completed checkout orders will appear here.</p>
          <button onClick={() => navigate("/products")}>Browse Products</button>
        </div>
      )}

      <div className={styles.list}>
        {orders.map((order) => (
          <article data-testid="order-card" className={styles.order} key={order.orderId}>
            <div className={styles.orderTop}>
              <div>
                <span data-testid="order-id" className={styles.label}>Order #{order.orderId}</span>
                <h2 data-testid="order-total">{formatCurrency(order.totalAmount)}</h2>
                <p>{formatDate(order.createdAt)}</p>
              </div>
              <div className={styles.badges}>
                <span data-testid="order-status">{order.status}</span>
                <span data-testid="order-payment">{order.paymentMethod} {order.paymentStatus}</span>
              </div>
            </div>

            <div data-testid="order-shipping-address" className={styles.address}>
              <strong data-testid="order-shipping-name">{order.shippingName}</strong>
              <span>
                {order.shippingAddress}, {order.city}, {order.state} - {order.pincode}
              </span>
              <span data-testid="order-shipping-phone">{order.shippingPhone}</span>
            </div>

            <div className={styles.items}>
              {order.items.map((item) => (
                <div data-testid="order-item" className={styles.item} key={item.orderItemId}>
                  <img src={item.imageUrl || "https://placehold.co/72x72"} alt={item.productName} />
                  <div>
                    <strong data-testid="order-item-name">{item.productName}</strong>
                    <span>Qty {item.quantity} x {formatCurrency(item.unitPrice)}</span>
                  </div>
                  <b>{formatCurrency(item.itemTotal)}</b>
                </div>
              ))}
            </div>

            <div className={styles.totals}>
              <span>Subtotal {formatCurrency(order.subtotal)}</span>
              <span>GST {formatCurrency(order.taxAmount)}</span>
              <strong>Total {formatCurrency(order.totalAmount)}</strong>
            </div>
          </article>
        ))}
      </div>
    </div>
  );
}
