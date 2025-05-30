import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Registro from "./pages/Registro";
import DashboardAdmin from "./pages/DashboardAdmin";
import DashboardEmpleado from "./pages/DashboardEmpleado";
import PrivateRoute from "./components/PrivateRoute";
import SemiPrivateRoute from "./components/SemiPrivateRoute";
import EmpleadosPage from "./pages/EmpleadosPage";
import ProductosPage from "./pages/ProductosPage";
import VentasPage from "./pages/VentasPage";
import RegistroPage from "./pages/RegistroPage";
import MovimientosPage from "./pages/MovimientosPage";
import MisMovimientosPage from "./pages/MisMovimientosPage";
import VentasEmpleadoPage from "./pages/VentasEmpleadoPage";
import ProductosStockPage from "./pages/ProductosStockPage";
import ReportesBasicosEmpleado from "./pages/ReportesBasicosEmpleado";
import CatalogoPage from "./pages/CatalogoPage";
import ClientesPage from "./pages/ClientesPage";
import OrdenEmpleadoPage from "./pages/OrdenEmpleadoPage";
import OrdenAdminPage from "./pages/OrdenAdminPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/catalogopage" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />

        {/* DASHBOARDS */}
        <Route
          path="/dashboard-admin"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <DashboardAdmin />
            </PrivateRoute>
          }
        />
        <Route
          path="/dashboard-empleado"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <DashboardEmpleado />
            </PrivateRoute>
          }
        />

        {/* SOLO ADMIN */}
        <Route
          path="/empleados"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <EmpleadosPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/productos"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <ProductosPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/ventas"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <VentasPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/registro-inventario"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <RegistroPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/movimientos"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <MovimientosPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/clientes"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <ClientesPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/ordenadminpage"
          element={
            <PrivateRoute allowedRoles={["admin"]}>
              <OrdenAdminPage />
            </PrivateRoute>
          }
        />

        {/* SOLO EMPLEADO */}
        <Route
          path="/ventas-empleado"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <VentasEmpleadoPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/mis-movimientos"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <MisMovimientosPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/productos-stock"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <ProductosStockPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/reportes-basicos"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <ReportesBasicosEmpleado />
            </PrivateRoute>
          }
        />
        <Route
          path="/orden-empleados"
          element={
            <PrivateRoute allowedRoles={["empleado"]}>
              <OrdenEmpleadoPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/catalogopage"
          element={
            <SemiPrivateRoute>
              <CatalogoPage />
            </SemiPrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
