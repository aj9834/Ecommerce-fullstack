import { AnimatePresence, motion } from "framer-motion";
import { addToCart } from "../api/cartApi";
import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getProductById } from "../api/productApi";
import useFavorites from "../hooks/useFavorites";

const fallbackImage = "https://placehold.co/900x700/e9f6f3/14532d?text=Product";
const MotionArticle = motion.article;
const MotionButton = motion.button;
const MotionDiv = motion.div;
const MotionImg = motion.img;
const currencyFormatter = new Intl.NumberFormat("en-IN", {
  style: "currency",
  currency: "INR",
  maximumFractionDigits: 0,
});

const cropViews = [
  { label: "Hero", position: "center center" },
  { label: "Detail", position: "left center" },
  { label: "Profile", position: "right center" },
  { label: "Close", position: "center bottom" },
];

function normalizeGallery(product) {
  const imageCandidates = [
    product.imageUrl,
    ...(Array.isArray(product.images) ? product.images : []),
    ...(Array.isArray(product.imageUrls) ? product.imageUrls : []),
    ...(Array.isArray(product.galleryImages) ? product.galleryImages : []),
  ].filter(Boolean);

  const uniqueImages = [...new Set(imageCandidates)];

  if (uniqueImages.length > 1) {
    return uniqueImages.slice(0, 4).map((src, index) => ({
      src,
      label: cropViews[index]?.label || `View ${index + 1}`,
      objectPosition: "center center",
    }));
  }

  const src = uniqueImages[0] || fallbackImage;
  return cropViews.map((view) => ({
    src,
    label: view.label,
    objectPosition: view.position,
  }));
}

