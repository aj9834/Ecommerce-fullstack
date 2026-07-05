import styles from "../styles/ratingStars.module.css";

export default function RatingStars({
  rating = 0,
  reviewCount,
  size = "normal",
}) {
  const roundedRating = Math.round(Number(rating) * 2) / 2;

  return (
    <span
      className={`${styles.rating} ${size === "small" ? styles.small : ""}`}
      aria-label={`${Number(rating).toFixed(1)} out of 5 stars`}
    >
      <span className={styles.stars} aria-hidden="true">
        {[1, 2, 3, 4, 5].map((star) => (
          <span key={star} className={star <= roundedRating ? styles.filled : styles.empty}>
            ★
          </span>
        ))}
      </span>
      <strong>{Number(rating).toFixed(1)}</strong>
      {reviewCount !== undefined && (
        <span className={styles.count}>({reviewCount})</span>
      )}
    </span>
  );
}
