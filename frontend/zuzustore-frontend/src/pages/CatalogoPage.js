import React, { useEffect, useState } from "react";
import axios from "axios";
import { jwtDecode } from "jwt-decode"; // IMPORTANTE

const MOCK_LOGO = "/img/zuzulogo.png";

function CatalogoPage() {
  const [productos, setProductos] = useState([]);
  const [carrito, setCarrito] = useState([]);
  const [showCarrito, setShowCarrito] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const [finalizandoCompra, setFinalizandoCompra] = useState(false);
  const [mensajeCompra, setMensajeCompra] = useState("");

  // Detectar si el usuario está logueado
  const isLogged = !!localStorage.getItem("token");

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/productos/public")
      .then((res) => setProductos(res.data))
      .catch(() => setProductos([]));
  }, []);

  const handleIntentoAgregar = (producto) => {
    if (!isLogged) {
      setShowLoginModal(true);
      return;
    }
    setCarrito((prev) => [...prev, producto]);
    setShowCarrito(true);
  };

  const handleIntentoAbrirCarrito = () => {
    if (!isLogged) {
      setShowLoginModal(true);
      return;
    }
    setShowCarrito(true);
  };

  const eliminarDelCarrito = (index) => {
    setCarrito((prev) => prev.filter((_, i) => i !== index));
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setShowUserMenu(false);
    window.location.href = "/login";
  };

  // Cierra el menú si das clic fuera (opcional, pero mejora UX)
  useEffect(() => {
    if (!showUserMenu) return;
    const handleClickOutside = (e) => {
      if (
        !e.target.closest(".user-menu-dropdown") &&
        !e.target.closest(".fa-user")
      ) {
        setShowUserMenu(false);
      }
    };
    window.addEventListener("mousedown", handleClickOutside);
    return () => window.removeEventListener("mousedown", handleClickOutside);
  }, [showUserMenu]);

  const totalPrecio = carrito.reduce(
    (sum, prod) => sum + Number(prod.precio),
    0
  );

  const handleFinalizarCompra = async () => {
    if (carrito.length === 0) return;

    setFinalizandoCompra(true);
    setMensajeCompra("");
    const token = localStorage.getItem("token");

    let clienteId = null;
    // Decodifica el JWT para sacar el id del cliente
    try {
      const decoded = jwtDecode(token);
      clienteId = decoded.id || decoded.sub || null;
    } catch {
      setMensajeCompra("No se pudo identificar el usuario.");
      setFinalizandoCompra(false);
      return;
    }

    if (!clienteId) {
      setMensajeCompra("No se pudo identificar el usuario.");
      setFinalizandoCompra(false);
      return;
    }

    // Arma los items
    const items = carrito.map((prod) => ({
      productoId: prod.id,
      cantidad: 1, // Si tienes selector de cantidad, cámbialo aquí
    }));

    // Arma el request
    const data = {
      clienteId,
      items,
      // puedes agregar telefono, direccionEntrega, etc si lo pides en el frontend
    };

    try {
      await axios.post("http://localhost:8080/api/ordenes", data, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMensajeCompra("¡Compra realizada con éxito! Revisa tu correo.");
      setCarrito([]);
      setTimeout(() => {
        setShowCarrito(false);
        setMensajeCompra("");
      }, 2000);
    } catch (err) {
      setMensajeCompra(
        err?.response?.data?.message ||
          "Error al procesar la compra. Intenta de nuevo."
      );
    } finally {
      setFinalizandoCompra(false);
    }
  };

  // NUEVO: Manejo del click en el ícono de usuario
  const handleUserIconClick = () => {
    if (isLogged) {
      setShowUserMenu((prev) => !prev);
    } else {
      setShowLoginModal(true);
    }
  };

  return (
    <div style={{ minHeight: "100vh", background: "#efd0df" }}>
      {/* Header */}
      <header
        style={{
          background: "#fff",
          padding: "16px 0",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          borderBottom: "1px solid #eee",
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: 18 }}>
          <img
            src={MOCK_LOGO}
            alt="logo"
            style={{ width: 70, borderRadius: "50%", marginLeft: 12 }}
          />
          <nav style={{ display: "flex", gap: 8 }}>
            <NavBtn text="catálogo" />
            <NavBtn text="maquillaje" />
            <NavBtn text="cremas" />
            <NavBtn text="nosotros" />
          </nav>
        </div>
        {/* Iconos derecha */}
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 14,
            marginRight: 18,
            position: "relative",
          }}
        >
          {/* USER ICON WITH DROPDOWN */}
          <div style={{ position: "relative" }}>
            <i
              className="fa fa-user"
              style={{ fontSize: 18, cursor: "pointer" }}
              onClick={handleUserIconClick}
            ></i>
            {showUserMenu && isLogged && (
              <div
                className="user-menu-dropdown"
                style={{
                  position: "absolute",
                  right: 0,
                  top: 32,
                  background: "#fff",
                  boxShadow: "0 2px 12px #0002",
                  borderRadius: 10,
                  padding: "15px 26px",
                  zIndex: 99,
                  minWidth: 160,
                  textAlign: "left",
                }}
              >
                <div
                  style={{ marginBottom: 12, color: "#333", fontWeight: 500 }}
                >
                  Sesión activa
                </div>
                <button
                  onClick={handleLogout}
                  style={{
                    background: "#ffe6f1",
                    color: "#c2185b",
                    border: "none",
                    borderRadius: 8,
                    padding: "8px 20px",
                    cursor: "pointer",
                    fontWeight: 600,
                    width: "100%",
                    fontSize: 15,
                  }}
                >
                  Cerrar sesión
                </button>
              </div>
            )}
          </div>
          {/* Otros iconos */}
          <i
            className="fa fa-search"
            style={{ fontSize: 18, cursor: "pointer" }}
          ></i>
          <i
            className="fa fa-shopping-cart"
            style={{ fontSize: 22, cursor: "pointer", marginLeft: 6 }}
            onClick={handleIntentoAbrirCarrito}
          ></i>
        </div>
      </header>

      {/* Contenido principal */}
      <main>
        <h1
          style={{
            textAlign: "center",
            fontWeight: 400,
            margin: "35px 0 20px 0",
            fontSize: 42,
            letterSpacing: 1,
            color: "#222",
          }}
        >
          CATÁLOGO
        </h1>
        <div
          style={{
            marginLeft: 44,
            fontSize: 17,
            color: "#5c5157",
            fontWeight: 300,
            marginBottom: 12,
          }}
        >
          filtros ↓
        </div>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(auto-fit, minmax(240px, 1fr))",
            gap: 30,
            maxWidth: 1260,
            margin: "0 auto",
            padding: "0 18px 40px 18px",
          }}
        >
          {productos.length === 0 ? (
            <div style={{ gridColumn: "1/-1", textAlign: "center" }}>
              No hay productos
            </div>
          ) : (
            productos.map((prod, i) => (
              <CardProducto
                key={prod.id}
                producto={prod}
                onAgregar={() => handleIntentoAgregar(prod)}
              />
            ))
          )}
        </div>
      </main>

      {/* Carrito lateral */}
      {showCarrito && (
        <CarritoLateral
          carrito={carrito}
          onClose={() => setShowCarrito(false)}
          onEliminar={eliminarDelCarrito}
          totalPrecio={totalPrecio}
          onFinalizarCompra={handleFinalizarCompra}
          finalizandoCompra={finalizandoCompra}
          mensajeCompra={mensajeCompra}
        />
      )}

      {/* Modal de Login necesario */}
      {showLoginModal && (
        <LoginModal
          onClose={() => setShowLoginModal(false)}
          textTitle="¡Inicia sesión para continuar!"
          textDesc="Debes iniciar sesión para acceder a tu cuenta y disfrutar todas las funciones."
        />
      )}
    </div>
  );
}

