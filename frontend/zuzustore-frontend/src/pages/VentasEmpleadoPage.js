import React, { useEffect, useState } from "react";
import axios from "axios";
import { Button, Table, Spinner, Form, Row, Col } from "react-bootstrap";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";
import Select from "react-select";

const icondash = "/img/icondashwhite.png";
const iconventas = "/img/iconventawhite.png";
const iconmovimientos = "/img/movimientowhite.png";

const ESTADOS_OPCIONES = [
  { value: "pendiente", label: "Pendiente" },
  { value: "entregado", label: "Entregado" },
  { value: "cancelado", label: "Cancelado" },
  { value: "devolucion", label: "Devolución" },
];

function getEstadoColor(estado) {
  switch ((estado || "").toLowerCase()) {
    case "entregado":
      return "#4caf50";
    case "devolucion":
      return "#2196f3";
    case "cancelado":
      return "#f44336";
    case "pendiente":
    default:
      return "#ffc107";
  }
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

function VentasEmpleadoPage() {
  // Estados principales
  const [ventas, setVentas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [usuario, setUsuario] = useState({
    nombre: "Usuario",
    rol: "Empleado",
  });
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("id");
  const [ascendente, setAscendente] = useState(true);
  const filasPorPagina = 10;

  // Menú y modal
  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

  // Formulario nueva venta
  const [productos, setProductos] = useState([]);
  const [clientes, setClientes] = useState([]);
  const [formVenta, setFormVenta] = useState({
    clienteId: "",
    estado: "pendiente",
  });

  // Carrito de productos para la venta
  const [productosVenta, setProductosVenta] = useState([]);
  // Producto temporal para agregar al carrito
  const [productoTemp, setProductoTemp] = useState({
    productoId: "",
    cantidad: 1,
  });

  const [registrando, setRegistrando] = useState(false);

  // Tabla de detalles de venta
  const [ventaSeleccionada, setVentaSeleccionada] = useState(null);
  const [detallesVenta, setDetallesVenta] = useState([]);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);

  const navigate = useNavigate();

  // Cargar info al iniciar
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
    fetchVentas();
    fetchProductos();
    fetchClientes();
    // eslint-disable-next-line
  }, []);

  // Obtener datos de API
  const fetchVentas = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/ventas/mis-ventas",
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setVentas(res.data);
    } catch {
      setVentas([]);
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

  const fetchClientes = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/clientes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setClientes(res.data);
    } catch {
      setClientes([]);
    }
  };

  // Opciones para selects (solo productos disponibles y stock > 0)
  const productoOptions = productos
    .filter((p) => p.estado === "disponible" && p.stock > 0)
    .map((p) => ({
      value: p.id,
      label: `ID: ${p.id} - ${p.nombre} (Stock: ${p.stock})`,
    }));
  const clienteOptions = clientes.map((c) => ({
    value: c.id,
    label: `ID: ${c.id} - ${c.nombres} ${c.apellidos} (${c.correo})`,
  }));

  // Registrar venta (con varios productos)
  const handleRegistrarVenta = async (e) => {
    e.preventDefault();
    if (!formVenta.clienteId || productosVenta.length === 0) {
      alert("Debe seleccionar un cliente y agregar al menos un producto.");
      return;
    }
    // Validar stocks antes de enviar (por si el stock cambió mientras se llenaba el formulario)
    for (let item of productosVenta) {
      const productoSel = productos.find((p) => p.id === Number(item.productoId));
      if (!productoSel) {
        alert("Producto no encontrado.");
        return;
      }
      if (item.cantidad > productoSel.stock) {
        alert(
          `No hay suficiente stock para el producto "${productoSel.nombre}". Stock actual: ${productoSel.stock}, solicitado: ${item.cantidad}`
        );
        return;
      }
    }
    setRegistrando(true);
    const token = localStorage.getItem("token");
    const payload = {
      clienteId: formVenta.clienteId,
      productos: productosVenta.map((p) => ({
        productoId: p.productoId,
        cantidad: p.cantidad,
      })),
      estado: formVenta.estado,
    };
    try {
      await axios.post("http://localhost:8080/api/ventas", payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFormVenta({
        clienteId: "",
        estado: "pendiente",
      });
      setProductosVenta([]);
      setProductoTemp({ productoId: "", cantidad: 1 });
      fetchVentas();
    } catch (error) {
      alert("Error al registrar la venta");
    }
    setRegistrando(false);
  };

  // Editar estado de la venta
  const handleEstadoChange = async (venta, selected) => {
    const token = localStorage.getItem("token");
    try {
      await axios.put(
        `http://localhost:8080/api/ventas/${venta.id}/estado`,
        { estado: selected.value },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      fetchVentas();
    } catch {
      alert("Error al actualizar el estado");
    }
  };

  // Ordenamiento y paginación
  const getSortedVentas = () => {
    const sorted = [...ventas].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
      if (columnaOrden === "id" || columnaOrden === "total") {
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

  const ventasOrdenadas = getSortedVentas();
  const totalPaginas = Math.ceil(ventasOrdenadas.length / filasPorPagina);
  const ventasPagina = ventasOrdenadas.slice(
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

  // Logout
  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  // Selección y carga de detalles de venta
  const handleSeleccionarVenta = (venta) => {
    setVentaSeleccionada(venta);
    setCargandoDetalle(true);
    setDetallesVenta([]);
    const token = localStorage.getItem("token");
    axios
      .get(`http://localhost:8080/api/detalle-ventas/por-venta/${venta.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setDetallesVenta(res.data))
      .catch(() => setDetallesVenta([]))
      .finally(() => setCargandoDetalle(false));
  };

  // ----------- RENDER --------------
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
          to="/dashboard-empleado"
        />
        <SidebarImageIcon src={iconventas} alt="Ventas" to="/ventas-empleado" />
        <SidebarImageIcon
          src={iconmovimientos}
          alt="Movimientos"
          to="/mis-movimientos"
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
                  onClick={() => setShowLogout(true)}
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
            <h2 style={{ margin: 0 }}>Mis Ventas</h2>
          </div>

          {/* Formulario de nueva venta */}
          <div
            style={{
              background: "#fff",
              borderRadius: 14,
              boxShadow: "0 2px 10px #0001",
              padding: 24,
              marginBottom: 28,
            }}
          >
            <Form onSubmit={handleRegistrarVenta}>
              <Row>
                <Col md={4}>
                  <Form.Group>
                    <Form.Label>Cliente</Form.Label>
                    <Select
                      name="clienteId"
                      value={
                        clienteOptions.find(
                          (c) => c.value === Number(formVenta.clienteId)
                        ) || null
                      }
                      onChange={(selected) =>
                        setFormVenta({
                          ...formVenta,
                          clienteId: selected ? selected.value : "",
                        })
                      }
                      options={clienteOptions}
                      placeholder="Buscar cliente..."
                      isClearable
                    />
                  </Form.Group>
                </Col>
                <Col md={3}>
                  <Form.Group>
                    <Form.Label>Estado de la venta</Form.Label>
                    <Select
                      name="estado"
                      value={
                        ESTADOS_OPCIONES.find(
                          (e) => e.value === formVenta.estado
                        ) || ESTADOS_OPCIONES[0]
                      }
                      onChange={(selected) =>
                        setFormVenta({ ...formVenta, estado: selected.value })
                      }
                      options={ESTADOS_OPCIONES.filter(
                        (e) =>
                          e.value === "pendiente" ||
                          e.value === "entregado" ||
                          e.value === "devolucion"
                      )}
                      placeholder="Selecciona estado..."
                    />
                  </Form.Group>
                </Col>
              </Row>
              {/* ------------------- AGREGADOR DE PRODUCTOS ------------------- */}
              <Row className="align-items-end mt-3">
                <Col md={5}>
                  <Form.Group>
                    <Form.Label>Producto</Form.Label>
                    <Select
                      name="productoId"
                      value={
                        productoOptions.find(
                          (p) => p.value === Number(productoTemp.productoId)
                        ) || null
                      }
                      onChange={(selected) =>
                        setProductoTemp({
                          ...productoTemp,
                          productoId: selected ? selected.value : "",
                        })
                      }
                      options={productoOptions}
                      placeholder="Buscar producto..."
                      isClearable
                    />
                  </Form.Group>
                </Col>
                <Col md={2}>
                  <Form.Group>
                    <Form.Label>Cantidad</Form.Label>
                    <Form.Control
                      name="cantidad"
                      type="number"
                      min="1"
                      value={productoTemp.cantidad}
                      onChange={(e) =>
                        setProductoTemp({
                          ...productoTemp,
                          cantidad: e.target.value,
                        })
                      }
                      required
                    />
                  </Form.Group>
                </Col>
                <Col md={2}>
                  <Button
                    variant="info"
                    style={{ width: "100%" }}
                    onClick={(e) => {
                      e.preventDefault();
                      if (
                        !productoTemp.productoId ||
                        !productoTemp.cantidad ||
                        productoTemp.cantidad < 1
                      )
                        return;

                      // Busca el producto seleccionado para validar stock
                      const productoSel = productos.find(
                        (p) => p.id === Number(productoTemp.productoId)
                      );
                      if (!productoSel) {
                        alert("Producto no encontrado.");
                        return;
                      }
                      if (Number(productoTemp.cantidad) > productoSel.stock) {
                        alert(
                          `La cantidad supera el stock disponible (${productoSel.stock}) para este producto.`
                        );
                        return;
                      }
                      // No permitir el mismo producto dos veces
                      if (
                        productosVenta.some(
                          (p) => p.productoId === productoTemp.productoId
                        )
                      ) {
                        alert("Ya has agregado este producto.");
                        return;
                      }
                      setProductosVenta((prev) => [
                        ...prev,
                        {
                          ...productoTemp,
                          cantidad: Number(productoTemp.cantidad),
                        },
                      ]);
                      setProductoTemp({ productoId: "", cantidad: 1 });
                    }}
                  >
                    Añadir producto
                  </Button>
                </Col>
              </Row>
              {/* Lista de productos agregados */}
              {productosVenta.length > 0 && (
                <div style={{ marginTop: 18, marginBottom: 8 }}>
                  <Table bordered size="sm">
                    <thead>
                      <tr>
                        <th>Producto</th>
                        <th>Cantidad</th>
                        <th>Acción</th>
                      </tr>
                    </thead>
                    <tbody>
                      {productosVenta.map((p, i) => (
                        <tr key={i}>
                          <td>
                            {
                              productoOptions.find(
                                (po) => po.value === Number(p.productoId)
                              )?.label
                            }
                          </td>
                          <td>{p.cantidad}</td>
                          <td>
                            <Button
                              size="sm"
                              variant="danger"
                              onClick={() =>
                                setProductosVenta(
                                  productosVenta.filter((_, idx) => idx !== i)
                                )
                              }
                            >
                              Eliminar
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              )}
              <Row className="mt-3">
                <Col md={2}>
                  <Button
                    type="submit"
                    variant="success"
                    disabled={
                      registrando ||
                      productosVenta.length === 0 ||
                      !formVenta.clienteId
                    }
                    className="w-100"
                  >
                    {registrando ? "Registrando..." : "Registrar Venta"}
                  </Button>
                </Col>
              </Row>
            </Form>
          </div>

          {/* Tablas ventas (izquierda) y detalle (derecha) */}
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
            {/* Tabla de ventas IZQUIERDA */}
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
                        <th
                          style={{ cursor: "pointer" }}
                          onClick={() => cambiarOrden("total")}
                        >
                          Total{" "}
                          {columnaOrden === "total"
                            ? ascendente
                              ? "▲"
                              : "▼"
                            : ""}
                        </th>
                        <th>Estado</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ventasPagina.length === 0 ? (
                        <tr>
                          <td colSpan={6} className="text-center">
                            No hay ventas
                          </td>
                        </tr>
                      ) : (
                        ventasPagina.map((venta) => (
                          <tr
                            key={venta.id}
                            onClick={() => handleSeleccionarVenta(venta)}
                            style={{
                              cursor: "pointer",
                              background:
                                ventaSeleccionada &&
                                ventaSeleccionada.id === venta.id
                                  ? "#fce4ec"
                                  : "white",
                            }}
                          >
                            <td>{venta.id}</td>
                            <td>
                              {venta.fecha
                                ? new Date(venta.fecha).toLocaleString()
                                : "-"}
                            </td>
                            <td>
                              {venta.cliente
                                ? `${venta.cliente.nombres} ${venta.cliente.apellidos}`
                                : "-"}
                            </td>
                            <td>
                              {venta.total !== undefined && venta.total !== null
                                ? `$${venta.total}`
                                : "-"}
                            </td>
                            <td>
                              {/* Estado editable SOLO si es pendiente */}
                              {venta.estado === "pendiente" ? (
                                <Select
                                  value={ESTADOS_OPCIONES.find(
                                    (e) => e.value === venta.estado
                                  )}
                                  options={ESTADOS_OPCIONES.filter(
                                    (e) => e.value !== "devolucion"
                                  )}
                                  onChange={(selected) =>
                                    handleEstadoChange(venta, selected)
                                  }
                                  menuPortalTarget={document.body}
                                  menuPosition="fixed"
                                  styles={{
                                    control: (base) => ({
                                      ...base,
                                      minWidth: 130,
                                      fontSize: 13,
                                      background: getEstadoColor(venta.estado),
                                      color: "#fff",
                                    }),
                                    singleValue: (base) => ({
                                      ...base,
                                      color: "#fff",
                                    }),
                                    menu: (base) => ({
                                      ...base,
                                      fontSize: 13,
                                      zIndex: 9999,
                                    }),
                                    menuPortal: (base) => ({
                                      ...base,
                                      zIndex: 9999,
                                    }),
                                  }}
                                />
                              ) : (
                                <span
                                  style={{
                                    background: getEstadoColor(venta.estado),
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
                                  {capitalize(venta.estado)}
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
            {/* Tabla de detalle de venta DERECHA */}
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
                Detalle de venta
              </h4>
              {!ventaSeleccionada ? (
                <div style={{ textAlign: "center", color: "#888" }}>
                  Selecciona una venta para ver los detalles
                </div>
              ) : cargandoDetalle ? (
                <Spinner />
              ) : detallesVenta.length === 0 ? (
                <div style={{ textAlign: "center", color: "#888" }}>
                  Sin detalles para esta venta
                </div>
              ) : (
                <div
                  style={{ display: "flex", flexDirection: "column", gap: 16 }}
                >
                  {detallesVenta.map((d) => (
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
                        {d.producto?.nombre || `Producto ID: ${d.producto_id}`}
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

// Capitaliza estados
function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default VentasEmpleadoPage;
