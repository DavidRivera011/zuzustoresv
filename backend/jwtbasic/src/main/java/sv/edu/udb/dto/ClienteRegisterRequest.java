package sv.edu.udb.dto;

import lombok.Data;

@Data
public class ClienteRegisterRequest {
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasena;
    private String fechaNacimiento;
    private String telefono;
    private String fechaRegistro;
    private String estado;
}
