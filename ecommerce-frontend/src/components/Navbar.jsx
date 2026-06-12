import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useState, useEffect } from "react";
import { getCart } from "../api/cartApi";
import styles from "../styles/navbar.module.css";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    const refreshCartCount = () => {
      if (localStorage.getItem("token") === "demo-video-token") {
        setCartCount(0);
        return;
      }

      getCart().then(res => setCartCount(res.data.totalItems || 0)).catch(() => {});
    };

    if (user) {
      refreshCartCount();
      window.addEventListener("cart:updated", refreshCartCount);
      return () => window.removeEventListener("cart:updated", refreshCartCount);
    }
  }, [user, location.pathname]);

  const isActive = (path) => location.pathname.startsWith(path);

  return (
    <nav className={styles.nav}>
      <div className={styles.inner}>
        <span id="nav-logo" className={styles.logo} onClick={() => navigate("/dashboard")}>
          Mercato
        </span>

        <div className={styles.links}>
          <button id="nav-home" className={`${styles.link} ${isActive("/dashboard") ? styles.active : ""}`}
            onClick={() => navigate("/dashboard")}>Home</button>
          <button id="nav-products" className={`${styles.link} ${isActive("/products") ? styles.active : ""}`}
            onClick={() => navigate("/products")}>Products</button>
          <button id="nav-cart" className={`${styles.link} ${isActive("/cart") ? styles.active : ""}`}
            onClick={() => navigate("/cart")}>
            Cart
            {cartCount > 0 && <span className={styles.badge}>{cartCount}</span>}
          </button>
          <button id="nav-orders" className={`${styles.link} ${isActive("/orders") ? styles.active : ""}`}
            onClick={() => navigate("/orders")}>Orders</button>
          <button id="nav-profile" className={`${styles.link} ${isActive("/profile") ? styles.active : ""}`}
            onClick={() => navigate("/profile")}>Profile</button>
          {user?.role === "ADMIN" && (
            <button className={`${styles.link} ${styles.adminLink} ${isActive("/admin") ? styles.active : ""}`}
              onClick={() => navigate("/admin")}>
              Admin Panel
            </button>
          )}
        </div>

        <div className={styles.right}>
          <button id="nav-user" className={styles.userName} onClick={() => navigate("/profile")}>{user?.name}</button>
          <button id="logout-button" className={styles.logoutBtn} onClick={() => { logout(); navigate("/login"); }}>
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
}
