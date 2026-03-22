package com.empresa.gestion.entity;
import java.util.Date;

public class ArchivoAdjunto {
    public enum TipoArchivo {
        SOLICITUD,
        ANALISIS
    }

    private Long id;
    private Caso caso;
    private String nombreArchivo;
    private String ruta;
    private TipoArchivo tipo;
    private Usuario usuarioSubio;
    private Date fechaSubida;

    public ArchivoAdjunto() {}

    public ArchivoAdjunto(Caso caso, String nombreArchivo, String ruta, TipoArchivo tipo, Usuario usuarioSubio) {
        this.caso = caso;
        this.nombreArchivo = nombreArchivo;
        this.ruta = ruta;
        this.tipo = tipo;
        this.usuarioSubio = usuarioSubio;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Caso getCaso() { return caso; }
    public void setCaso(Caso caso) { this.caso = caso; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRuta() { return ruta; }
    public void setRuta(String ruta) { this.ruta = ruta; }

    public TipoArchivo getTipo() { return tipo; }
    public void setTipo(TipoArchivo tipo) { this.tipo = tipo; }

    public Usuario getUsuarioSubio() { return usuarioSubio; }
    public void setUsuarioSubio(Usuario usuarioSubio) { this.usuarioSubio = usuarioSubio; }

    public Date getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(Date fechaSubida) { this.fechaSubida = fechaSubida; }
}
