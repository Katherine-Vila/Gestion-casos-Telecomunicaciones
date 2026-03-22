package com.empresa.gestion.entity;
import java.util.Date;

public class Bitacora {
    private Long id;
    private Caso caso;
    private Date fecha;
    private String descripcion;
    private Integer porcentajeAvance; // después del registro
    private Date createdAt;

    public Bitacora() {}

    public Bitacora(Caso caso, Date fecha, String descripcion, Integer porcentajeAvance) {
        this.caso = caso;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.porcentajeAvance = porcentajeAvance;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Caso getCaso() { return caso; }
    public void setCaso(Caso caso) { this.caso = caso; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Integer getPorcentajeAvance() { return porcentajeAvance; }
    public void setPorcentajeAvance(Integer porcentajeAvance) { this.porcentajeAvance = porcentajeAvance; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}