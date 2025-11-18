package es.rtur.pruebas.recipes.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Receta entity.
 * Used to transfer recipe data between layers.
 */
public class RecetaDTO {

    private Integer id;

    @NotBlank(message = "Recipe name cannot be blank")
    private String nombre;

    @Positive(message = "Preparation time must be positive")
    private Integer tiempo;

    @Positive(message = "Number of servings must be positive")
    private Integer comensales;

    private String dificultad;
    private String preparacion;
    private String categoria;
    private Integer idAutor;
    private String nombreAutor; // For display purposes
    private LocalDateTime fCreacion;
    private LocalDateTime fModificacion;

    public RecetaDTO() {
    }

    public RecetaDTO(Integer id, String nombre, Integer tiempo, Integer comensales,
                    String dificultad, String preparacion, String categoria, Integer idAutor,
                    LocalDateTime fCreacion, LocalDateTime fModificacion) {
        this.id = id;
        this.nombre = nombre;
        this.tiempo = tiempo;
        this.comensales = comensales;
        this.dificultad = dificultad;
        this.preparacion = preparacion;
        this.categoria = categoria;
        this.idAutor = idAutor;
        this.fCreacion = fCreacion;
        this.fModificacion = fModificacion;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getTiempo() { return tiempo; }
    public void setTiempo(Integer tiempo) { this.tiempo = tiempo; }

    public Integer getComensales() { return comensales; }
    public void setComensales(Integer comensales) { this.comensales = comensales; }

    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }

    public String getPreparacion() { return preparacion; }
    public void setPreparacion(String preparacion) { this.preparacion = preparacion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Integer getIdAutor() { return idAutor; }
    public void setIdAutor(Integer idAutor) { this.idAutor = idAutor; }

    public String getNombreAutor() { return nombreAutor; }
    public void setNombreAutor(String nombreAutor) { this.nombreAutor = nombreAutor; }

    public LocalDateTime getFCreacion() { return fCreacion; }
    public void setFCreacion(LocalDateTime fCreacion) { this.fCreacion = fCreacion; }

    public LocalDateTime getFModificacion() { return fModificacion; }
    public void setFModificacion(LocalDateTime fModificacion) { this.fModificacion = fModificacion; }
}