export default function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [added, setAdded] = useState(false);
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [hoveredIndex, setHoveredIndex] = useState(null);
  const {
    isFavorite,
    toggleFavorite,
    error: wishlistError,
  } = useFavorites();

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const res = await getProductById(id);
        setProduct(res.data);
        setSelectedIndex(0);
      } catch {
        setError("Product not found.");
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id]);

  const galleryImages = useMemo(() => {
    return product ? normalizeGallery(product) : [];
  }, [product]);

  const selectedImage = galleryImages[selectedIndex] || galleryImages[0];
  const favorite = product ? isFavorite(product.productId) : false;
  const inStock = Number(product?.stock) > 0;

  const handleAddToCart = async () => {
    try {
      await addToCart({ productId: parseInt(id, 10), quantity });
      setAdded(true);
      setTimeout(() => setAdded(false), 2000);
    } catch (err) {
      const msg = err.response?.data?.error || "Failed to add to cart";
      alert(msg);
    }
  };

  if (loading) {
    return (
      <div id="product-detail-loading" className="flex min-h-[220px] items-center justify-center text-slate-500">
        Loading...
      </div>
    );
  }

  if (error) {
    return (
      <div id="product-detail-error" className="flex min-h-[220px] items-center justify-center text-slate-500">
        {error}
      </div>
    );
  }

  if (!product) return null;

  return (
    <main className="mx-auto w-full max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
      <button
        id="product-detail-back"
        type="button"
        onClick={() => navigate("/products")}
        className="mb-8 inline-flex items-center rounded-xl border border-emerald-100 bg-white/85 px-4 py-2 text-sm font-bold text-teal-700 shadow-sm backdrop-blur transition hover:-translate-y-0.5 hover:border-emerald-200 hover:bg-emerald-50"
      >
        <span aria-hidden="true" className="mr-2">{"<"}</span>
        Back to Products
      </button>

      <section className="grid grid-cols-1 items-stretch gap-8 md:grid-cols-2">
        <MotionDiv
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, ease: "easeOut" }}
          className="flex h-full min-h-[560px] flex-col rounded-2xl border border-white/80 bg-white/70 p-4 shadow-[0_24px_70px_rgba(16,45,29,0.12)] backdrop-blur-xl"
        >
          <div className="relative flex-1 overflow-hidden rounded-2xl bg-slate-100">
            <AnimatePresence mode="wait">
              <MotionImg
                key={`${selectedImage?.src}-${selectedImage?.objectPosition}`}
                src={selectedImage?.src || fallbackImage}
                alt={product.name}
                className="absolute inset-0 h-full w-full object-cover"
                style={{ objectPosition: selectedImage?.objectPosition || "center center" }}
                initial={{ opacity: 0, scale: 1.035 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.985 }}
                transition={{ duration: 0.32, ease: "easeOut" }}
              />
            </AnimatePresence>
          </div>

          <div
            className="mt-4 grid grid-cols-4 gap-3"
            onMouseLeave={() => setHoveredIndex(null)}
          >
            {galleryImages.map((image, index) => {
              const isHovered = hoveredIndex === index;
              const shouldBlur = hoveredIndex !== null && hoveredIndex !== index;
              const isSelected = selectedIndex === index;

              return (
                <MotionButton
                  key={`${image.src}-${image.label}`}
                  type="button"
                  onMouseEnter={() => {
                    setHoveredIndex(index);
                    setSelectedIndex(index);
                  }}
                  onFocus={() => {
                    setHoveredIndex(index);
                    setSelectedIndex(index);
                  }}
                  onClick={() => setSelectedIndex(index)}
                  animate={{
                    scale: isHovered ? 1.05 : 1,
                    opacity: shouldBlur ? 0.58 : 1,
                    filter: shouldBlur ? "blur(4px)" : "blur(0px)",
                  }}
                  transition={{ duration: 0.16, ease: "easeOut" }}
                  className={`relative h-24 overflow-hidden rounded-xl border bg-white shadow-sm outline-none ring-offset-2 transition ${
                    isSelected
                      ? "border-emerald-500 ring-2 ring-emerald-200"
                      : "border-white/80 hover:border-emerald-200"
                  }`}
                  aria-label={`Show ${image.label} image`}
                >
                  <img
                    src={image.src}
                    alt={`${product.name} ${image.label}`}
                    className="h-full w-full object-cover"
                    style={{ objectPosition: image.objectPosition }}
                  />
                </MotionButton>
              );
            })}
          </div>
        </MotionDiv>

        <MotionArticle
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45, delay: 0.08, ease: "easeOut" }}
          className="flex h-full min-h-[560px] flex-col rounded-2xl border border-slate-100 bg-white p-7 shadow-[0_22px_60px_rgba(15,23,42,0.08)] sm:p-8"
        >
          <div className="flex items-start justify-between gap-4">
            <span
              data-testid="product-detail-category"
              className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-black uppercase tracking-wide text-emerald-700"
            >
              {product.category}
            </span>

            <button
              id="product-detail-like"
              type="button"
              onClick={() => toggleFavorite(product.productId)}
              aria-label={`${favorite ? "Unlike" : "Like"} ${product.name}`}
              aria-pressed={favorite}
              title={`${favorite ? "Unlike" : "Like"} ${product.name}`}
              className={`grid h-11 w-11 place-items-center rounded-full border text-2xl leading-none transition hover:scale-105 ${
                favorite
                  ? "border-rose-200 bg-rose-50 text-rose-500"
                  : "border-slate-200 bg-white text-slate-800 hover:text-rose-500"
              }`}
            >
              <span aria-hidden="true">{favorite ? "\u2665" : "\u2661"}</span>
            </button>
          </div>

          <div className="mt-8 space-y-5">
            <h1
              id="product-detail-name"
              className="text-4xl font-black leading-tight tracking-tight text-slate-950 lg:text-5xl"
            >
              {product.name}
            </h1>

            <p
              data-testid="product-detail-description"
              className="max-w-xl text-base leading-8 text-slate-500"
            >
              {product.description}
            </p>
          </div>

          <div className="mt-8 flex flex-wrap items-center gap-4">
            <span id="product-detail-price" className="text-4xl font-black tracking-tight text-teal-700">
              {currencyFormatter.format(Number(product.price || 0))}
            </span>
            <span
              data-testid="product-detail-stock"
              className={`rounded-full px-3 py-1 text-sm font-extrabold ${
                inStock ? "bg-emerald-50 text-emerald-700" : "bg-rose-50 text-rose-600"
              }`}
            >
              {inStock ? `${product.stock} in stock` : "Out of stock"}
            </span>
          </div>

          <div className="mt-auto pt-10">
            {wishlistError && (
              <p className="mb-4 rounded-xl bg-rose-50 px-4 py-3 text-sm font-bold text-rose-700">
                {wishlistError}
              </p>
            )}
            {inStock && (
              <div className="mb-7 flex flex-wrap items-center justify-between gap-4 border-y border-slate-100 py-5">
                <span className="text-sm font-black text-slate-700">Quantity</span>
                <div className="inline-flex overflow-hidden rounded-xl border border-slate-200 bg-white">
                  <button
                    id="product-detail-quantity-decrease"
                    type="button"
                    onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                    className="grid h-12 w-12 place-items-center text-xl font-black text-teal-700 transition hover:bg-emerald-50"
                    aria-label="Decrease quantity"
                  >
                    -
                  </button>
                  <span
                    id="product-detail-quantity-value"
                    className="grid h-12 w-14 place-items-center border-x border-slate-200 text-base font-black text-slate-950"
                  >
                    {quantity}
                  </span>
                  <button
                    id="product-detail-quantity-increase"
                    type="button"
                    onClick={() => setQuantity((q) => Math.min(product.stock, q + 1))}
                    className="grid h-12 w-12 place-items-center text-xl font-black text-teal-700 transition hover:bg-emerald-50"
                    aria-label="Increase quantity"
                  >
                    +
                  </button>
                </div>
              </div>
            )}

            <MotionButton
              id="product-detail-add-to-cart"
              type="button"
              onClick={handleAddToCart}
              disabled={!inStock || added}
              whileHover={inStock && !added ? { y: -2 } : undefined}
              whileTap={inStock && !added ? { scale: 0.98 } : undefined}
              className="w-full rounded-2xl bg-gradient-to-r from-emerald-700 to-teal-700 px-6 py-4 text-base font-black text-white shadow-[0_18px_36px_rgba(15,118,110,0.25)] transition disabled:cursor-not-allowed disabled:from-slate-400 disabled:to-slate-500 disabled:shadow-none"
            >
              {added ? "Added to Cart" : inStock ? "Add to Cart" : "Out of Stock"}
            </MotionButton>
          </div>
        </MotionArticle>
      </section>
    </main>
  );
}
