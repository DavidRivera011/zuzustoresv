import React from "react";
import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const dashboardPorRol = {
  ADMIN: "/dashboard-admin",
  EMPLEADO: "/dashboard-empleado",
};

function PrivateRoute({ children, allowedRoles }) {
  const token = localStorage.getItem("token");
  if (!token) return <Navigate to="/login" />;

  let userRole = null;
  try {
    const decoded = jwtDecode(token);
    userRole = (decoded.rol || decoded.role || "").toUpperCase();
  } catch (e) {
    return <Navigate to="/login" />;
  }

  const allowedUpper = (allowedRoles || []).map(r => r.toUpperCase());

  if (allowedUpper.includes(userRole)) {
    return children;
  } else if (dashboardPorRol[userRole]) {
    return <Navigate to={dashboardPorRol[userRole]} />;
  } else {
    return <Navigate to="/login" />;
  }
}

export default PrivateRoute;
