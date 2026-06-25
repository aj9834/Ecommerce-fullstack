import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import ForgotPasswordPage from "../pages/ForgotPasswordPage";
import ResetPasswordPage from "../pages/ResetPasswordPage";
import DashboardPage from "../pages/DashboardPage";
import ProductsPage from "../pages/ProductsPage";
import ProductDetailPage from "../pages/ProductDetailPage";
import CartPage from "../pages/CartPage";
import CheckoutPage from "../pages/CheckoutPage";
import CheckoutSuccessPage from "../pages/CheckoutSuccessPage";
import OrdersPage from "../pages/OrdersPage";
import ProfilePage from "../pages/ProfilePage";
import WishlistPage from "../pages/WishlistPage";
import AdminPage from "../pages/admin/AdminPage";
import ProtectedRoute from "../components/ProtectedRoute";
import AdminRoute from "../components/AdminRoute";
import Layout from "../components/Layout";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/forgot-password" element={<ForgotPasswordPage />} />
      <Route path="/reset-password" element={<ResetPasswordPage />} />

      <Route path="/dashboard" element={
        <ProtectedRoute><Layout><DashboardPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/products" element={
        <ProtectedRoute><Layout><ProductsPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/products/:id" element={
        <ProtectedRoute><Layout><ProductDetailPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/cart" element={
        <ProtectedRoute><Layout><CartPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/checkout" element={
        <ProtectedRoute><Layout><CheckoutPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/checkout/success" element={
        <ProtectedRoute><Layout><CheckoutSuccessPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/orders" element={
        <ProtectedRoute><Layout><OrdersPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/wishlist" element={
        <ProtectedRoute><Layout><WishlistPage /></Layout></ProtectedRoute>
      }/>
      <Route path="/profile" element={
        <ProtectedRoute><Layout><ProfilePage /></Layout></ProtectedRoute>
      }/>
      <Route path="/admin/*" element={
        <AdminRoute><AdminPage /></AdminRoute>
      }/>
    </Routes>
  );
}
