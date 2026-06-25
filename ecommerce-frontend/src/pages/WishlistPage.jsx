import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { addToCart } from "../api/cartApi";
import useFavorites from "../hooks/useFavorites";
import styles from "../styles/wishlist.module.css";

const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0,
});

export default function WishlistPage() {
  const navigate = useNavigate();
  const {
    products,
    totalItems,
    loading,
    error,
    toggleFavorite,
  } = useFavorites();
  const [addingProductId, setAddingProductId] = useState(null);
  const [message, setMessage] = useState("");

  const handleRemove = async (event, productId) => {
    event.stopPropagation();
    await toggleFavorite(productId);
  };

  const handleAddToCart = async (event, product) => {
    event.stopPropagation();
    if (product.stock <= 0 || addingProductId === product.productId) return;

    setAddingProductId(product.productId);
    setMessage("");
    try {
      await addToCart({ productId: product.productId, quantity: 1 });
      window.dispatchEvent(new Event("cart:updated"));
      setMessage(`${product.name} was added to your cart.`);
    } catch (err) {
      setMessage(err.response?.data?.error || "Could not add this product to your cart.");
    } finally {
      setAddingProductId(null);
    }
  };

  if (loading) {
    return <div id="wishlist-loading" className={styles.state}>Loading your wishlist...</div>;
  }

  return (
    <main className={styles.page}>
      <header className={styles.header}>
        <div>
          <span className={styles.eyebrow}>Saved for later</span>
          <h1 className={styles.title}>Your Wishlist</h1>
          <p className={styles.subtitle}>
            Products you love, saved to your account.
          </p>
        </div>
        <span className={styles.count}>{totalItems} saved</span>
      </header>

      {(error || message) && (
        <p className={error ? styles.error : styles.message}>{error || message}</p>
      )}

      {products.length === 0 ? (
        <section className={styles.empty}>
          <span className={styles.emptyHeart} aria-hidden="true">♡</span>
          <h2>Your wishlist is waiting</h2>
          <p>Tap the heart on any product and it will appear here.</p>
          <button type="button" onClick={() => navigate("/products")}>
            Explore products
          </button>
        </section>
      ) : (
        <section className={styles.grid} aria-label="Wishlist products">
          {products.map((product) => (
            <article
              key={product.productId}
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
            >
              <div className={styles.imageWrap}>
                <img
                  src={product.imageUrl || "https://placehold.co/500x380"}
                  alt={product.name}
                  className={styles.image}
                />
                <button
                  type="button"
                  className={styles.remove}
                  onClick={(event) => handleRemove(event, product.productId)}
                  aria-label={`Remove ${product.name} from wishlist`}
                  title="Remove from wishlist"
                >
                  ♥
                </button>
              </div>

              <div className={styles.body}>
                <span className={styles.category}>{product.category}</span>
                <h2>{product.name}</h2>
                <div className={styles.meta}>
                  <strong>{currencyFormatter.format(Number(product.price || 0))}</strong>
                  <span>{product.stock > 0 ? "In stock" : "Out of stock"}</span>
                </div>
                <button
                  type="button"
                  className={styles.cartButton}
                  onClick={(event) => handleAddToCart(event, product)}
                  disabled={product.stock <= 0 || addingProductId === product.productId}
                >
                  {addingProductId === product.productId ? "Adding..." : "Add to Cart"}
                </button>
              </div>
            </article>
          ))}
        </section>
      )}
    </main>
  );
}