// Botón menú
function NavBtn({ text }) {
  return (
    <button
      style={{
        border: "1px solid #aaa",
        background: "#fff",
        borderRadius: 20,
        padding: "4px 16px",
        fontSize: 13,
        marginRight: 4,
        cursor: "pointer",
      }}
    >
      {text}
    </button>
  );
}

// Card producto
function CardProducto({ producto, onAgregar }) {
  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 8,
        boxShadow: "0 2px 8px #0001",
        padding: 16,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        minHeight: 320,
      }}
    >
      <img
        src={
          producto.imagenUrl ||
          "https://via.placeholder.com/160x160.png?text=Producto"
        }
        alt={producto.nombre}
        style={{
          width: 170,
          height: 170,
          objectFit: "contain",
          marginBottom: 10,
          background: "#f5f1ef",
          borderRadius: 4,
        }}
      />
      <div style={{ textAlign: "left", width: "100%" }}>
        <div style={{ fontWeight: 700, fontSize: 17, letterSpacing: 0.5 }}>
          {producto.marca ? producto.marca.toUpperCase() : "MARCA"}
        </div>
        <div
          style={{
            fontWeight: 400,
            fontSize: 15,
            marginTop: 3,
            marginBottom: 2,
          }}
        >
          {producto.nombre}
        </div>
        <div style={{ color: "#9b7b8d", fontSize: 14, marginBottom: 10 }}>
          {producto.descripcion || "Versión del producto"}
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <span style={{ fontWeight: 500, color: "#7d1445" }}>
            ${Number(producto.precio).toFixed(2)}
          </span>
          <button
            onClick={onAgregar}
            style={{
              border: "none",
              background: "#ffe6f1",
              color: "#c2185b",
              borderRadius: 8,
              padding: "5px 15px",
              cursor: "pointer",
              fontWeight: 600,
              marginLeft: 8,
              fontSize: 14,
            }}
          >
            Añadir
          </button>
        </div>
      </div>
    </div>
  );
}

