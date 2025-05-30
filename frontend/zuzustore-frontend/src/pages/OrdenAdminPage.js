import React, { useEffect, useState } from "react";
import axios from "axios";
import { Button, Table, Spinner } from "react-bootstrap";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import { jwtDecode } from "jwt-decode";

// ICONOS
const icondash = "/img/icondashwhite.png";
const iconproductos = "/img/iconproductowhite.png";
const iconventas = "/img/iconventawhite.png";
const iconorden = "/img/iconordeneswhite.png";
const iconuser = "/img/iconuserwhite.png";
const iconmovimientos = "/img/movimientowhite.png";
const iconclientes = "/img/clienticonwhite.png";

function getEstadoColor(estado) {
  switch ((estado || "").toLowerCase()) {
    case "entregada":
      return "#4caf50";
    case "procesando":
      return "#2196f3";
    case "cancelada":
      return "#f44336";
    case "pendiente":
    default:
      return "#ffc107";
  }
}

function SidebarImageIcon({ src, alt, to }) {
  const handleClick = () => {
    window.location.href = to;
  };
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
      onClick={handleClick}
    />
  );
}

function OrdenAdminPage() {
  const [ordenes, setOrdenes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [usuario, setUsuario] = useState({
    nombre: "Admin",
    rol: "Admin",
  });
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("id");
  const [ascendente, setAscendente] = useState(true);
  const filasPorPagina = 10;
  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

  // Detalle
  const [ordenSeleccionada, setOrdenSeleccionada] = useState(null);
  const [detallesOrden, setDetallesOrden] = useState([]);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUsuario({
          nombre: decoded.nombre || decoded.username || "Admin",
          apellido: decoded.apellido || "",
          rol: decoded.rol ? capitalize(decoded.rol) : "Admin",
        });
      } catch {
        setUsuario({ nombre: "Admin", rol: "Admin" });
      }
    }
    fetchOrdenes();
    // eslint-disable-next-line
  }, []);

  const fetchOrdenes = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/ordenes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setOrdenes(res.data);
    } catch {
      setOrdenes([]);
    }
    setCargando(false);
  };

  // Detalle de orden
  const handleSeleccionarOrden = (orden) => {
    setOrdenSeleccionada(orden);
    setCargandoDetalle(true);
    setDetallesOrden([]);
    const token = localStorage.getItem("token");
    if (orden.detalles) {
      setDetallesOrden(orden.detalles);
      setCargandoDetalle(false);
    } else {
      axios
        .get(`http://localhost:8080/api/ordenes/${orden.id}/detalles`, {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => setDetallesOrden(res.data))
        .catch(() => setDetallesOrden([]))
        .finally(() => setCargandoDetalle(false));
    }
  };

  // Ordenamiento y paginación
  const getSortedOrdenes = () => {
    const sorted = [...ordenes].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
      if (columnaOrden === "id") {
        valorA = Number(valorA);
        valorB = Number(valorB);
      }
      if (columnaOrden === "fecha") {
        valorA = new Date(a.fecha);
        valorB = new Date(b.fecha);
      }
      if (valorA < valorB) return ascendente ? -1 : 1;
      if (valorA > valorB) return ascendente ? 1 : -1;
      return 0;
    });
    return sorted;
  };

  const ordenesOrdenadas = getSortedOrdenes();
  const totalPaginas = Math.ceil(ordenesOrdenadas.length / filasPorPagina);
  const ordenesPagina = ordenesOrdenadas.slice(
    (paginaActual - 1) * filasPorPagina,
    paginaActual * filasPorPagina
  );

  const cambiarOrden = (columna) => {
    if (columnaOrden === columna) {
      setAscendente(!ascendente);
    } else {
      setColumnaOrden(columna);
      setAscendente(true);
    }
    setPaginaActual(1);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  const handleEstadoChange = async (ordenId, nuevoEstado) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/ordenes/${ordenId}/estado`,
        JSON.stringify(nuevoEstado),
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      fetchOrdenes();
    } catch (err) {
      alert("No se pudo cambiar el estado de la orden.");
    }
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
        <SidebarImageIcon src={iconorden} alt="Órdenes" to="/ordenadminpage" />
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
                    setShowLogout(true);
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
        {showLogout && (
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
                <Button
                  variant="secondary"
                  onClick={() => setShowLogout(false)}
                >
                  Cancelar
                </Button>
                <Button variant="danger" onClick={handleLogout}>
                  Cerrar sesión
                </Button>
              </div>
            </div>
          </div>
        )}

        {/* Panel principal */}
        <main style={{ padding: 28 }}>
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: 18,
            }}
          >
            <h2 style={{ margin: 0 }}>Órdenes de Clientes (Admin)</h2>
          </div>
          <div
            style={{
              background: "#fff",
              borderRadius: 14,
              boxShadow: "0 2px 10px #0001",
              padding: 24,
              display: "flex",
              gap: 32,
              alignItems: "flex-start",
              marginTop: 20,
            }}
          >
            {/* Tabla de órdenes IZQUIERDA */}
            <div style={{ flex: 2 }}>
              {cargando ? (
                <Spinner />
              ) : (
                <>
                  <Table bordered hover responsive>
                    <thead>
                      <tr>
                        <th
                          style={{ cursor: "pointer" }}
                          onClick={() => cambiarOrden("id")}
                        >
                          ID{" "}
                          {columnaOrden === "id"
                            ? ascendente
                              ? "▲"
                              : "▼"
                            : ""}
                        </th>
                        <th
                          style={{ cursor: "pointer" }}
                          onClick={() => cambiarOrden("fecha")}
                        >
                          Fecha{" "}
                          {columnaOrden === "fecha"
                            ? ascendente
                              ? "▲"
                              : "▼"
                            : ""}
                        </th>
                        <th>Cliente</th>
                        <th>Teléfono</th>
                        <th>Correo</th>
                        <th>Estado</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ordenesPagina.length === 0 ? (
                        <tr>
                          <td colSpan={7} className="text-center">
                            No hay órdenes
                          </td>
                        </tr>
                      ) : (
                        ordenesPagina.map((orden) => (
                          <tr
                            key={orden.id}
                            onClick={() => handleSeleccionarOrden(orden)}
                            style={{
                              cursor: "pointer",
                              background:
                                ordenSeleccionada &&
                                ordenSeleccionada.id === orden.id
                                  ? "#fce4ec"
                                  : "white",
                            }}
                          >
                            <td>{orden.id}</td>
                            <td>
                              {orden.fecha
                                ? new Date(orden.fecha).toLocaleString()
                                : "-"}
                            </td>
                            <td>{orden.cliente || "-"}</td>
                            <td>{orden.telefono || "-"}</td>
                            <td>{orden.correo || "-"}</td>
                            <td>
                              {orden.estado === "pendiente" ? (
                                <select
                                  value={orden.estado}
                                  style={{
                                    background: "#fff5d7",
                                    borderRadius: 8,
                                    fontWeight: 600,
                                    color: "#7d1445",
                                    padding: "4px 8px",
                                    border: "1px solid #eee",
                                  }}
                                  onChange={(e) =>
                                    handleEstadoChange(orden.id, e.target.value)
                                  }
                                >
                                  <option value="pendiente">Pendiente</option>
                                  <option value="entregada">Entregada</option>
                                  <option value="cancelada">Cancelada</option>
                                </select>
                              ) : (
                                <span
                                  style={{
                                    background: getEstadoColor(orden.estado),
                                    color: "#fff",
                                    padding: "4px 12px",
                                    borderRadius: 14,
                                    fontWeight: 600,
                                    fontSize: 13,
                                    display: "inline-block",
                                    minWidth: 95,
                                    textAlign: "center",
                                  }}
                                >
                                  {capitalize(orden.estado)}
                                </span>
                              )}
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </Table>
                  {/* Paginador */}
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "center",
                      gap: 8,
                      marginTop: 8,
                    }}
                  >
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      disabled={paginaActual === 1}
                      onClick={() => setPaginaActual(paginaActual - 1)}
                    >
                      {"<"}
                    </Button>
                    {Array.from({ length: totalPaginas }, (_, i) => (
                      <Button
                        key={i + 1}
                        variant={
                          paginaActual === i + 1
                            ? "primary"
                            : "outline-secondary"
                        }
                        size="sm"
                        onClick={() => setPaginaActual(i + 1)}
                      >
                        {i + 1}
                      </Button>
                    ))}
                    <Button
                      variant="outline-secondary"
                      size="sm"
                      disabled={paginaActual === totalPaginas}
                      onClick={() => setPaginaActual(paginaActual + 1)}
                    >
                      {">"}
                    </Button>
                  </div>
                </>
              )}
            </div>
            {/* Detalle de la orden DERECHA */}
            <div
              style={{
                flex: 1,
                boxShadow: "0 2px 10px #0001",
                minWidth: 330,
                maxWidth: 400,
                marginLeft: 8,
                marginTop: 0,
              }}
            >
              <h4 style={{ textAlign: "center", marginBottom: 8 }}>
                Detalle de Orden
              </h4>
              {!ordenSeleccionada ? (
                <div style={{ textAlign: "center", color: "#888" }}>
                  Selecciona una orden para ver los detalles
                </div>
              ) : cargandoDetalle ? (
                <Spinner />
              ) : detallesOrden.length === 0 ? (
                <div style={{ textAlign: "center", color: "#888" }}>
                  Sin detalles para esta orden
                </div>
              ) : (
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 16 }}
                >
                  {detallesOrden.map((d) => (
                    <div
                      key={d.id}
                      style={{
                        background: "#faf3f8",
                        borderRadius: 12,
                        boxShadow: "0 2px 8px #0001",
                        padding: 18,
                        marginBottom: 0,
                        border: "1px solid #e0b8d8",
                        display: "flex",
                        flexDirection: "column",
                        gap: 4,
                      }}
                    >
                      <div
                        style={{
                          fontWeight: 700,
                          fontSize: 17,
                          marginBottom: 4,
                        }}
                      >
                        {d.producto || `Producto ID: ${d.producto_id}`}
                      </div>
                      <div style={{ fontSize: 14 }}>
                        <b>ID Detalle:</b> {d.id}
                      </div>
                      <div style={{ fontSize: 14 }}>
                        <b>Cantidad:</b> {d.cantidad}
                      </div>
                      <div style={{ fontSize: 14 }}>
                        <b>Precio unitario:</b> $
                        {Number(
                          d.precioUnitario || d.precio_unitario || 0
                        ).toFixed(2)}
                      </div>
                      <div style={{ fontSize: 14 }}>
                        <b>Subtotal:</b> ${Number(d.subtotal || 0).toFixed(2)}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}

function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default OrdenAdminPage;
