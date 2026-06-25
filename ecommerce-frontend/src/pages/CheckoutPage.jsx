import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getCart } from "../api/cartApi";
import {
  checkout,
  createRazorpayOrder,
  verifyRazorpayPayment,
} from "../api/OrderApi";
import styles from "../styles/checkout.module.css";

const RAZORPAY_SCRIPT_URL = "https://checkout.razorpay.com/v1/checkout.js";

const initialForm = {
  shippingName: "",
  shippingPhone: "",
  shippingAddress: "",
  city: "",
  state: "",
  pincode: "",
  paymentMethod: "COD",
};

const formatCurrency = (value) =>
  `Rs. ${Number(value || 0).toLocaleString("en-IN", { maximumFractionDigits: 2 })}`;

const loadRazorpayScript = () =>
  new Promise((resolve, reject) => {
    if (window.Razorpay) {
      resolve();
      return;
    }

    const existingScript = document.querySelector(`script[src="${RAZORPAY_SCRIPT_URL}"]`);
    if (existingScript) {
      existingScript.addEventListener("load", resolve, { once: true });
      existingScript.addEventListener(
        "error",
        () => reject(new Error("Razorpay checkout failed to load")),
        { once: true }
      );
      return;
    }

    const script = document.createElement("script");
    script.src = RAZORPAY_SCRIPT_URL;
    script.async = true;
    script.onload = resolve;
    script.onerror = () => reject(new Error("Razorpay checkout failed to load"));
    document.body.appendChild(script);
  });

