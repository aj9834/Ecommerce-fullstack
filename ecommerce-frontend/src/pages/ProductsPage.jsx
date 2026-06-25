import { useState, useEffect } from "react";
import { addToCart } from "../api/cartApi";
import { getAllProducts, searchProducts } from "../api/productApi";
import { useNavigate } from "react-router-dom";
import useFavorites from "../hooks/useFavorites";
import styles from "../styles/products.module.css";

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const [addingProductId, setAddingProductId] = useState(null);
  const [addedProductId, setAddedProductId] = useState(null);
  const {
    isFavorite,
    toggleFavorite,
    error: wishlistError,
  } = useFavorites();
  const navigate = useNavigate();

  async function fetchProducts() {
    setLoading(true);
    setError("");
    try {
      const res = await getAllProducts();
      setProducts(res.data);
    } catch {
      setError("Failed to load products");
    } finally {
      setLoading(false);
    }
  }

  // Load all products on first render
  useEffect(() => {
    let isMounted = true;

    getAllProducts()
      .then((res) => {
        if (isMounted) setProducts(res.data);
      })
      .catch(() => {
        if (isMounted) setError("Failed to load products");
      })
      .finally(() => {
        if (isMounted) setLoading(false);
      });

    return () => {
      isMounted = false;
    };
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await searchProducts(keyword, category);
      setProducts(res.data);
    } catch {
      setError("Search failed");
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setKeyword("");
    setCategory("");
    fetchProducts();
  };

  const handleAddToCart = async (e, product) => {
    e.stopPropagation();

    if (
      product.stock <= 0 ||
      addingProductId === product.productId ||
      addedProductId === product.productId
    ) return;

    setAddingProductId(product.productId);
    setError("");

    try {
      await addToCart({ productId: product.productId, quantity: 1 });
      setAddedProductId(product.productId);
      window.dispatchEvent(new Event("cart:updated"));
      setTimeout(() => {
        setAddedProductId((currentId) =>
          currentId === product.productId ? null : currentId
        );
      }, 1800);
    } catch (err) {
      const msg = err.response?.data?.error || "Failed to add product to cart";
      setError(msg);
    } finally {
      setAddingProductId(null);
    }
  };

  // Get unique categories from loaded products for the dropdown
  const categories = [...new Set(products.map((p) => p.category))];

  const handleFavorite = (e, productId) => {
    e.stopPropagation();
    toggleFavorite(productId);
  };

  return (
    <div className={styles.page}>

      {/* Header */}
      <div className={styles.header}>
        <div>
          <span className={styles.eyebrow}>Curated for you</span>
          <h1 id="products-title" className={styles.title}>Discover something good.</h1>
        </div>
        {!loading && !error && (
          <span className={styles.resultCount}>{products.length} products</span>
        )}
      </div>

      {/* Search + Filter Bar */}
      <form onSubmit={handleSearch} className={styles.searchBar}>
        <input
          id="product-search"
          type="text"
          placeholder="Search products..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          className={styles.searchInput}
        />
        <select
          id="product-category"
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className={styles.select}
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
        <button id="product-search-submit" type="submit" className={styles.searchBtn}>Search</button>
        <button id="product-search-reset" type="button" onClick={handleReset} className={styles.resetBtn}>Reset</button>
      </form>

      {/* States */}
      {loading && <p id="products-loading" className={styles.message}>Loading products...</p>}
      {error && <p id="products-error" className={styles.error}>{error}</p>}
      {wishlistError && <p className={styles.error}>{wishlistError}</p>}
      {!loading && !error && products.length === 0 && (
        <p id="products-empty" className={styles.message}>No products found.</p>
      )}

      {/* Product Grid */}
      {!loading && !error && (
        <div id="product-grid" className={styles.grid}>
          {products.map((product, index) => (
            <div
              key={product.productId}
              data-testid="product-card"
              data-product-id={product.productId}
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
              style={{ "--card-index": index }}
            >
              <div className={styles.imageWrap}>
                <img
                  src={product.imageUrl || "https://placehold.co/300x200"}
                  alt={product.name}
                  className={styles.image}
                />
                <button
                  type="button"
                  className={`${styles.likeBtn} ${isFavorite(product.productId) ? styles.liked : ""}`}
                  onClick={(e) => handleFavorite(e, product.productId)}
                  aria-label={`${isFavorite(product.productId) ? "Unlike" : "Like"} ${product.name}`}
                  aria-pressed={isFavorite(product.productId)}
                  title={`${isFavorite(product.productId) ? "Unlike" : "Like"} ${product.name}`}
                >
                  <span aria-hidden="true">{isFavorite(product.productId) ? "\u2665" : "\u2661"}</span>
                </button>
                {product.stock > 0 && product.stock <= 10 && (
                  <span className={styles.lowStock}>Only {product.stock} left</span>
                )}
              </div>
              <div className={styles.cardBody}>
                <span data-testid="product-category" className={styles.category}>{product.category}</span>
                <h3 data-testid="product-name" className={styles.productName}>{product.name}</h3>
                <div className={styles.cardFooter}>
                  <span data-testid="product-price" className={styles.price}>₹{product.price}</span>
                  <span data-testid="product-stock" className={styles.stock}>
                    {product.stock > 0 ? `${product.stock} in stock` : "Out of stock"}
                  </span>
                </div>
                <button
                  type="button"
                  data-testid="add-to-cart"
                  className={styles.cartBtn}
                  onClick={(e) => handleAddToCart(e, product)}
                  disabled={
                    product.stock <= 0 ||
                    addingProductId === product.productId ||
                    addedProductId === product.productId
                  }
                >
                  {addedProductId === product.productId
                    ? "Added \u2713"
                    : addingProductId === product.productId
                      ? "Adding..."
                      : "Add to Cart"}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
