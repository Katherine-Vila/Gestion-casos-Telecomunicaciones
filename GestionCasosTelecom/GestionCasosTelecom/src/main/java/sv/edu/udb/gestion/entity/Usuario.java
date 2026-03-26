package sv.edu.udb.gestion.entity;
import java.util.Date;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
    private Departamento departamento; // departamento al que pertenece (para JEFE_AREA, EMPLEADO)
    private Usuario jefeDesarrollo;    // solo para PROGRAMADOR
    private Date createdAt;

    public Usuario() {}

    public Usuario(String nombre, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Usuario getJefeDesarrollo() { return jefeDesarrollo; }
    public void setJefeDesarrollo(Usuario jefeDesarrollo) { this.jefeDesarrollo = jefeDesarrollo; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