// Carrito lateral
function CarritoLateral({
  carrito,
  onClose,
  onEliminar,
  totalPrecio,
  onFinalizarCompra,
  finalizandoCompra,
  mensajeCompra,
}) {
  return (
    <div
      style={{
        position: "fixed",
        top: 0,
        right: 0,
        width: 350,
        height: "100vh",
        background: "#fff",
        boxShadow: "-2px 0 20px #0004",
        zIndex: 1000,
        padding: "20px 14px 0 14px",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <div style={{ display: "flex", alignItems: "center", marginBottom: 24 }}>
        <i
          className="fa fa-shopping-cart"
          style={{ fontSize: 22, marginRight: 8 }}
        ></i>
        <span style={{ fontWeight: 600, fontSize: 17 }}>Carrito</span>
        <button
          onClick={onClose}
          style={{
            marginLeft: "auto",
            border: "none",
            background: "none",
            fontSize: 22,
            cursor: "pointer",
            color: "#c2185b",
          }}
        >
          ×
        </button>
      </div>
      {/* Productos del carrito */}
      <div style={{ flex: 1, overflowY: "auto" }}>
        {carrito.length === 0 ? (
          <div style={{ color: "#999", textAlign: "center", marginTop: 40 }}>
            No hay productos en el carrito
          </div>
        ) : (
          carrito.map((prod, i) => (
            <div
              key={i}
              style={{
                display: "flex",
                alignItems: "center",
                marginBottom: 14,
                background: "#f6e6ef",
                borderRadius: 10,
                padding: 10,
                boxShadow: "0 1px 5px #0001",
              }}
            >
              <img
                src={prod.imagenUrl || "https://via.placeholder.com/55"}
                alt={prod.nombre}
                style={{
                  width: 55,
                  height: 55,
                  objectFit: "cover",
                  borderRadius: 8,
                  marginRight: 10,
                }}
              />
              <div style={{ flex: 1 }}>
                <div style={{ fontWeight: 700, fontSize: 13 }}>
                  {prod.nombre}
                </div>
                <div
                  style={{ fontWeight: 400, fontSize: 13, color: "#5c5157" }}
                >
                  {prod.descripcion}
                </div>
                <div
                  style={{ fontWeight: 600, fontSize: 14, color: "#7d1445" }}
                >
                  ${Number(prod.precio).toFixed(2)}
                </div>
              </div>
              <button
                onClick={() => onEliminar(i)}
                style={{
                  background: "none",
                  border: "none",
                  color: "#c2185b",
                  fontSize: 20,
                  fontWeight: 700,
                  marginLeft: 8,
                  cursor: "pointer",
                }}
              >
                x
              </button>
            </div>
          ))
        )}
      </div>
      {/* Pie carrito */}
      <div
        style={{ borderTop: "1px solid #eee", marginTop: 8, paddingTop: 10 }}
      >
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            fontWeight: 500,
            marginBottom: 7,
          }}
        >
          <span>Cant. de productos:</span>
          <span>{carrito.length}</span>
        </div>
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            fontWeight: 600,
          }}
        >
          <span>Precio total:</span>
          <span>${totalPrecio.toFixed(2)}</span>
        </div>
        <button
          style={{
            marginTop: 17,
            width: "100%",
            background: "#c2185b",
            color: "#fff",
            borderRadius: 12,
            padding: "12px 0",
            fontSize: 18,
            fontWeight: 700,
            border: "none",
            cursor: finalizandoCompra ? "wait" : "pointer",
            opacity: finalizandoCompra ? 0.7 : 1,
          }}
          onClick={onFinalizarCompra}
          disabled={carrito.length === 0 || finalizandoCompra}
        >
          {finalizandoCompra ? "Procesando..." : "Finalizar compra"}
        </button>
        {mensajeCompra && (
          <div
            style={{
              color: mensajeCompra.startsWith("¡Compra")
                ? "#388e3c"
                : "#c2185b",
              background: "#fbe9ea",
              borderRadius: 7,
              padding: "7px 12px",
              marginTop: 10,
              textAlign: "center",
              fontWeight: 500,
              fontSize: 15,
            }}
          >
            {mensajeCompra}
          </div>
        )}
      </div>
    </div>
  );
}

