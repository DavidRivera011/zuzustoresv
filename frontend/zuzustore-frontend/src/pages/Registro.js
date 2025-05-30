import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const logo = "/img/zuzulogo.png";
const backgroundImg = "/img/zuzufondoregister.jpg";

function Registro() {
  const [nombres, setNombres] = useState("");
  const [apellidos, setApellidos] = useState("");
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [fechaNacimiento, setFechaNacimiento] = useState("");
  const [telefono, setTelefono] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [errores, setErrores] = useState({});
  const navigate = useNavigate();

  // Validaciones
  const validar = () => {
    let err = {};

    if (!nombres.trim()) err.nombres = "Nombres requeridos";
    if (!apellidos.trim()) err.apellidos = "Apellidos requeridos";
    if (!correo) {
      err.correo = "Correo requerido";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) {
      err.correo = "Correo no válido";
    }
    if (!contrasena) {
      err.contrasena = "Contraseña requerida";
    } else if (contrasena.length < 8) {
      err.contrasena = "La contraseña debe tener al menos 8 caracteres";
    }
    if (!telefono) {
      err.telefono = "Teléfono requerido";
    } else if (!/^\d{8}$/.test(telefono)) {
      err.telefono = "Debe tener 8 dígitos";
    }
    if (!fechaNacimiento) {
      err.fechaNacimiento = "Fecha requerida";
    } else if (new Date(fechaNacimiento) > new Date()) {
      err.fechaNacimiento = "No puede ser en el futuro";
    }

    setErrores(err);
    return Object.keys(err).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensaje("");
    if (!validar()) return;

    try {
      await axios.post("http://localhost:8080/api/clientes/register", {
        nombres,
        apellidos,
        correo,
        contrasena,
        fechaNacimiento,
        telefono,
      });

      setMensaje("¡Registro exitoso! Redirigiendo al login...");
      setNombres("");
      setApellidos("");
      setCorreo("");
      setContrasena("");
      setFechaNacimiento("");
      setTelefono("");
      setTimeout(() => {
        navigate("/login");
      }, 500);
    } catch (error) {
      if (error.response && error.response.data) {
        setMensaje(
          error.response.data.join
            ? error.response.data.join("\n")
            : error.response.data
        );
      } else {
        setMensaje("Error al registrar usuario");
      }
    }
  };

  return (
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        fontFamily: "Montserrat, Arial, sans-serif"
      }}
    >
      {/* Lado Izquierdo: Formulario */}
      <div style={{
        flex: 1,
        background: "#fff",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        padding: "30px",
        minWidth: "320px"
      }}>
        {/* Logo */}
        <img src={logo} alt="Logo" style={{ width: 130, marginBottom: 18 }} />
        <h2 style={{
          fontWeight: "750",
          marginBottom: 40,
          fontSize: "2rem",
        }}>¡Regístrate!</h2>

        <form onSubmit={handleSubmit} style={{ width: "100%", maxWidth: 350, margin: "0 auto" }}>
          <div style={{
            display: "flex",
            gap: "12px",
            marginBottom: "12px"
          }}>
            <input
              type="text"
              placeholder="Nombres"
              className={`custom-input ${errores.nombres ? "input-error" : ""}`}
              value={nombres}
              onChange={e => setNombres(e.target.value)}
              style={{ flex: 1 }}
            />
            <input
              type="text"
              placeholder="Apellidos"
              className={`custom-input ${errores.apellidos ? "input-error" : ""}`}
              value={apellidos}
              onChange={e => setApellidos(e.target.value)}
              style={{ flex: 1 }}
            />
          </div>
          <div style={{ marginBottom: "12px" }}>
            <input
              type="password"
              placeholder="Contraseña"
              className={`custom-input ${errores.contrasena ? "input-error" : ""}`}
              value={contrasena}
              onChange={e => setContrasena(e.target.value)}
              minLength={8}
              style={{ width: "100%" }}
            />
          </div>
          <div style={{
            display: "flex",
            gap: "12px",
            marginBottom: "12px"
          }}>
            <input
              type="email"
              placeholder="Correo Electrónico"
              className={`custom-input ${errores.correo ? "input-error" : ""}`}
              value={correo}
              onChange={e => setCorreo(e.target.value)}
              style={{ flex: 1 }}
            />
            <input
              type="tel"
              placeholder="Teléfono"
              className={`custom-input ${errores.telefono ? "input-error" : ""}`}
              value={telefono}
              onChange={e => setTelefono(e.target.value)}
              maxLength={8}
              pattern="\d{8}"
              style={{ flex: 1 }}
            />
          </div>
          <div style={{ marginBottom: "24px" }}>
            <input
              type="date"
              placeholder="Fecha de Nacimiento"
              className={`custom-input ${errores.fechaNacimiento ? "input-error" : ""}`}
              value={fechaNacimiento}
              onChange={e => setFechaNacimiento(e.target.value)}
              style={{ width: "100%" }}
            />
          </div>
          <button type="submit"
            style={{
              width: "100%",
              background: "#A28C99",
              color: "#fff",
              border: "none",
              borderRadius: "18px",
              padding: "13px 0",
              fontWeight: "bold",
              fontSize: "1.15rem",
              marginBottom: "10px",
              boxShadow: "none",
              cursor: "pointer",
              transition: "background 0.2s"
            }}
          >
            Registrarse
          </button>
          <div style={{
            fontSize: 13,
            color: "#444",
            textAlign: "center",
            marginTop: "20px"
          }}>
            ¿Ya tienes una cuenta?{" "}
            <span
              style={{ fontWeight: "bold", cursor: "pointer", color: "#111" }}
              onClick={() => navigate("/login")}
            >
              Inicia Sesión
            </span>
          </div>
          {mensaje && (
            <div
              style={{
                marginTop: 18,
                padding: "10px 0",
                background: mensaje.includes("exitoso") ? "#c5e1c5" : "#f9c0c0",
                color: "#222",
                borderRadius: "10px",
                textAlign: "center",
                fontSize: "0.98rem"
              }}
            >
              {mensaje}
            </div>
          )}
        </form>
      </div>
      {/* Lado Derecho: Imagen de fondo */}
      <div style={{
        flex: 1,
        background: `url(${backgroundImg}) no-repeat center center/cover`,
        minHeight: "100vh",
        minWidth: 0
      }}>
        {/* Solo imagen de fondo */}
      </div>

      {/* Estilos para los inputs */}
      <style>
        {`
          .custom-input {
            width: 100%;
            background: #e6e6e6;
            border: none;
            border-radius: 18px;
            padding: 12px 16px;
            margin-bottom: 0px;
            font-size: 1rem;
            color: #444;
            outline: none;
            transition: box-shadow 0.15s;
            box-shadow: none;
          }
          .custom-input:focus {
            box-shadow: 0 0 0 2px #a28c99;
            background: #f4ecf7;
          }
          .input-error {
            background: #fbe4e4 !important;
          }
        `}
      </style>
    </div>
  );
}

export default Registro;
