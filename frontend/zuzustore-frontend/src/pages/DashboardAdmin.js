import React from "react";
import { useNavigate } from "react-router-dom";

function DashboardAdmin() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token"); // Elimina el token
    navigate("/login");               // Redirige a login
  };

  return (
    <div className="container mt-5">
      <div className="row mb-4">
        <div className="col-12">
          <div className="card shadow-sm">
            <div className="card-body">
              <h2 className="mb-0 text-center">Panel de Administrador</h2>
              <p className="text-center text-muted mb-0">
                Bienvenido, aquí puedes gestionar tu inventario, empleados y reportes.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Estadísticas de prueba */}
      <div className="row mb-4">
        <div className="col-md-4 mb-3">
          <div className="card border-primary shadow-sm">
            <div className="card-body text-center">
              <h5 className="card-title">Productos en Inventario</h5>
              <p className="display-6">120</p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card border-success shadow-sm">
            <div className="card-body text-center">
              <h5 className="card-title">Empleados</h5>
              <p className="display-6">8</p>
            </div>
          </div>
        </div>
        <div className="col-md-4 mb-3">
          <div className="card border-info shadow-sm">
            <div className="card-body text-center">
              <h5 className="card-title">Ventas Hoy</h5>
              <p className="display-6">36</p>
            </div>
          </div>
        </div>
      </div>

      {/* Botones de navegación de prueba */}
      <div className="row">
        <div className="col-md-3 mb-2">
          <button className="btn btn-outline-primary w-100">Gestionar Productos</button>
        </div>
        <div className="col-md-3 mb-2">
          <button className="btn btn-outline-success w-100">Gestionar Empleados</button>
        </div>
        <div className="col-md-3 mb-2">
          <button className="btn btn-outline-info w-100">Ver Reportes</button>
        </div>
        <div className="col-md-3 mb-2">
          <button className="btn btn-outline-danger w-100" onClick={handleLogout}>
            Cerrar sesión
          </button>
        </div>
      </div>
    </div>
  );
}

export default DashboardAdmin;
