package com.facturacion.facturacion.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReporteProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String convenio;

    @Column
    private String empresa;

    @Column
    private String nombre;

    @Column
    private String grupo;

    @Column
    private String descripcion;

    @Column
    private int copago;

    @Column
    private String facturado;

    @Column
    private String referencia;


}
