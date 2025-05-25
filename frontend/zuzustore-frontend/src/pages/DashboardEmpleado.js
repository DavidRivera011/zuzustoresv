import React from "react";
import { useNavigate } from "react-router-dom";

function DashboardEmpleado() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");               
  };

  return (
    <div className="container mt-5">
      {/* ...resto del dashboard... */}
      <div className="row">
        <div className="col-md-4 mb-2">
          <button className="btn btn-outline-primary w-100">Nueva Entrada</button>
        </div>
        <div className="col-md-4 mb-2">
          <button className="btn btn-outline-warning w-100">Nueva Salida</button>
        </div>
        <div className="col-md-4 mb-2">
          <button className="btn btn-outline-danger w-100" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </div>
    </div>
  );
}

export default DashboardEmpleado;
