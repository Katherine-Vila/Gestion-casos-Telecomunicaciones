import java.util.Date;
public class Departamento {
    private Long id;
    private String codigo; // 3 letras, único
    private String nombre;
    private Date createdAt;

    // Constructores, getters y setters
    public Departamento() {}

    public Departamento(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