// MODAL de login requerido - admite textos custom
function LoginModal({ onClose, textTitle, textDesc }) {
  return (
    <div
      style={{
        position: "fixed",
        zIndex: 1100,
        top: 0,
        left: 0,
        width: "100vw",
        height: "100vh",
        background: "rgba(0,0,0,0.2)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: "#fff",
          borderRadius: 18,
          padding: "38px 28px",
          boxShadow: "0 3px 24px #0003",
          minWidth: 310,
          textAlign: "center",
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <h3 style={{ fontWeight: 700, marginBottom: 12, color: "#c2185b" }}>
          {textTitle || "¡Inicia sesión para comprar!"}
        </h3>
        <div style={{ fontSize: 16, marginBottom: 22 }}>
          {textDesc ||
            "Debes iniciar sesión para añadir productos al carrito y solicitar tu compra."}
        </div>
        <div style={{ display: "flex", gap: 18, justifyContent: "center" }}>
          <button
            onClick={() => (window.location.href = "/login")}
            style={{
              background: "#c2185b",
              color: "#fff",
              borderRadius: 10,
              border: "none",
              padding: "10px 18px",
              fontWeight: 700,
              fontSize: 16,
              cursor: "pointer",
            }}
          >
            Iniciar sesión
          </button>
          <button
            onClick={onClose}
            style={{
              background: "#eee",
              color: "#444",
              borderRadius: 10,
              border: "none",
              padding: "10px 18px",
              fontWeight: 600,
              fontSize: 16,
              cursor: "pointer",
            }}
          >
            Cancelar
          </button>
        </div>
      </div>
    </div>
  );
}

export default CatalogoPage;
