/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState, useEffect, useCallback } from "react";
import axiosInstance from "../api/axiosInstance";

const AuthContext = createContext(null);

const getInitialDemoUser = () => {
  if (localStorage.getItem("token") !== "demo-video-token") return null;

  try {
    const storedUser = localStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : null;
  } catch {
    return null;
  }
};

const shouldVerifyStoredToken = () => {
  const token = localStorage.getItem("token");
  return Boolean(token) && token !== "demo-video-token";
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(getInitialDemoUser);
  const [loading, setLoading] = useState(shouldVerifyStoredToken);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (!token) {
      // No token at all — definitely not logged in
      return;
    }

    // Token exists — ask backend if it's still valid
    if (token === "demo-video-token") {
      return;
    }

    axiosInstance.get("/auth/me")
      .then((res) => {
        const { token: _token, ...userData } = res.data;
        localStorage.setItem("user", JSON.stringify(userData));
        setUser(userData); // token valid, restore user
      })
      .catch(() => {
        // Token expired or invalid
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const login = useCallback((userData, token) => {
    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
  }, []);

  const updateUser = useCallback((userData, token) => {
    if (token) localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(userData));
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  }, []);

  // Show nothing while verifying token — prevents page flash
  if (loading) return null;

  return (
    <AuthContext.Provider value={{ user, login, logout, updateUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
