import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

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

    // Validación de email
    if (!correo) {
      err.correo = "Correo requerido";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(correo)) {
      err.correo = "Correo no válido";
    }

    // Contraseña
    if (!contrasena) {
      err.contrasena = "Contraseña requerida";
    } else if (contrasena.length < 8) {
      err.contrasena = "La contraseña debe tener al menos 8 caracteres";
    }

    // Teléfono
    if (!telefono) {
      err.telefono = "Teléfono requerido";
    } else if (!/^\d{8}$/.test(telefono)) {
      err.telefono = "Debe tener 8 dígitos";
    }

    // Fecha nacimiento
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
      await axios.post("http://localhost:8080/api/auth/register", {
        nombre: nombres,
        apellido: apellidos,
        correo,
        contrasena,
        fechaNacimiento,
        telefono,
      });
      setMensaje("¡Registro exitoso! Ahora puedes iniciar sesión.");
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
    <div className="container mt-5" style={{ maxWidth: 430 }}>
      <div className="card shadow">
        <div className="card-body">
          <h2 className="mb-4 text-center">Registrar empleado</h2>
          <form onSubmit={handleSubmit} noValidate>
            <div className="mb-3">
              <label className="form-label">Nombres</label>
              <input
                type="text"
                className={`form-control ${errores.nombres && "is-invalid"}`}
                value={nombres}
                onChange={e => setNombres(e.target.value)}
              />
              {errores.nombres && <div className="invalid-feedback">{errores.nombres}</div>}
            </div>
            <div className="mb-3">
              <label className="form-label">Apellidos</label>
              <input
                type="text"
                className={`form-control ${errores.apellidos && "is-invalid"}`}
                value={apellidos}
                onChange={e => setApellidos(e.target.value)}
              />
              {errores.apellidos && <div className="invalid-feedback">{errores.apellidos}</div>}
            </div>
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
                minLength={8}
              />
              {errores.contrasena && <div className="invalid-feedback">{errores.contrasena}</div>}
            </div>
            <div className="mb-3">
              <label className="form-label">Fecha de nacimiento</label>
              <input
                type="date"
                className={`form-control ${errores.fechaNacimiento && "is-invalid"}`}
                value={fechaNacimiento}
                onChange={e => setFechaNacimiento(e.target.value)}
              />
              {errores.fechaNacimiento && <div className="invalid-feedback">{errores.fechaNacimiento}</div>}
            </div>
            <div className="mb-3">
              <label className="form-label">Teléfono</label>
              <input
                type="tel"
                className={`form-control ${errores.telefono && "is-invalid"}`}
                value={telefono}
                onChange={e => setTelefono(e.target.value)}
                maxLength={8}
                pattern="\d{8}"
              />
              {errores.telefono && <div className="invalid-feedback">{errores.telefono}</div>}
            </div>
            <button type="submit" className="btn btn-primary w-100 mb-2">
              Registrar
            </button>
            <button
              type="button"
              className="btn btn-link w-100"
              onClick={() => navigate("/login")}
            >
              ¿Ya tienes cuenta? Inicia sesión aquí
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

export default Registro;
