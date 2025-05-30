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
const iconproductos = "/img/iconproductowhite.png";
const iconventas = "/img/iconventawhite.png";
const iconorden = "/img/iconordeneswhite.png";
const iconuser = "/img/iconuserwhite.png";
const iconmovimientos = "/img/movimientowhite.png";
const iconclientes = "/img/clienticonwhite.png";

Chart.register(
  BarElement,
  CategoryScale,
  LinearScale,
  ArcElement,
  Tooltip,
  Legend
);

function DashboardAdmin() {
  const navigate = useNavigate();

  // Estados
  const [estadisticas, setEstadisticas] = useState({
    ganancias: 0,
    devoluciones: 0,
    compras: 0,
    ingresos: 0,
  });
  const [datosBarras, setDatosBarras] = useState({ labels: [], datasets: [] });
  const [datosPie, setDatosPie] = useState({
    labels: [],
    datasets: [{ data: [] }],
  });
  const [alertaStock, setAlertaStock] = useState([]);
  const [topVendidosTabla, setTopVendidosTabla] = useState([]);
  const [loading, setLoading] = useState(true);

  const [usuario, setUsuario] = useState({ nombre: "Usuario", rol: "Rol" });
  const [showMenu, setShowMenu] = useState(false);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUsuario({
          nombre: decoded.nombre || decoded.username || "Usuario",
          apellido: decoded.apellido || "",
          rol: decoded.rol ? capitalize(decoded.rol) : "Rol",
        });
      } catch {
        setUsuario({ nombre: "Usuario", rol: "Rol" });
      }
    }

    // Estadísticas tarjetas
    axios
      .get("http://localhost:8080/api/admin/estadisticas", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setEstadisticas(res.data);
      });

    // Gráfico de barras: ventas y devoluciones por mes
    axios
      .get("http://localhost:8080/api/admin/ventas-mes", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        // Asegura que las claves existan y tengan el formato correcto
        setDatosBarras({
          labels: res.data?.labels ?? [],
          datasets: res.data?.datasets ?? [],
        });
      });

    // Pie: top productos vendidos
    axios
      .get("http://localhost:8080/api/admin/top-productos", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setDatosPie({
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
        setTopVendidosTabla(res.data?.tabla || []);
      });

    // Alerta de stock
    axios
      .get("http://localhost:8080/api/admin/stock-bajo", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        setAlertaStock(res.data || []);
      })
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

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
        <SidebarImageIcon
          src={icondash}
          alt="Dashboard"
          to="/dashboard-admin"
        />
        <SidebarImageIcon src={iconproductos} alt="Productos" to="/productos" />
        <SidebarImageIcon
          src={iconmovimientos}
          alt="Movimientos"
          to="/movimientos"
        />
        <SidebarImageIcon src={iconventas} alt="Ventas" to="/ventas" />
        <SidebarImageIcon
          src={iconorden}
          alt="Órdenes"
          to="/ordenadminpage"
        />
        <SidebarImageIcon
          src={iconuser}
          alt="Gestor de Empleados"
          to="/empleados"
        />
        <SidebarImageIcon
          src={iconclientes}
          alt="Gestor de Clientes"
          to="/clientes"
        />
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
            <CardEstadistica
              titulo="Valor de stock"
              valor={`$${Number(estadisticas.valorStock).toLocaleString(
                "es-SV",
                { minimumFractionDigits: 2 }
              )}`}
            />
            <CardEstadistica
              titulo="Devolución de ventas"
              valor={`$${Number(estadisticas.devoluciones).toLocaleString(
                "es-SV",
                { minimumFractionDigits: 2 }
              )}`}
            />
            <CardEstadistica titulo="Ingreso" valor={`$${Number(estadisticas.ingresos).toLocaleString(
                "es-SV",
                { minimumFractionDigits: 2 }
              )}`}
            />
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
              {datosBarras.labels.length > 0 &&
              datosBarras.datasets.length > 0 ? (
                <Bar data={datosBarras} height={110} />
              ) : (
                <div style={{ textAlign: "center", color: "#aaa" }}>
                  Cargando gráfico de ventas...
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
              <div style={{ fontSize: 14, marginBottom: 8 }}>
                Top productos vendidos
              </div>
              {datosPie.labels.length > 0 &&
              datosPie.datasets[0]?.data?.length > 0 ? (
                <Pie data={datosPie} />
              ) : (
                <div style={{ textAlign: "center", color: "#aaa" }}>
                  Cargando gráfico de productos...
                </div>
              )}
            </div>
          </div>

          {/* Tablas */}
          <div style={{ display: "flex", gap: 20 }}>
            <div
              style={{
                flex: 2,
                background: "#fff",
                borderRadius: 14,
                boxShadow: "0 2px 10px #0001",
                padding: 18,
              }}
            >
              <div
                onClick={() => navigate("/movimientos")}
                style={{
                  fontWeight: 600,
                  marginBottom: 8,
                  color: "#7c4d68",
                  cursor: "pointer",
                  textDecoration: "underline",
                  width: "fit-content",
                  transition: "color 0.2s",
                }}
                onMouseOver={(e) => (e.target.style.color = "#a4508b")}
                onMouseOut={(e) => (e.target.style.color = "#7c4d68")}
              >
                Alerta de Stock
              </div>
              <TablaStock filas={alertaStock} loading={loading} />
            </div>
            <div
              style={{
                flex: 1,
                background: "#fff",
                borderRadius: 14,
                boxShadow: "0 2px 10px #0001",
                padding: 18,
              }}
            >
              <div
                onClick={() => navigate("/ventas")}
                style={{
                  fontWeight: 600,
                  marginBottom: 8,
                  color: "#7c4d68",
                  cursor: "pointer",
                  textDecoration: "underline",
                  width: "fit-content",
                  transition: "color 0.2s",
                }}
                onMouseOver={(e) => (e.target.style.color = "#a4508b")}
                onMouseOut={(e) => (e.target.style.color = "#7c4d68")}
              >
                Top productos vendidos
              </div>

              <TablaTopVendidos filas={topVendidosTabla} loading={loading} />
            </div>
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
        <span style={{ color: "#218838", fontSize: 24, marginRight: 5 }}>
          +{" "}
        </span>
        {valor?.toLocaleString?.() ?? valor}
      </div>
      <span
        role="img"
        aria-label="icon"
        style={{ fontSize: 19, marginLeft: 3 }}
      >
        🤑
      </span>
    </div>
  );
}