export default function CheckoutPage() {
  const [cart, setCart] = useState(null);
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    getCart()
      .then((res) => setCart(res.data))
      .catch(() => setError("Failed to load checkout details."))
      .finally(() => setLoading(false));
  }, []);

  const totals = useMemo(() => {
    const subtotal = Number(cart?.totalPrice || 0);
    const taxAmount = Number((subtotal * 0.18).toFixed(2));
    const deliveryFee = 0;
    return {
      subtotal,
      taxAmount,
      deliveryFee,
      totalAmount: subtotal + taxAmount + deliveryFee,
    };
  }, [cart]);

  const isEmpty = !cart?.items?.length;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setFieldErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const validate = () => {
    const errors = {};
    if (!form.shippingName.trim()) errors.shippingName = "Name is required";
    if (!/^[0-9]{10}$/.test(form.shippingPhone.trim())) {
      errors.shippingPhone = "Enter a 10 digit phone number";
    }
    if (!form.shippingAddress.trim()) errors.shippingAddress = "Address is required";
    if (!form.city.trim()) errors.city = "City is required";
    if (!form.state.trim()) errors.state = "State is required";
    if (!/^[0-9]{6}$/.test(form.pincode.trim())) {
      errors.pincode = "Enter a 6 digit pincode";
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (isEmpty) {
      setError("Your cart is empty.");
      return;
    }

    if (!validate()) return;

    setPlacing(true);
    try {
      const payload = Object.fromEntries(
        Object.entries(form).map(([key, value]) => [key, value.trim()])
      );

      if (form.paymentMethod === "COD") {
        const res = await checkout(payload);
        window.dispatchEvent(new Event("cart:updated"));
        navigate(`/checkout/success?orderId=${res.data.orderId}&method=COD`);
        return;
      }

      await loadRazorpayScript();
      const { data: paymentOrder } = await createRazorpayOrder(payload);

      const razorpay = new window.Razorpay({
        key: paymentOrder.keyId,
        amount: paymentOrder.amount,
        currency: paymentOrder.currency,
        name: "Ecommerce Store",
        description: `Payment for order #${paymentOrder.localOrderId}`,
        order_id: paymentOrder.razorpayOrderId,
        prefill: {
          name: paymentOrder.customerName,
          email: paymentOrder.customerEmail,
          contact: paymentOrder.customerPhone,
        },
        theme: { color: "#166534" },
        modal: {
          confirm_close: true,
          ondismiss: () => {
            setError("Payment was cancelled. Your order is still pending payment.");
            setPlacing(false);
          },
        },
        handler: async (response) => {
          try {
            const verification = await verifyRazorpayPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature,
            });
            window.dispatchEvent(new Event("cart:updated"));
            navigate(
              `/checkout/success?orderId=${verification.data.orderId}&method=ONLINE`
            );
          } catch (verificationError) {
            setError(
              verificationError.response?.data?.error ||
                "Payment completed, but verification failed. Please contact support."
            );
            setPlacing(false);
          }
        },
      });

      razorpay.on("payment.failed", (response) => {
        setError(
          response.error?.description ||
            "Online payment failed. You can retry from your pending order."
        );
        setPlacing(false);
      });

      razorpay.open();
    } catch (err) {
      const data = err.response?.data;
      if (data && !data.error) {
        setFieldErrors(data);
      }
      setError(data?.error || "Failed to place order. Please review your details and try again.");
    } finally {
      setPlacing(false);
    }
  };

  if (loading) return <div id="checkout-loading" className={styles.center}>Loading checkout...</div>;

  if (isEmpty) {
    return (
      <div className={styles.page}>
        <div id="checkout-empty" className={styles.empty}>
          <h1>Your cart is empty</h1>
          <p>Add products before starting checkout.</p>
          <button id="checkout-browse-products" onClick={() => navigate("/products")}>Browse Products</button>
        </div>
      </div>
    );
  }

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <button className={styles.backBtn} onClick={() => navigate("/cart")}>Back to cart</button>
        <h1>Checkout</h1>
      </div>

      {error && <div id="checkout-error" className={styles.error}>{error}</div>}

      <form id="checkout-form" className={styles.layout} onSubmit={handleSubmit}>
        <section className={styles.panel}>
          <h2>Shipping Details</h2>
          <div className={styles.grid}>
            <label>
              Full name
              <input
                id="checkout-shipping-name"
                name="shippingName"
                value={form.shippingName}
                onChange={handleChange}
                placeholder="Your name"
              />
              {fieldErrors.shippingName && <span data-testid="checkout-shipping-name-error">{fieldErrors.shippingName}</span>}
            </label>

            <label>
              Phone
              <input
                id="checkout-shipping-phone"
                name="shippingPhone"
                value={form.shippingPhone}
                onChange={handleChange}
                placeholder="10 digit mobile number"
                inputMode="numeric"
              />
              {fieldErrors.shippingPhone && <span data-testid="checkout-shipping-phone-error">{fieldErrors.shippingPhone}</span>}
            </label>

            <label className={styles.full}>
              Address
              <textarea
                id="checkout-shipping-address"
                name="shippingAddress"
                value={form.shippingAddress}
                onChange={handleChange}
                placeholder="House number, street, area"
                rows={4}
              />
              {fieldErrors.shippingAddress && <span data-testid="checkout-shipping-address-error">{fieldErrors.shippingAddress}</span>}
            </label>

            <label>
              City
              <input id="checkout-city" name="city" value={form.city} onChange={handleChange} placeholder="City" />
              {fieldErrors.city && <span data-testid="checkout-city-error">{fieldErrors.city}</span>}
            </label>

            <label>
              State
              <input id="checkout-state" name="state" value={form.state} onChange={handleChange} placeholder="State" />
              {fieldErrors.state && <span data-testid="checkout-state-error">{fieldErrors.state}</span>}
            </label>

            <label>
              Pincode
              <input
                id="checkout-pincode"
                name="pincode"
                value={form.pincode}
                onChange={handleChange}
                placeholder="6 digit pincode"
                inputMode="numeric"
              />
              {fieldErrors.pincode && <span data-testid="checkout-pincode-error">{fieldErrors.pincode}</span>}
            </label>
          </div>

          <div className={styles.payment}>
            <h2>Payment</h2>
            <label className={styles.paymentOption}>
              <input
                id="checkout-payment-cod"
                type="radio"
                name="paymentMethod"
                value="COD"
                checked={form.paymentMethod === "COD"}
                onChange={handleChange}
              />
              <span>
                <strong>Cash on Delivery (COD)</strong>
                Pay when your order arrives.
              </span>
            </label>
            <label className={styles.paymentOption}>
              <input
                id="checkout-payment-online"
                type="radio"
                name="paymentMethod"
                value="ONLINE"
                checked={form.paymentMethod === "ONLINE"}
                onChange={handleChange}
              />
              <span>
                <strong>Online Payment</strong>
                Pay securely with Razorpay.
              </span>
            </label>
          </div>
        </section>

        <aside className={styles.summary}>
          <h2>Order Summary</h2>
          <div className={styles.items}>
            {cart.items.map((item) => (
              <div className={styles.item} key={item.cartItemId}>
                <img src={item.imageUrl || "https://placehold.co/80x80"} alt={item.productName} />
                <div>
                  <strong>{item.productName}</strong>
                  <span>Qty {item.quantity} x {formatCurrency(item.price)}</span>
                </div>
                <b>{formatCurrency(item.itemTotal)}</b>
              </div>
            ))}
          </div>

          <div className={styles.rows}>
            <div><span>Subtotal</span><span>{formatCurrency(totals.subtotal)}</span></div>
            <div><span>GST (18%)</span><span>{formatCurrency(totals.taxAmount)}</span></div>
            <div><span>Delivery</span><span>Free</span></div>
          </div>

          <div className={styles.total}>
            <span>Total</span>
            <strong>{formatCurrency(totals.totalAmount)}</strong>
          </div>

          <button id="checkout-place-order" className={styles.placeBtn} type="submit" disabled={placing}>
            {placing
              ? form.paymentMethod === "ONLINE"
                ? "Opening Payment..."
                : "Placing Order..."
              : form.paymentMethod === "ONLINE"
                ? "Pay Online"
                : "Place COD Order"}
          </button>
        </aside>
      </form>
    </div>
  );
}
