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

function ProductosPage() {
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [form, setForm] = useState({
    nombre: "",
    descripcion: "",
    marca: "",
    precio: "",
    stock: "",
    categoria_id: "",
    imagenUrl: "",
    estado: "disponible",
  });

  const [categorias, setCategorias] = useState([]);
  const [editando, setEditando] = useState(false);
  const [idEditando, setIdEditando] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [idEliminar, setIdEliminar] = useState(null);

  const [usuario, setUsuario] = useState({ nombre: "Usuario", rol: "Rol" });

  // PAGINACIÓN Y ORDENAMIENTO
  const filasPorPagina = 13;
  const [paginaActual, setPaginaActual] = useState(1);
  const [columnaOrden, setColumnaOrden] = useState("id");
  const [ascendente, setAscendente] = useState(true);

  // SIDEBAR & HEADER
  const [showMenu, setShowMenu] = useState(false);
  const [showLogout, setShowLogout] = useState(false);

  // Icon imagen
  const [showImageModal, setShowImageModal] = useState(false);
  const [selectedImageUrl, setSelectedImageUrl] = useState(null);

  const [busqueda, setBusqueda] = useState("");

  const handleImageClick = (url) => {
    setSelectedImageUrl(url);
    setShowImageModal(true);
  };
  const handleCloseImageModal = () => {
    setShowImageModal(false);
    setSelectedImageUrl(null);
  };

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
    fetchProductos();
    fetchCategorias();
  }, []);

  const fetchProductos = async () => {
    setCargando(true);
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/productos", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProductos(res.data);
    } catch (err) {
      setProductos([]);
    }
    setCargando(false);
  };

  const fetchCategorias = async () => {
    const token = localStorage.getItem("token");
    try {
      const res = await axios.get("http://localhost:8080/api/categorias", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCategorias(res.data);
    } catch (err) {
      setCategorias([]);
    }
  };

  // --- CRUD ---
  const handleEditProducto = (producto) => {
    setEditando(true);
    setIdEditando(producto.id);
    setForm({
      nombre: producto.nombre,
      descripcion: producto.descripcion,
      marca: producto.marca,
      precio: producto.precio,
      stock: producto.stock,
      categoria_id: producto.categoria?.id || producto.categoria_id || "",
      imagenUrl: producto.imagenUrl || "",
      estado: producto.estado,
    });
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditando(false);
    setIdEditando(null);
    setForm({
      nombre: "",
      descripcion: "",
      marca: "",
      precio: "",
      stock: "",
      categoria_id: "",
      imagenUrl: "",
      estado: "disponible",
    });
  };

  const handleSaveProducto = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    const payload = {
      ...form,
      imagenUrl: form.imagenUrl,
      categoria: { id: form.categoria_id },
    };

    try {
      if (editando && idEditando) {
        await axios.put(
          `http://localhost:8080/api/productos/${idEditando}`,
          payload,
          { headers: { Authorization: `Bearer ${token}` } }
        );
      } else {
        await axios.post("http://localhost:8080/api/productos", payload, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }
      handleCloseModal();
      fetchProductos();
    } catch {
      alert("Error al guardar producto");
    }
  };

  const handleDeleteProducto = async () => {
    const token = localStorage.getItem("token");
    try {
      await axios.delete(`http://localhost:8080/api/productos/${idEliminar}`, {
        headers: { Authorization: `Bearer ${token}` }, // <-- Y ESTO
      });
      setShowDeleteModal(false);
      setIdEliminar(null);
      fetchProductos();
    } catch {
      alert("Error al eliminar producto");
    }
  };

  // --- ORDENAMIENTO Y PAGINACIÓN ---
  const getSortedProductos = () => {
    const sorted = [...productos].sort((a, b) => {
      let valorA = a[columnaOrden] ?? "";
      let valorB = b[columnaOrden] ?? "";
      // Si es número o precio
      if (
        columnaOrden === "id" ||
        columnaOrden === "precio" ||
        columnaOrden === "stock"
      ) {
        valorA = Number(valorA);
        valorB = Number(valorB);
      }
      // Por defecto: string/número
      if (valorA < valorB) return ascendente ? -1 : 1;
      if (valorA > valorB) return ascendente ? 1 : -1;
      return 0;
    });
    return sorted;
  };

  const productosOrdenados = getSortedProductos();
  const productosFiltrados = productosOrdenados.filter((prod) =>
    prod.nombre.toLowerCase().includes(busqueda.toLowerCase())
  );
  const totalPaginas = Math.ceil(productosFiltrados.length / filasPorPagina);
  const productosPagina = productosFiltrados.slice(
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

  // --- LOGOUT HEADER ---
  const handleLogout = () => {
    localStorage.removeItem("token");
    window.location.href = "/login";
  };

  // --- RENDER ---
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
            <h2 style={{ margin: 0 }}>Productos</h2>
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <Form.Control
                type="text"
                placeholder="Buscar producto..."
                value={busqueda}
                onChange={(e) => {
                  setBusqueda(e.target.value);
                  setPaginaActual(1);
                }}
                style={{ width: "1400px" }}
              />
              <Button variant="primary" onClick={() => setShowModal(true)}>
                Agregar producto
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
                        onClick={() => cambiarOrden("nombre")}
                      >
                        Nombre{" "}
                        {columnaOrden === "nombre"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th>Descripción</th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("marca")}
                      >
                        Marca{" "}
                        {columnaOrden === "marca"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("precio")}
                      >
                        Precio{" "}
                        {columnaOrden === "precio"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th
                        style={{ cursor: "pointer" }}
                        onClick={() => cambiarOrden("stock")}
                      >
                        Stock{" "}
                        {columnaOrden === "stock"
                          ? ascendente
                            ? "▲"
                            : "▼"
                          : ""}
                      </th>
                      <th>Categoría</th>
                      <th>Estado</th>
                      <th>Imagen</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {productosPagina.length === 0 ? (
                      <tr>
                        <td colSpan="10" className="text-center">
                          No hay productos
                        </td>
                      </tr>
                    ) : (
                      productosPagina.map((prod) => (
                        <tr key={prod.id}>
                          <td>{prod.id}</td>
                          <td>{prod.nombre}</td>
                          <td>{prod.descripcion}</td>
                          <td>{prod.marca}</td>
                          <td>${prod.precio}</td>
                          <td>{prod.stock}</td>
                          <td>
                            {prod.categoria?.nombre ||
                              categorias.find((c) => c.id === prod.categoria_id)
                                ?.nombre ||
                              "-"}
                          </td>
                          <td>
                            {prod.estado === "disponible"
                              ? "Disponible"
                              : "No disponible"}
                          </td>
                          <td>
                            {prod.imagenUrl ? (
                              <img
                                src={prod.imagenUrl || prod.imagenUrl}
                                alt={prod.nombre}
                                style={{
                                  width: 40,
                                  height: 40,
                                  objectFit: "cover",
                                  cursor: "pointer",
                                  borderRadius: 8,
                                  boxShadow: "0 1px 6px #0002",
                                  transition: "transform 0.1s",
                                }}
                                onClick={() =>
                                  handleImageClick(
                                    prod.imagenUrl || prod.imagenUrl
                                  )
                                }
                                title="Ver imagen"
                              />
                            ) : (
                              "-"
                            )}
                          </td>
                          <td>
                            <Button
                              variant="warning"
                              size="sm"
                              style={{ marginRight: 6 }}
                              onClick={() => handleEditProducto(prod)}
                            >
                              Editar
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

      {/* Modal para agregar/editar */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Form onSubmit={handleSaveProducto}>
          <Modal.Header closeButton>
            <Modal.Title>
              {editando ? "Editar Producto" : "Agregar Producto"}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-2">
              <Form.Label>Nombre</Form.Label>
              <Form.Control
                name="nombre"
                value={form.nombre}
                onChange={handleChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Descripción</Form.Label>
              <Form.Control
                as="textarea"
                name="descripcion"
                value={form.descripcion}
                onChange={handleChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Marca</Form.Label>
              <Form.Control
                name="marca"
                value={form.marca}
                onChange={handleChange}
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Precio</Form.Label>
              <Form.Control
                name="precio"
                type="number"
                min="0"
                step="0.01"
                value={form.precio}
                onChange={handleChange}
                required
              />
            </Form.Group>

            <Form.Group className="mb-2">
              <Form.Label>Categoría</Form.Label>
              <Form.Control
                as="select"
                name="categoria_id"
                value={form.categoria_id}
                onChange={handleChange}
                required
              >
                <option value="">Seleccione una categoría</option>
                {categorias.map((cat) => (
                  <option value={cat.id} key={cat.id}>
                    {cat.nombre}
                  </option>
                ))}
              </Form.Control>
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>URL de Imagen</Form.Label>
              <Form.Control
                name="imagenUrl"
                value={form.imagenUrl}
                onChange={handleChange}
              />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label>Estado</Form.Label>
              <Form.Control
                as="select"
                name="estado"
                value={form.estado}
                onChange={handleChange}
                required
              >
                <option value="disponible">Disponible</option>
                <option value="no_disponible">No disponible</option>
              </Form.Control>
            </Form.Group>
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
          <Modal.Title>Eliminar Producto</Modal.Title>
        </Modal.Header>
        <Modal.Body>¿Seguro que quieres eliminar este producto?</Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Cancelar
          </Button>
          <Button variant="danger" onClick={handleDeleteProducto}>
            Eliminar
          </Button>
        </Modal.Footer>
      </Modal>

      <Modal
        show={showImageModal}
        onHide={handleCloseImageModal}
        centered
        size="lg"
      >
        <Modal.Body style={{ textAlign: "center", background: "#222" }}>
          {selectedImageUrl && (
            <img
              src={selectedImageUrl}
              alt="Producto"
              style={{
                maxWidth: "95%",
                maxHeight: "80vh",
                borderRadius: 14,
                boxShadow: "0 3px 24px #0009",
              }}
            />
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

export default ProductosPage;
