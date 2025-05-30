import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

const logo = "/img/zuzulogo.png";
const backgroundImg = "/img/zuzufondoregister.jpg";

function Login() {
  const [correo, setCorreo] = useState("");
  const [contrasena, setContrasena] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [errores, setErrores] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwtDecode(token);
        if (decoded.rol === "admin") {
          navigate("/dashboard-admin");
        } else if (decoded.rol === "empleado") {
          navigate("/dashboard-empleado");
        }
      } catch (e) {}
    }
  }, [navigate]);

  const validar = () => {
    let err = {};
    if (!correo) {
      err.correo = "Correo requerido";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) {
      err.correo = "Correo no válido";
    }
    if (!contrasena) {
      err.contrasena = "Contraseña requerida";
    } else if (contrasena.length < 8) {
      err.contrasena = "Mínimo 8 caracteres";
    }
    setErrores(err);
    return Object.keys(err).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensaje("");
    if (!validar()) return;
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        correo,
        contrasena,
      });
      localStorage.setItem("token", response.data.token);
      setMensaje("¡Login exitoso!");
      const decoded = jwtDecode(response.data.token);
      setTimeout(() => {
        if (decoded.rol === "admin") {
          navigate("/dashboard-admin");
        } else {
          navigate("/dashboard-empleado");
        }
      }, 500);
    } catch (error) {
      if (error.response && error.response.data) {
        setMensaje(
          error.response.data.join
            ? error.response.data.join("\n")
            : error.response.data
        );
      } else {
        setMensaje("Error al iniciar sesión");
      }
    }
  };

  return (
    <div
      style={{
        display: "flex",
        minHeight: "100vh",
        fontFamily: "Montserrat, Arial, sans-serif",
      }}
    >
      {/* Lado izquierdo (formulario) */}
      <div
        style={{
          flex: 1,
          background: "#fff",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          padding: "30px",
          minWidth: "320px",
        }}
      >
        <img src={logo} alt="Logo" style={{ width: 130, marginBottom: 18 }} />
        <h2
          style={{
            fontWeight: "700",
            marginBottom: 5,
            fontSize: "2rem",
            textAlign: "center",
          }}
        >
          ¡Bienvenido a ZuzuStore!
        </h2>
        <div
          style={{
            fontWeight: "400",
            marginBottom: "20px",
            textAlign: "center",
            fontSize: "1.08rem",
            color: "#222",
          }}
        >
          Inicia Sesión
        </div>

        <form
          onSubmit={handleSubmit}
          style={{ width: "100%", maxWidth: 350, margin: "0 auto" }}
        >
          <div style={{ marginBottom: "15px" }}>
            <input
              type="email"
              placeholder="Usuario"
              className={`custom-input ${errores.correo ? "input-error" : ""}`}
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              style={{ width: "100%" }}
            />
          </div>
          <div style={{ marginBottom: "22px" }}>
            <input
              type="password"
              placeholder="Contraseña"
              className={`custom-input ${errores.contrasena ? "input-error" : ""}`}
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              style={{ width: "100%" }}
            />
          </div>
          <button
            type="submit"
            style={{
              width: "100%",
              background: "#A28C99",
              color: "#fff",
              border: "none",
              borderRadius: "18px",
              padding: "13px 0",
              fontWeight: "bold",
              fontSize: "1.1rem",
              marginBottom: "25px",
              cursor: "pointer",
              transition: "background 0.2s",
            }}
          >
            Login
          </button>
          <div
            style={{
              fontSize: 13,
              color: "#444",
              textAlign: "left",
              marginTop: "45px",
              marginLeft: "6px",
              marginBottom: "0",
            }}
          >
            ¿No tienes cuenta?{" "}
            <span
              style={{ fontWeight: "bold", cursor: "pointer", color: "#111" }}
              onClick={() => navigate("/registro")}
            >
              Regístrate ahora
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
                fontSize: "0.98rem",
              }}
            >
              {mensaje}
            </div>
          )}
        </form>
      </div>

      {/* Lado derecho (imagen de fondo) */}
      <div
        style={{
          flex: 1,
          background: `url(${backgroundImg}) no-repeat center center/cover`,
          minHeight: "100vh",
          minWidth: 0,
        }}
      ></div>

      {/* Custom inputs CSS */}
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

export default Login;
