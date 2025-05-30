package sv.edu.udb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.edu.udb.dto.OrdenRequest;
import sv.edu.udb.dto.OrdenResponse;
import sv.edu.udb.model.*;
import sv.edu.udb.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public Orden crearOrden(OrdenRequest ordenRequest) {
        Cliente cliente = clienteRepository.findById(ordenRequest.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Orden orden = Orden.builder()
                .cliente(cliente)
                .telefono(ordenRequest.getTelefono() != null ? ordenRequest.getTelefono() : cliente.getTelefono())
                .correo(ordenRequest.getCorreo() != null ? ordenRequest.getCorreo() : cliente.getCorreo())
                .direccionEntrega(ordenRequest.getDireccionEntrega())
                .estado(Orden.EstadoOrden.pendiente)
                .build();

        List<DetalleOrden> detalles = new ArrayList<>();
        double total = 0.0;

        for (OrdenRequest.ItemCarrito item : ordenRequest.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            double precio = producto.getPrecio().doubleValue();
            int cantidad = item.getCantidad();
            double subtotal = precio * cantidad;

            DetalleOrden detalle = DetalleOrden.builder()
                    .orden(orden)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(precio)
                    .subtotal(subtotal)
                    .build();

            detalles.add(detalle);
            total += subtotal;
        }

        orden.setDetalles(detalles);
        return ordenRepository.save(orden);
    }

    // Listar todas las órdenes ya como respuesta DTO
    public List<OrdenResponse> listarOrdenes() {
        List<Orden> ordenes = ordenRepository.findAll();
        return ordenes.stream().map(this::mapOrdenToResponse).toList();
    }

    // Listar órdenes por cliente, igual en DTO
    public List<OrdenResponse> listarOrdenesPorCliente(Long clienteId) {
        List<Orden> ordenes = ordenRepository.findByClienteId(clienteId);
        return ordenes.stream().map(this::mapOrdenToResponse).toList();
    }

    // Conversor de entidad a DTO PLANO (sin recursividad infinita)
    public OrdenResponse mapOrdenToResponse(Orden orden) {
        OrdenResponse dto = new OrdenResponse();
        dto.setId(orden.getId());
        dto.setCliente(orden.getCliente().getNombres() + " " + orden.getCliente().getApellidos());
        dto.setTelefono(orden.getCliente().getTelefono());
        dto.setCorreo(orden.getCliente().getCorreo());
        dto.setDireccionEntrega(orden.getDireccionEntrega());
        dto.setFecha(orden.getFecha().toString());
        dto.setEstado(orden.getEstado().name());

        List<OrdenResponse.Detalle> detallesDTO = orden.getDetalles().stream().map(det -> {
            OrdenResponse.Detalle d = new OrdenResponse.Detalle();
            // Si agregaste estos campos al Detalle, los setea, si no, solo deja los que existan en tu DTO:
            // d.setProductoId(det.getProducto().getId());
            d.setProducto(det.getProducto().getNombre());
            // d.setImagenUrl(det.getProducto().getImagenUrl());
            d.setCantidad(det.getCantidad());
            d.setPrecioUnitario(det.getPrecioUnitario());
            d.setSubtotal(det.getSubtotal());
            return d;
        }).toList();
        dto.setDetalles(detallesDTO);

        return dto;
    }
    
    @Transactional
    public void editarEstadoOrden(Long id, String nuevoEstado) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        // Valida que solo se pueda cambiar si está pendiente
        if (orden.getEstado() != Orden.EstadoOrden.pendiente) {
            throw new IllegalStateException("Solo puedes cambiar el estado si está pendiente");
        }
        // Actualiza
        orden.setEstado(Orden.EstadoOrden.valueOf(nuevoEstado));
        ordenRepository.save(orden);
    }

}
