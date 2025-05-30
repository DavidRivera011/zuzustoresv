import React, { useEffect, useState } from "react";
import axios from "axios";
import { Modal, Button, Table, Spinner, Form, Row, Col } from "react-bootstrap";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import { jwtDecode } from "jwt-decode";
import Select from "react-select";

// ICONOS
const icondash = "/img/icondashwhite.png";
const iconproductos = "/img/iconproductowhite.png";
const iconventas = "/img/iconventawhite.png";
const iconorden = "/img/iconordeneswhite.png";
const iconuser = "/img/iconuserwhite.png";
const iconmovimientos = "/img/movimientowhite.png";
const iconclientes = "/img/clienticonwhite.png";

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
  const [productos, setProductos] = useState([]);
  const [form, setForm] = useState({
    producto_id: "",
    tipo: "entrada",
    cantidad: "",
    motivo: "",
  });
  const [errorForm, setErrorForm] = useState("");
  const [usuario, setUsuario] = useState({
    nombre: "Usuario",
    rol: "Rol",
    id: null,
  });
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("fecha");
  const [ascendente, setAscendente] = useState(false);
  const filasPorPagina = 10;

  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

  // Modal detalle
  const [detalleMovimiento, setDetalleMovimiento] = useState(null);
  const [showDetalle, setShowDetalle] = useState(false);

  // Obtener productos y usuario
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        setUsuario({
          nombre: decoded.nombre || decoded.username || "Usuario",
          apellido: decoded.apellido || "",
          rol: decoded.rol ? capitalize(decoded.rol) : "Rol",
          id: decoded.id || decoded.user_id || decoded.sub || null,
        });
      } catch {
        setUsuario({ nombre: "Usuario", rol: "Rol", id: null });
      }
    }
    fetchMovimientos();
    fetchProductos();
    // eslint-disable-next-line
  }, []);

  const fetchMovimientos = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/movimientos-inventario",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setMovimientos(res.data);
    } catch {
      setMovimientos([]);
    }
    setCargando(false);
  };

  const fetchProductos = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/productos", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProductos(res.data);
    } catch {
      setProductos([]);
    }
  };

  // --- Registro de movimiento ---
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorForm("");

    if (
      !form.producto_id ||
      !form.cantidad ||
      form.cantidad <= 0 ||
      !form.tipo
    ) {
      setErrorForm("Completa todos los campos correctamente.");
      return;
    }

    // Validación de stock negativo en movimientos de salida
    const producto = productos.find((p) => p.id === Number(form.producto_id));
    const cantidad = Number(form.cantidad);

    if (!producto) {
      setErrorForm("Selecciona un producto válido.");
      return;
    }

    if (form.tipo === "salida") {
      const stockFinal = producto.stock - cantidad;
      if (stockFinal < 0) {
        setErrorForm(
          `No puedes realizar una salida que deje el stock en negativo. 
Stock actual: ${producto.stock}, Cantidad a retirar: ${cantidad}`
        );
        return;
      }
    }

    const token = localStorage.getItem("token");
    try {
      await axios.post(
        "http://localhost:8080/api/movimientos-inventario",
        {
          producto: { id: form.producto_id },
          empleado: { id: usuario.id },
          tipo: form.tipo,
          cantidad: form.cantidad,
          motivo: form.motivo,
        },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setForm({ producto_id: "", tipo: "entrada", cantidad: "", motivo: "" });
      fetchMovimientos();
      fetchProductos();
    } catch {
      setErrorForm("Error al registrar movimiento.");
    }
  };

  // --- Stock después del movimiento (solo para mostrarlo, no afecta la BD) ---
  function calcularStockFinal() {
    if (!form.producto_id || !form.cantidad || form.cantidad <= 0) return "";
    const producto = productos.find((p) => p.id === Number(form.producto_id));
    if (!producto) return "";
    let stock = producto.stock;
    const cantidad = Number(form.cantidad);
    switch (form.tipo) {
      case "entrada":
        stock += cantidad;
        break;
      case "salida":
        stock -= cantidad;
        break;
      default:
        break;
    }
    return stock;
  }

  // Ordenamiento y paginación
  const getSortedMovimientos = () => {
    const sorted = [...movimientos].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
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
        <SidebarImageIcon src={iconclientes} alt="Gestor de Clientes" to="/clientes" />
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
            <h2 style={{ margin: 0 }}>Movimientos de Inventario</h2>
          </div>
          {/* ----- FORMULARIO NUEVO MOVIMIENTO ----- */}
          <div
            style={{
              background: "#fff",
              borderRadius: 12,
              boxShadow: "0 1px 6px #0002",
              padding: 18,
              marginBottom: 28,
            }}
          >
            <Form onSubmit={handleSubmit}>
              <Row className="align-items-end">
                {/* Producto */}
                <Col md={4} style={{ marginBottom: 0 }}>
                  <Form.Group>
                    <Form.Label>Producto</Form.Label>
                    <Select
                      name="producto_id"
                      value={
                        productos
                          .filter((p) => p.id === Number(form.producto_id))
                          .map((p) => ({
                            value: p.id,
                            label: `${p.nombre} (Stock: ${p.stock})`,
                          }))[0] || null
                      }
                      onChange={(selected) =>
                        setForm({
                          ...form,
                          producto_id: selected ? selected.value : "",
                        })
                      }
                      options={productos.map((p) => ({
                        value: p.id,
                        label: `${p.nombre} (Stock: ${p.stock})`,
                      }))}
                      placeholder="Buscar producto..."
                      isClearable
                    />
                    <div style={{ minHeight: 25 }} />
                  </Form.Group>
                </Col>
                {/* Tipo */}
                <Col md={2} style={{ marginBottom: 0 }}>
                  <Form.Group>
                    <Form.Label>Tipo</Form.Label>
                    <Form.Control
                      as="select"
                      name="tipo"
                      value={form.tipo}
                      onChange={handleChange}
                      required
                    >
                      <option value="entrada">Entrada</option>
                      <option value="salida">Salida</option>
                    </Form.Control>
                    <div style={{ minHeight: 25 }} />
                  </Form.Group>
                </Col>
                <Col md={2} style={{ marginBottom: 0 }}>
                  <Form.Group>
                    <Form.Label>Cantidad</Form.Label>
                    <Form.Control
                      type="number"
                      min="1"
                      name="cantidad"
                      value={form.cantidad}
                      onChange={handleChange}
                      required
                    />
                    <div
                      style={{
                        minHeight: 20,
                        marginTop: 4,
                        fontSize: 13,
                        color: "#4a148c",
                        fontWeight: 600,
                        lineHeight: "20px",
                        transition: "opacity 0.2s",
                        opacity: form.producto_id && form.cantidad ? 1 : 0,
                      }}
                    >
                      {form.producto_id && form.cantidad ? (
                        <>
                          Stock después del movimiento:{" "}
                          <b>{calcularStockFinal()}</b>
                        </>
                      ) : (
                        ""
                      )}
                    </div>
                  </Form.Group>
                </Col>
                {/* Motivo */}
                <Col md={3} style={{ marginBottom: 0 }}>
                  <Form.Group>
                    <Form.Label>Motivo</Form.Label>
                    <Form.Control
                      name="motivo"
                      value={form.motivo}
                      onChange={handleChange}
                      placeholder="Motivo del movimiento"
                      required
                    />
                    <div style={{ minHeight: 25 }} />
                  </Form.Group>
                </Col>
                {/* Botón */}
                <Col md={1} style={{ marginBottom: 0 }}>
                  <Button
                    type="submit"
                    variant="success"
                    style={{ width: "100%", marginTop: 28 }}
                  >
                    Agregar
                  </Button>
                  <div style={{ minHeight: 25 }} />
                </Col>
              </Row>
              {errorForm && (
                <div style={{ color: "red", marginTop: 12 }}>{errorForm}</div>
              )}
            </Form>
          </div>
          {/* ----- FIN FORMULARIO ----- */}

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
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("id")}
                      >
                        ID{" "}
                        {columnaOrden === "id" ? (ascendente ? "▲" : "▼") : ""}
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
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("tipo")}
                      >
                        Tipo{" "}
                        {columnaOrden === "tipo"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th>Producto</th>
                      <th>Cantidad</th>
                      <th>Stock resultante</th>
                      <th>Motivo</th>
                      <th>Empleado</th>
                      <th>Detalle</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movimientosPagina.length === 0 ? (
                      <tr>
                        <td colSpan={8} className="text-center">
                          No hay movimientos
                        </td>
                      </tr>
                    ) : (
                      movimientosPagina.map((mov) => (
                        <tr key={mov.id}>
                          <td>{mov.id}</td>
                          <td>{new Date(mov.fecha).toLocaleString()}</td>
                          <td>{capitalize(mov.tipo)}</td>
                          <td>
                            {mov.producto
                              ? mov.producto.nombre
                              : mov.producto_id}
                          </td>
                          <td>{mov.cantidad}</td>
                          <td>
                            {mov.stockResultante !== undefined &&
                            mov.stockResultante !== null
                              ? mov.stockResultante
                              : "-"}
                          </td>
                          <td>{mov.motivo}</td>
                          <td>
                            {mov.empleado
                              ? `${mov.empleado.nombres} ${mov.empleado.apellidos}`
                              : mov.usuario_empleado_id}
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
                <div
                  style={{ display: "flex", justifyContent: "center", gap: 8 }}
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
                        paginaActual === i + 1 ? "primary" : "outline-secondary"
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
              <p>
                <b>ID:</b> {detalleMovimiento.id}
              </p>
              <p>
                <b>Fecha:</b>{" "}
                {new Date(detalleMovimiento.fecha).toLocaleString()}
              </p>
              <p>
                <b>Tipo:</b> {capitalize(detalleMovimiento.tipo)}
              </p>
              <p>
                <b>Cantidad:</b> {detalleMovimiento.cantidad}
              </p>
              <p>
                <b>Stock Resultante:</b> {detalleMovimiento.stockResultante}
              </p>
              <p>
                <b>Motivo:</b> {detalleMovimiento.motivo}
              </p>
              <hr />
              <h5>Producto</h5>
              <p>
                <b>Nombre:</b>{" "}
                {detalleMovimiento.producto?.nombre ||
                  detalleMovimiento.producto_id}
              </p>
              <hr />
              <h5>Empleado</h5>
              <p>
                {detalleMovimiento.empleado
                  ? `${detalleMovimiento.empleado.nombres} ${detalleMovimiento.empleado.apellidos}`
                  : detalleMovimiento.usuario_empleado_id}
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
