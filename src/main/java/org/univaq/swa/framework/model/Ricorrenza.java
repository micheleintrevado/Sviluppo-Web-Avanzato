/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.univaq.swa.framework.model;

import java.time.LocalDateTime;


/**
 *
 * @author miche
 */
public class Ricorrenza {
    private int id;
    private TipoRicorrenza tipoRicorrenza;
    private LocalDateTime dataTermine;
    
    public Ricorrenza() {
    }

    public Ricorrenza(int id, TipoRicorrenza tipoRicorrenza, LocalDateTime dataTermine) {
        this.id = id;
        this.tipoRicorrenza = tipoRicorrenza;
        this.dataTermine = dataTermine;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TipoRicorrenza getTipoRicorrenza() {
        return tipoRicorrenza;
    }

    public void setTipoRicorrenza(TipoRicorrenza tipoRicorrenza) {
        this.tipoRicorrenza = tipoRicorrenza;
    }

    public LocalDateTime getDataTermine() {
        return dataTermine;
    }

    public void setDataTermine(LocalDateTime dataTermine) {
        this.dataTermine = dataTermine;
    }

    
    
    
}