// Tabla de productos con bajo stock
function TablaStock({ filas, loading }) {
  if (loading) return <div>Cargando...</div>;
  if (!filas || filas.length === 0) return <div>Sin alertas de stock.</div>;
  return (
    <table style={{ width: "100%", fontSize: 14, borderCollapse: "collapse" }}>
      <thead>
        <tr style={{ borderBottom: "2px solid #222" }}>
          <th>ID</th>
          <th>Producto</th>
          <th>Stock</th>
          <th>Alerta</th>
          <th>Estado</th>
        </tr>
      </thead>
      <tbody>
        {filas.map((f, i) => (
          <tr key={i}>
            <td>{f.id}</td>
            <td>{f.nombre}</td>
            <td>{f.stock}</td>
            <td>Alerta</td>
            <td>{f.stock <= 5 ? "Bajo" : "OK"}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

// Tabla de top productos vendidos
function TablaTopVendidos({ filas, loading }) {
  if (loading) return <div>Cargando...</div>;
  if (!filas || filas.length === 0) return <div>No hay productos.</div>;
  return (
    <table style={{ width: "100%", fontSize: 14, borderCollapse: "collapse" }}>
      <thead>
        <tr style={{ borderBottom: "2px solid #222" }}>
          <th>Producto</th>
          <th>Cantidad</th>
        </tr>
      </thead>
      <tbody>
        {filas.map((f, i) => (
          <tr key={i}>
            <td>{f.nombre}</td>
            <td>{f.cantidad}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

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

// Resto de componentes (SidebarIcon, CardEstadistica, TablaStock, TablaTopVendidos) igual a tu código anterior

export default DashboardAdmin;
