import java.util.Date;
public class Caso {
    private Long id;
    private String codigo; // autogenerado: codigoDepartamento + YYMMDD + 3 dígitos aleatorios
    private String descripcionSolicitud;
    private Date fechaSolicitud;
    private EstadoCaso estado;
    private Integer porcentajeAvance; // 0-100
    private Date fechaLimite;          // asignada por el jefe de desarrollo
    private String analisisDescripcion; // comentarios del jefe de desarrollo al abrir el caso
    private String argumentoRechazo;    // cuando el jefe de desarrollo rechaza la solicitud
    private String observacionesRechazo; // cuando el probador rechaza la corrección

    private Departamento departamento;   // área funcional solicitante
    private Usuario solicitante;         // jefe de área que solicitó el caso
    private Usuario jefeDesarrollo;      // jefe de desarrollo asignado
    private Usuario programador;         // programador asignado
    private Usuario probador;            // empleado que probará el caso

    private Date createdAt;

    // Constructores, getters y setters
    public Caso() {}

    public Caso(String descripcionSolicitud, Date fechaSolicitud, Departamento departamento, Usuario solicitante) {
        this.descripcionSolicitud = descripcionSolicitud;
        this.fechaSolicitud = fechaSolicitud;
        this.departamento = departamento;
        this.solicitante = solicitante;
        this.estado = EstadoCaso.EN_ESPERA;
        this.porcentajeAvance = 0;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcionSolicitud() { return descripcionSolicitud; }
    public void setDescripcionSolicitud(String descripcionSolicitud) { this.descripcionSolicitud = descripcionSolicitud; }

    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public EstadoCaso getEstado() { return estado; }
    public void setEstado(EstadoCaso estado) { this.estado = estado; }

    public Integer getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(Integer porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    public Date getFechaLimite() { return fechaLimite; }
    public void setFechaLimite(Date fechaLimite) { this.fechaLimite = fechaLimite; }

    public String getAnalisisDescripcion() { return analisisDescripcion; }
    public void setAnalisisDescripcion(String analisisDescripcion) { this.analisisDescripcion = analisisDescripcion; }

    public String getArgumentoRechazo() { return argumentoRechazo; }
    public void setArgumentoRechazo(String argumentoRechazo) { this.argumentoRechazo = argumentoRechazo; }

    public String getObservacionesRechazo() { return observacionesRechazo; }
    public void setObservacionesRechazo(String observacionesRechazo) { this.observacionesRechazo = observacionesRechazo; }

    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; }

    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }

    public Usuario getJefeDesarrollo() { return jefeDesarrollo; }
    public void setJefeDesarrollo(Usuario jefeDesarrollo) { this.jefeDesarrollo = jefeDesarrollo; }

    public Usuario getProgramador() { return programador; }
    public void setProgramador(Usuario programador) { this.programador = programador; }

    public Usuario getProbador() { return probador; }
    public void setProbador(Usuario probador) { this.probador = probador; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
