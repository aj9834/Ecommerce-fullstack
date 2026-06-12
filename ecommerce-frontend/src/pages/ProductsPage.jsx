import { useState, useEffect } from "react";
import { addToCart } from "../api/cartApi";
import { getAllProducts, searchProducts } from "../api/productApi";
import { useNavigate } from "react-router-dom";
import styles from "../styles/products.module.css";

export default function ProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const [addingProductId, setAddingProductId] = useState(null);
  const [addedProductId, setAddedProductId] = useState(null);
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

  return (
    <div className={styles.page}>

      {/* Header */}
      <div className={styles.header}>
        <h1 id="products-title" className={styles.title}>Products</h1>
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
      {!loading && !error && products.length === 0 && (
        <p id="products-empty" className={styles.message}>No products found.</p>
      )}

      {/* Product Grid */}
      {!loading && !error && (
        <div id="product-grid" className={styles.grid}>
          {products.map((product) => (
            <div
              key={product.productId}
              data-testid="product-card"
              data-product-id={product.productId}
              className={styles.card}
              onClick={() => navigate(`/products/${product.productId}`)}
            >
              <img
                src={product.imageUrl || "https://placehold.co/300x200"}
                alt={product.name}
                className={styles.image}
              />
              <div className={styles.cardBody}>
                <span data-testid="product-category" className={styles.category}>{product.category}</span>
                <h3 data-testid="product-name" className={styles.productName}>{product.name}</h3>
                <p className={styles.description}>
                  {product.description?.slice(0, 80)}
                  {product.description?.length > 80 ? "..." : ""}
                </p>
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
                    ? "Added"
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
