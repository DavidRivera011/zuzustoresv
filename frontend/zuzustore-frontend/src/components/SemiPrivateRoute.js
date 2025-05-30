import { Navigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

function SemiPrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  if (!token) {
    return children;
  }

  let userRole = null;
  try {
    const decoded = jwtDecode(token);
    userRole = (decoded.rol || decoded.role || "").toUpperCase();
  } catch (e) {
    return children;
  }

  if (userRole === "ADMIN" || userRole === "EMPLEADO") {
    const dashboard = userRole === "ADMIN" ? "/dashboard-admin" : "/dashboard-empleado";
    return <Navigate to={dashboard} />;
  }
  return children;
}

export default SemiPrivateRoute;
