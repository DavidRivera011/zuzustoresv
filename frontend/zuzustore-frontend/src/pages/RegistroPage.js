import React, { useEffect, useState } from "react";
import axios from "axios";
import { Modal, Button, Table, Spinner } from "react-bootstrap";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import { jwtDecode } from "jwt-decode";

// Tus iconos
const icondash = "/img/icondashwhite.png";
const iconproductos = "/img/iconproductowhite.png";
const iconventas = "/img/iconventawhite.png";
const iconorden = "/img/iconordeneswhite.png";
const iconuser = "/img/iconuserwhite.png";
const iconmovimientos = "/img/movimientowhite.png";

// Sidebar
function SidebarImageIcon({ src, alt, to }) {
  const handleClick = () => (window.location.href = to);
  return (
    <img
      src={src}
      alt={alt}
      style={{
        width: 32,
        height: 32,
        margin: "16px 0",
        cursor: "pointer",
        objectFit: "contain",
        filter: "drop-shadow(0 1px 4px #0002)",
      }}
      onClick={handleClick}
      title={alt}
    />
  );
}

function MovimientosInventarioPage() {
  const [movimientos, setMovimientos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [usuario, setUsuario] = useState({ nombre: "Usuario", rol: "Rol" });
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("fecha");
  const [ascendente, setAscendente] = useState(false); // Por fecha descendente
  const filasPorPagina = 15;

  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

  // Modal detalle
  const [detalleMovimiento, setDetalleMovimiento] = useState(null);
  const [showDetalle, setShowDetalle] = useState(false);

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
    fetchMovimientos();
  }, []);

  const fetchMovimientos = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/movimientos", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMovimientos(res.data);
    } catch {
      setMovimientos([]);
    }
    setCargando(false);
  };

  // Ordenamiento y paginación
  const getSortedMovimientos = () => {
    const sorted = [...movimientos].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
      // Números y fechas
      if (columnaOrden === "id" || columnaOrden === "cantidad") {
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

  const movimientosOrdenados = getSortedMovimientos();
  const totalPaginas = Math.ceil(movimientosOrdenados.length / filasPorPagina);
  const movimientosPagina = movimientosOrdenados.slice(
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

  // --- LOGOUT HEADER ---
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
        <SidebarImageIcon src={icondash} alt="Dashboard" to="/dashboard-admin" />
        <SidebarImageIcon src={iconproductos} alt="Productos" to="/productos" />
        <SidebarImageIcon src={iconmovimientos} alt="Movimientos" to="/movimientos" />
        <SidebarImageIcon src={iconventas} alt="Ventas" to="/ventas" />
        <SidebarImageIcon src={iconorden} alt="Órdenes" to="/registro-inventario" />
        <SidebarImageIcon src={iconuser} alt="Gestor de Empleados" to="/empleados" />
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
                <Button variant="secondary" onClick={() => setShowLogout(false)}>
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
            <h2 style={{ margin: 0 }}>Movimientos de Inventario</h2>
          </div>
          {/* Tabla */}
          <div
            style={{
              background: "#fff",
              borderRadius: 14,
              boxShadow: "0 2px 10px #0001",
              padding: 24,
            }}
          >
            {cargando ? (
              <Spinner />
            ) : (
              <>
                <Table bordered hover responsive>
                  <thead>
                    <tr>
                      <th style={{ cursor: "pointer" }} onClick={() => cambiarOrden("id")}>
                        ID {columnaOrden === "id" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th style={{ cursor: "pointer" }} onClick={() => cambiarOrden("fecha")}>
                        Fecha {columnaOrden === "fecha" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th style={{ cursor: "pointer" }} onClick={() => cambiarOrden("tipo")}>
                        Tipo {columnaOrden === "tipo" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th>Producto</th>
                      <th>Marca</th>
                      <th>Categoría</th>
                      <th style={{ cursor: "pointer" }} onClick={() => cambiarOrden("cantidad")}>
                        Cantidad {columnaOrden === "cantidad" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th>Motivo</th>
                      <th>Empleado</th>
                      <th>Detalle</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movimientosPagina.length === 0 ? (
                      <tr>
                        <td colSpan={10} className="text-center">
                          No hay movimientos
                        </td>
                      </tr>
                    ) : (
                      movimientosPagina.map((mov) => (
                        <tr key={mov.id}>
                          <td>{mov.id}</td>
                          <td>{new Date(mov.fecha).toLocaleString()}</td>
                          <td>{capitalize(mov.tipo)}</td>
                          <td>{mov.producto ? mov.producto.nombre : "-"}</td>
                          <td>{mov.producto ? mov.producto.marca : "-"}</td>
                          <td>{mov.producto && mov.producto.categoria ? mov.producto.categoria.nombre : "-"}</td>
                          <td>{mov.cantidad}</td>
                          <td>{mov.motivo}</td>
                          <td>
                            {mov.empleado
                              ? `${mov.empleado.nombres} ${mov.empleado.apellidos}`
                              : "-"}
                          </td>
                          <td>
                            <Button
                              size="sm"
                              variant="info"
                              onClick={() => {
                                setDetalleMovimiento(mov);
                                setShowDetalle(true);
                              }}
                            >
                              Ver
                            </Button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </Table>
                {/* Paginador */}
                <div style={{ display: "flex", justifyContent: "center", gap: 8 }}>
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
                      variant={paginaActual === i + 1 ? "primary" : "outline-secondary"}
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
        </main>
      </div>

      {/* Modal Detalle Movimiento */}
      <Modal show={showDetalle} onHide={() => setShowDetalle(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Detalle de Movimiento</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {detalleMovimiento ? (
            <div>
              <p><b>ID:</b> {detalleMovimiento.id}</p>
              <p><b>Fecha:</b> {new Date(detalleMovimiento.fecha).toLocaleString()}</p>
              <p><b>Tipo:</b> {capitalize(detalleMovimiento.tipo)}</p>
              <p><b>Cantidad:</b> {detalleMovimiento.cantidad}</p>
              <p><b>Motivo:</b> {detalleMovimiento.motivo}</p>
              <hr />
              <h5>Producto</h5>
              <p><b>Nombre:</b> {detalleMovimiento.producto?.nombre}</p>
              <p><b>Marca:</b> {detalleMovimiento.producto?.marca}</p>
              <p><b>Categoría:</b> {detalleMovimiento.producto?.categoria?.nombre}</p>
              <hr />
              <h5>Empleado</h5>
              <p>
                {detalleMovimiento.empleado
                  ? `${detalleMovimiento.empleado.nombres} ${detalleMovimiento.empleado.apellidos}`
                  : "-"}
              </p>
            </div>
          ) : (
            "Cargando..."
          )}
        </Modal.Body>
      </Modal>
    </div>
  );
}

function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default MovimientosInventarioPage;
