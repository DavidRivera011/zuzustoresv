import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";

function Login() {
  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [mensaje, setMensaje] = useState('');
  const [errores, setErrores] = useState({});
  const navigate = useNavigate();

  // Redirección automática si ya hay sesión
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
      } catch (e) {
        // Si el token es inválido, no hacemos nada (puede mostrar login normalmente)
      }
    }
  }, [navigate]);

  // Validaciones
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
    setMensaje('');
    if (!validar()) return;
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', {
        correo,
        contrasena
      });
      localStorage.setItem('token', response.data.token);
      setMensaje('¡Login exitoso!');
      // Decodificar el token para ver el rol
      const decoded = jwtDecode(response.data.token);
      setTimeout(() => {
        if (decoded.rol === "admin") {
          navigate("/dashboard-admin");
        } else {
          navigate("/dashboard-empleado");
        }
      }, 1000); // 1 segundo de espera
    } catch (error) {
      if (error.response && error.response.data) {
        setMensaje(error.response.data.join ? error.response.data.join('\n') : error.response.data);
      } else {
        setMensaje('Error al iniciar sesión');
      }
    }
  };

  return (
    <div className="container mt-5" style={{ maxWidth: 430 }}>
      <div className="card shadow">
        <div className="card-body">
          <h2 className="mb-4 text-center">Iniciar sesión</h2>
          <form onSubmit={handleSubmit} noValidate>
            <div className="mb-3">
              <label className="form-label">Correo</label>
              <input
                type="email"
                className={`form-control ${errores.correo && "is-invalid"}`}
                value={correo}
                onChange={e => setCorreo(e.target.value)}
              />
              {errores.correo && <div className="invalid-feedback">{errores.correo}</div>}
            </div>
            <div className="mb-3">
              <label className="form-label">Contraseña</label>
              <input
                type="password"
                className={`form-control ${errores.contrasena && "is-invalid"}`}
                value={contrasena}
                onChange={e => setContrasena(e.target.value)}
              />
              {errores.contrasena && <div className="invalid-feedback">{errores.contrasena}</div>}
            </div>
            <button type="submit" className="btn btn-primary w-100 mb-2">Entrar</button>
            <button
              type="button"
              className="btn btn-link w-100"
              onClick={() => navigate("/registro")}
            >
              ¿No tienes cuenta? Regístrate aquí
            </button>
          </form>
          {mensaje && (
            <div
              className={`alert mt-3 ${mensaje.includes("exitoso") ? "alert-success" : "alert-danger"}`}
            >
              {mensaje}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Login;
