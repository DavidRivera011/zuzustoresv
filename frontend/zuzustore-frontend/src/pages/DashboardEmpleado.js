import React, { useEffect, useState } from "react";
import { Bar, Pie } from "react-chartjs-2";
import {
  Chart,
  BarElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  Tooltip,
  Legend,
} from "chart.js";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const icondash = "/img/icondashwhite.png";
const iconventas = "/img/iconventawhite.png";
const iconmovimientos = "/img/movimientowhite.png";
const iconorden = "/img/iconordeneswhite.png";

Chart.register(BarElement, CategoryScale, LinearScale, ArcElement, Tooltip, Legend);

function SidebarImageIcon({ src, alt, to }) {
  const navigate = useNavigate();
  return (
    <img
      src={src}
      alt={alt}
      title={alt}
      style={{
        width: 32,
        height: 32,
        margin: "16px 0",
        cursor: "pointer",
        objectFit: "contain",
        filter: "drop-shadow(0 1px 4px #0002)",
      }}
      onClick={() => navigate(to)}
    />
  );
}

function DashboardEmpleado() {
  const [estadisticas, setEstadisticas] = useState({
    movimientosHoy: 0,
    productosGestionados: 0,
    ventasHoy: 0,
    totalVentas: 0,
  });
  const [barras, setBarras] = useState({ labels: [], datasets: [] });
  const [pie, setPie] = useState({ labels: [], datasets: [{ data: [] }] });
  const [loading, setLoading] = useState(true);
  const [usuario, setUsuario] = useState({ nombre: "Usuario", rol: "Empleado" });
  const [showMenu, setShowMenu] = useState(false);
  const [showModal, setShowModal] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUsuario({
          nombre: decoded.nombre || decoded.username || "Usuario",
          apellido: decoded.apellido || "",
          rol: decoded.rol ? capitalize(decoded.rol) : "Empleado",
        });
      } catch {
        setUsuario({ nombre: "Usuario", rol: "Empleado" });
      }
    }

    // Estadísticas tarjetas
    axios
      .get("http://localhost:8080/api/empleado/estadisticas", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setEstadisticas(res.data))
      .catch(() => setEstadisticas({
        movimientosHoy: "--", productosGestionados: "--", ventasHoy: "--", totalVentas: "--"
      }));

    // Gráfica de barras: ventas y movimientos del empleado por mes
    axios
      .get("http://localhost:8080/api/empleado/ventas-mes", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setBarras({
          labels: res.data?.labels ?? [],
          datasets: res.data?.datasets ?? [],
        });
      });

    // Gráfica de pastel: productos gestionados por categoría o por tipo
    axios
      .get("http://localhost:8080/api/empleado/productos-gestionados", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setPie({
          labels: res.data?.labels ?? [],
          datasets: [
            {
              data: res.data?.values ?? [],
              backgroundColor: [
                "#191970",
                "#6c63ff",
                "#d1b3c4",
                "#8888ff",
                "#ffd1e0",
                "#aabbcc",
              ],
            },
          ],
        });
      })
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  if (loading) {
    return (
      <div style={{
        display: "flex", justifyContent: "center", alignItems: "center",
        minHeight: "100vh", background: "#efd0df", fontSize: 26, color: "#7c4d68"
      }}>
        <div className="spinner-border text-primary" role="status" style={{marginRight: 14}} />
        Cargando panel...
      </div>
    );
  }

  return (
    <div style={{ display: "flex", minHeight: "100vh", background: "#efd0df" }}>
      {/* Sidebar */}
      <aside
        style={{
          width: 70,
          background: "#7c4d68",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          paddingTop: 18,
        }}
      >
        <FiMenu color="#fff" size={30} style={{ marginBottom: 24 }} />
        <SidebarImageIcon src={icondash} alt="Dashboard" to="/dashboard-empleado" />
        <SidebarImageIcon src={iconventas} alt="Ventas" to="/ventas-empleado" />
        <SidebarImageIcon src={iconmovimientos} alt="Mis Movimientos" to="/mis-movimientos" />
        <SidebarImageIcon src={iconorden} alt="Mis Ordenes" to="/orden-empleados" />
      </aside>
      {/* Main content */}
      <div style={{ flex: 1 }}>
        {/* Header */}
        <header
          style={{
            height: 55,
            background: "#fff",
            display: "flex",
            alignItems: "center",
            justifyContent: "flex-end",
            padding: "0 28px",
            borderBottom: "1px solid #eee",
            fontSize: 15,
            color: "#444",
            position: "relative",
          }}
        >
          <span style={{ marginRight: 8, cursor: "pointer" }}>
            {usuario.nombre} {usuario.apellido} | {usuario.rol}
          </span>
          {/* Avatar con menú */}
          <div style={{ position: "relative" }}>
            <FaUserCircle
              size={30}
              style={{ cursor: "pointer" }}
              onClick={() => setShowMenu((m) => !m)}
            />
            {showMenu && (
              <div
                style={{
                  position: "absolute",
                  top: 40,
                  right: 0,
                  background: "#fff",
                  boxShadow: "0 2px 10px #0001",
                  borderRadius: 10,
                  padding: "10px 16px",
                  zIndex: 100,
                  minWidth: 120,
                }}
              >
                <button
                  className="btn btn-outline-danger w-100"
                  style={{ fontWeight: 600 }}
                  onClick={() => {
                    setShowModal(true);
                    setShowMenu(false);
                  }}
                >
                  Cerrar sesión
                </button>
              </div>
            )}
          </div>
        </header>
        {/* Modal de confirmación */}
        {showModal && (
          <div
            style={{
              position: "fixed",
              top: 0,
              left: 0,
              width: "100vw",
              height: "100vh",
              background: "rgba(0,0,0,0.3)",
              zIndex: 101,
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <div
              style={{
                background: "#fff",
                borderRadius: 12,
                padding: 30,
                boxShadow: "0 3px 16px #0003",
                minWidth: 280,
              }}
            >
              <div style={{ marginBottom: 18, fontWeight: 600, fontSize: 19 }}>
                ¿Cerrar sesión?
              </div>
              <div className="d-flex justify-content-between">
                <button
                  className="btn btn-secondary"
                  onClick={() => setShowModal(false)}
                >
                  Cancelar
                </button>
                <button className="btn btn-danger" onClick={handleLogout}>
                  Cerrar sesión
                </button>
              </div>
            </div>
          </div>
        )}
        {/* Panel principal */}
        <main style={{ padding: 28 }}>
          {/* Tarjetas superiores */}
          <div style={{ display: "flex", gap: 20, marginBottom: 20 }}>
            <CardEstadistica titulo="Movimientos hoy" valor={estadisticas.movimientosHoy} />
            <CardEstadistica titulo="Productos gestionados" valor={estadisticas.productosGestionados} />
            <CardEstadistica titulo="Ventas hoy" valor={estadisticas.ventasHoy} />
            <CardEstadistica titulo="Total de ventas realizadas" valor={estadisticas.totalVentasEntregadas} />
          </div>

          {/* Gráficos */}
          <div style={{ display: "flex", gap: 20, marginBottom: 20 }}>
            <div
              style={{
                flex: 2,
                background: "#fff",
                borderRadius: 14,
                boxShadow: "0 2px 10px #0001",
                padding: 20,
              }}
            >
              <div style={{ fontWeight: 600, marginBottom: 8 }}>
                Ventas y movimientos por mes
              </div>
              {barras.labels.length > 0 && barras.datasets.length > 0 ? (
                <Bar data={barras} height={110} />
              ) : (
                <div style={{ textAlign: "center", color: "#aaa" }}>
                  Cargando gráfico...
                </div>
              )}
            </div>
            <div
              style={{
                flex: 1,
                background: "#fff",
                borderRadius: 14,
                boxShadow: "0 2px 10px #0001",
                padding: 20,
              }}
            >
              <div style={{ fontWeight: 600, marginBottom: 8 }}>
                Productos gestionados
              </div>
              {pie.labels.length > 0 && pie.datasets[0]?.data?.length > 0 ? (
                <Pie data={pie} />
              ) : (
                <div style={{ textAlign: "center", color: "#aaa" }}>
                  Cargando gráfico...
                </div>
              )}
            </div>
          </div>

          {/* Acciones rápidas */}
          <div style={{ display: "flex", gap: 20, marginBottom: 20 }}>
            <button className="btn btn-outline-primary flex-fill" onClick={() => navigate("/ventas-empleado")}>
              Nueva Venta
            </button>
            <button className="btn btn-outline-warning flex-fill" onClick={() => navigate("/mis-movimientos")}>
              Mis Movimientos
            </button>
          </div>
        </main>
      </div>
    </div>
  );
}

// COMPONENTES AUXILIARES

function CardEstadistica({ titulo, valor }) {
  return (
    <div
      style={{
        flex: 1,
        background: "#fff",
        borderRadius: 14,
        boxShadow: "0 2px 10px #0001",
        padding: "16px 18px",
      }}
    >
      <div style={{ fontSize: 14, color: "#888", marginBottom: 4 }}>
        {titulo}
      </div>
      <div
        style={{
          fontWeight: 700,
          fontSize: 21,
          display: "flex",
          alignItems: "center",
        }}
      >
        {valor?.toLocaleString?.() ?? valor}
      </div>
    </div>
  );
}

function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default DashboardEmpleado;
