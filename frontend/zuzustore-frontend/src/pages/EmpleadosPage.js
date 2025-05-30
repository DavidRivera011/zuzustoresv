import React, { useEffect, useState } from "react";
import axios from "axios";
import { Modal, Button, Form, Table, Spinner } from "react-bootstrap";
import { FiMenu } from "react-icons/fi";
import { FaUserCircle } from "react-icons/fa";
import { jwtDecode } from "jwt-decode";

const icondash = "/img/icondashwhite.png";
const iconproductos = "/img/iconproductowhite.png";
const iconventas = "/img/iconventawhite.png";
const iconorden = "/img/iconordeneswhite.png";
const iconuser = "/img/iconuserwhite.png";
const iconmovimientos = "/img/movimientowhite.png";
const iconclientes = "/img/clienticonwhite.png";

function SidebarImageIcon({ src, alt, to }) {
  const handleClick = () => {
    window.location.href = to;
  };
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

function EmpleadosPage() {
  const [empleados, setEmpleados] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    correo: "",
    contrasena: "",
    fechaNacimiento: "",
    telefono: "",
    fechaIngreso: new Date().toISOString().slice(0, 10),
    salario: "",
    fechaSalida: "",
    rol: "empleado",
  });

  const [editando, setEditando] = useState(false);
  const [idEditando, setIdEditando] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [idEliminar, setIdEliminar] = useState(null);

  // Al abrir el modal para editar:
  const handleEditEmpleado = (empleado) => {
    setEditando(true);
    setIdEditando(empleado.id);
    setForm({
      nombre: empleado.nombres,
      apellido: empleado.apellidos,
      correo: empleado.correo,
      contrasena: "",
      fechaNacimiento: empleado.fechaNacimiento
        ? empleado.fechaNacimiento.substring(0, 10)
        : "",
      telefono: empleado.telefono || "",
      fechaIngreso: empleado.fechaIngreso
        ? empleado.fechaIngreso.substring(0, 10)
        : new Date().toISOString().slice(0, 10),
      salario: empleado.salario || "",
      fechaSalida: empleado.fechaSalida
        ? empleado.fechaSalida.substring(0, 10)
        : "",
      rol: empleado.rol || "empleado",
    });
    setShowModal(true);
  };

  // Limpiar y cerrar el modal
  const handleCloseModal = () => {
    setShowModal(false);
    setEditando(false);
    setIdEditando(null);
    setForm({
      nombre: "",
      apellido: "",
      correo: "",
      contrasena: "",
      fechaNacimiento: "",
      telefono: "",
      fechaIngreso: new Date().toISOString().slice(0, 10),
      salario: "",
      fechaSalida: "",
    });
  };

  const [busqueda, setBusqueda] = useState("");

  // Guardar (agregar o editar)
  const handleSaveEmpleado = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    const datos = { ...form };
    try {
      if (editando && idEditando) {
        // EDITAR
        await axios.put(
          `http://localhost:8080/api/usuarios/empleados/${idEditando}`,
          datos,
          { headers: { Authorization: `Bearer ${token}` } }
        );
      } else {
        // AGREGAR
        await axios.post(
          "http://localhost:8080/api/usuarios/empleados",
          datos,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
      }
      handleCloseModal();
      fetchEmpleados();
    } catch {
      alert("Error al guardar empleado");
    }
  };

  // Eliminar empleado
  const handleDeleteEmpleado = async () => {
    const token = localStorage.getItem("token");
    try {
      await axios.delete(
        `http://localhost:8080/api/usuarios/empleados/${idEliminar}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setShowDeleteModal(false);
      setIdEliminar(null);
      fetchEmpleados();
    } catch {
      alert("Error al eliminar empleado");
    }
  };

  // Paginación y ordenamiento
  const filasPorPagina = 14;
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("id");
  const [ascendente, setAscendente] = useState(true);

  // Para header
  const [usuario, setUsuario] = useState({ nombre: "Usuario", rol: "Rol" });
  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

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
    fetchEmpleados();
  }, []);

  const fetchEmpleados = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get(
        "http://localhost:8080/api/usuarios/empleados",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setEmpleados(res.data);
    } catch {
      setEmpleados([]);
    }
    setCargando(false);
  };

  // Ordenar empleados según la columna seleccionada
  const getSortedEmpleados = (data = empleados) => {
    const sorted = [...data].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
      // Si es número
      if (columnaOrden === "id" || columnaOrden === "salario") {
        valorA = Number(valorA);
        valorB = Number(valorB);
      }
      // Si es fecha
      if (columnaOrden.includes("fecha")) {
        valorA = valorA || "";
        valorB = valorB || "";
        return ascendente
          ? valorA.localeCompare(valorB)
          : valorB.localeCompare(valorA);
      }
      // Por defecto: string/número
      if (valorA < valorB) return ascendente ? -1 : 1;
      if (valorA > valorB) return ascendente ? 1 : -1;
      return 0;
    });
    return sorted;
  };

  // Calcular paginación
  const empleadosFiltrados = empleados.filter((emp) => {
    const search = busqueda.toLowerCase();
    return (
      emp.nombres?.toLowerCase().includes(search) ||
      emp.apellidos?.toLowerCase().includes(search) ||
      emp.correo?.toLowerCase().includes(search) ||
      emp.rol?.toLowerCase().includes(search) ||
      (emp.telefono && emp.telefono.toLowerCase().includes(search))
    );
  });
  const empleadosOrdenados = getSortedEmpleados(empleadosFiltrados);

  const totalPaginas = Math.ceil(empleadosOrdenados.length / filasPorPagina);
  const empleadosPagina = empleadosOrdenados.slice(
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

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

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
          to="/registro-inventario"
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
              gap: 12,
            }}
          >
            <h2 style={{ margin: 0 }}>Empleados</h2>
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <Form.Control
                type="text"
                placeholder="Buscar empleado por correo..."
                value={busqueda}
                onChange={(e) => {
                  setBusqueda(e.target.value);
                  setPaginaActual(1);
                }}
                style={{ width: 1400, minWidth: 200, fontSize: 15 }}
              />
              <Button variant="primary" onClick={() => setShowModal(true)}>
                Agregar empleado
              </Button>
            </div>
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
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("id")}
                      >
                        ID{" "}
                        {columnaOrden === "id" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("nombres")}
                      >
                        Nombres{" "}
                        {columnaOrden === "nombres"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("apellidos")}
                      >
                        Apellidos{" "}
                        {columnaOrden === "apellidos"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("correo")}
                      >
                        Correo{" "}
                        {columnaOrden === "correo"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("fechaNacimiento")}
                      >
                        Fecha Nacimiento{" "}
                        {columnaOrden === "fechaNacimiento"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("telefono")}
                      >
                        Teléfono{" "}
                        {columnaOrden === "telefono"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("salario")}
                      >
                        Salario{" "}
                        {columnaOrden === "salario"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("fechaIngreso")}
                      >
                        Fecha Ingreso{" "}
                        {columnaOrden === "fechaIngreso"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("fechaSalida")}
                      >
                        Fecha Salida{" "}
                        {columnaOrden === "fechaSalida"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("rol")}
                      >
                        Rol{" "}
                        {columnaOrden === "rol" ? (ascendente ? "▲" : "▼") : ""}
                      </th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {empleadosPagina.length === 0 ? (
                      <tr>
                        <td colSpan="9" className="text-center">
                          No hay empleados
                        </td>
                      </tr>
                    ) : (
                      empleadosPagina.map((emp) => (
                        <tr key={emp.id}>
                          <td>{emp.id}</td>
                          <td>{emp.nombres}</td>
                          <td>{emp.apellidos}</td>
                          <td>{emp.correo}</td>
                          <td>{emp.fechaNacimiento}</td>
                          <td>{emp.telefono}</td>
                          <td>
                            {emp.salario != null ? `$${emp.salario}` : "-"}
                          </td>
                          <td>{emp.fechaIngreso || "-"}</td>
                          <td>
                            {emp.fechaSalida ? (
                              <span
                                style={{
                                  color: "#d32f2f",
                                  background: "#ffebee",
                                  padding: "2px 8px",
                                  borderRadius: 8,
                                  fontWeight: 600,
                                }}
                              >
                                {emp.fechaSalida}
                              </span>
                            ) : (
                              "-"
                            )}
                          </td>
                          <td>{emp.rol ? capitalize(emp.rol) : "-"}</td>
                          <td>
                            <Button
                              variant="warning"
                              size="sm"
                              style={{ marginRight: 6 }}
                              onClick={() => handleEditEmpleado(emp)}
                            >
                              Editar
                            </Button>
                            <Button
                              variant="danger"
                              size="sm"
                              onClick={() => {
                                setIdEliminar(emp.id);
                                setShowDeleteModal(true);
                              }}
                            >
                              Eliminar
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

      {/* Modal para agregar */}
      <Modal
        show={showModal}
        onHide={handleCloseModal}
        size="lg" // Hacemos el modal más ancho
        centered
        dialogClassName="modal-xxl"
        style={{ minWidth: 900 }}
      >
        <Form onSubmit={handleSaveEmpleado}>
          <Modal.Header closeButton>
            <Modal.Title>
              {editando ? "Editar Empleado" : "Agregar Empleado"}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <div className="container-fluid">
              <div className="row">
                {/* Primera columna */}
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Nombres</Form.Label>
                    <Form.Control
                      name="nombre"
                      value={form.nombre}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Apellidos</Form.Label>
                    <Form.Control
                      name="apellido"
                      value={form.apellido}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Correo</Form.Label>
                    <Form.Control
                      name="correo"
                      type="email"
                      value={form.correo}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>
                      Contraseña{" "}
                      {editando && (
                        <span style={{ color: "#888" }}>
                          (dejar vacío para no cambiar)
                        </span>
                      )}
                    </Form.Label>
                    <Form.Control
                      name="contrasena"
                      type="password"
                      value={form.contrasena}
                      onChange={handleChange}
                      minLength={editando ? 0 : 8}
                      required={!editando}
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Rol</Form.Label>
                    <Form.Control
                      as="select"
                      name="rol"
                      value={form.rol}
                      onChange={handleChange}
                      required
                    >
                      <option value="empleado">Empleado</option>
                      <option value="admin">Administrador</option>
                    </Form.Control>
                  </Form.Group>
                </div>
                {/* Segunda columna */}
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Fecha Nacimiento</Form.Label>
                    <Form.Control
                      name="fechaNacimiento"
                      type="date"
                      value={form.fechaNacimiento}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Teléfono</Form.Label>
                    <Form.Control
                      name="telefono"
                      value={form.telefono}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Fecha de Ingreso</Form.Label>
                    <Form.Control
                      name="fechaIngreso"
                      type="date"
                      value={form.fechaIngreso}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>Salario</Form.Label>
                    <Form.Control
                      name="salario"
                      type="number"
                      step="0.01"
                      min="0"
                      value={form.salario}
                      onChange={handleChange}
                      required
                    />
                  </Form.Group>
                  <Form.Group className="mb-3">
                    <Form.Label>
                      Fecha de Salida{" "}
                      <span style={{ color: "#888", fontWeight: 400 }}>
                        (opcional)
                      </span>
                    </Form.Label>
                    <Form.Control
                      name="fechaSalida"
                      type="date"
                      value={form.fechaSalida}
                      onChange={handleChange}
                    />
                  </Form.Group>
                </div>
              </div>
            </div>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancelar
            </Button>
            <Button type="submit" variant="success">
              {editando ? "Guardar cambios" : "Agregar"}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Modal eliminar */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Eliminar Empleado</Modal.Title>
        </Modal.Header>
        <Modal.Body>¿Seguro que quieres eliminar este empleado?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Cancelar
          </Button>
          <Button variant="danger" onClick={handleDeleteEmpleado}>
            Eliminar
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}

function capitalize(str) {
  if (!str) return "";
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export default EmpleadosPage;
