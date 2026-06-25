import { useCallback, useEffect, useState } from "react";
import {
  addToWishlist,
  getWishlist,
  removeFromWishlist,
} from "../api/wishlistApi";

const STORAGE_KEY = "mercato-favorites";
const FAVORITES_EVENT = "favorites:updated";

function readDemoFavorites() {
  try {
    const stored = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
    return new Set(stored.map(String));
  } catch {
    return new Set();
  }
}

export default function useFavorites() {
  const [favorites, setFavorites] = useState(new Set());
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const applyWishlist = useCallback((wishlistProducts) => {
    const nextProducts = wishlistProducts || [];
    setProducts(nextProducts);
    setFavorites(new Set(nextProducts.map((product) => String(product.productId))));
  }, []);

  const broadcastWishlist = useCallback((wishlistProducts) => {
    window.dispatchEvent(new CustomEvent(FAVORITES_EVENT, {
      detail: { products: wishlistProducts || [] },
    }));
  }, []);

  const refreshFavorites = useCallback(async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      applyWishlist([]);
      setLoading(false);
      return;
    }

    if (token === "demo-video-token") {
      setFavorites(readDemoFavorites());
      setProducts([]);
      setLoading(false);
      return;
    }

    try {
      const response = await getWishlist();
      applyWishlist(response.data.products);
      setError("");
    } catch (err) {
      setError(err.response?.data?.error || "Could not load your wishlist");
    } finally {
      setLoading(false);
    }
  }, [applyWishlist]);

  useEffect(() => {
    const initialRefresh = window.setTimeout(refreshFavorites, 0);

    const syncFavorites = (event) => {
      if (event.detail?.products) {
        applyWishlist(event.detail.products);
      } else if (localStorage.getItem("token") === "demo-video-token") {
        setFavorites(readDemoFavorites());
      } else {
        refreshFavorites();
      }
    };

    window.addEventListener(FAVORITES_EVENT, syncFavorites);
    window.addEventListener("storage", syncFavorites);
    return () => {
      window.clearTimeout(initialRefresh);
      window.removeEventListener(FAVORITES_EVENT, syncFavorites);
      window.removeEventListener("storage", syncFavorites);
    };
  }, [applyWishlist, refreshFavorites]);

  const toggleFavorite = async (productId) => {
    const id = String(productId);
    const wasFavorite = favorites.has(id);

    if (localStorage.getItem("token") === "demo-video-token") {
      const nextFavorites = new Set(favorites);
      if (wasFavorite) nextFavorites.delete(id);
      else nextFavorites.add(id);
      localStorage.setItem(STORAGE_KEY, JSON.stringify([...nextFavorites]));
      setFavorites(nextFavorites);
      window.dispatchEvent(new Event(FAVORITES_EVENT));
      return true;
    }

    const optimisticFavorites = new Set(favorites);
    if (wasFavorite) {
      optimisticFavorites.delete(id);
    } else {
      optimisticFavorites.add(id);
    }
    setFavorites(optimisticFavorites);
    setError("");

    try {
      const response = wasFavorite
        ? await removeFromWishlist(productId)
        : await addToWishlist(productId);
      applyWishlist(response.data.products);
      broadcastWishlist(response.data.products);
      return true;
    } catch (err) {
      setFavorites(favorites);
      setError(err.response?.data?.error || "Could not update your wishlist");
      return false;
    }
  };

  return {
    isFavorite: (productId) => favorites.has(String(productId)),
    toggleFavorite,
    products,
    totalItems: favorites.size,
    loading,
    error,
    refreshFavorites,
  };
}
