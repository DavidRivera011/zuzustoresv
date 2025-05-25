import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

function PrivateRoute({ children, allowedRoles }) {
  const token = localStorage.getItem("token");
  if (!token) return <Navigate to="/login" />;

  if (allowedRoles) {
    try {
      const decoded = jwtDecode(token);
      if (!allowedRoles.includes(decoded.rol)) {
        return <Navigate to="/login" />;
      }
    } catch (e) {
      return <Navigate to="/login" />;
    }
  }
  return children;
}
export default PrivateRoute;
