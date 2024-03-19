
package org.univaq.swa.framework.model;

import java.time.LocalDateTime;

/**
 *
 * @author Giuseppe
 */
public class Evento {

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    private int id;
    private String nome;
    private LocalDateTime orarioInizio;
    private LocalDateTime orarioFine;
    private String descrizione;
    private String nomeOrganizzatore;
    private String emailResponsabile;
    private Tipologia tipologia;

    public int getId() {
        return id;
    }

    public Evento(int id, String nome, LocalDateTime orarioInizio, LocalDateTime orarioFine, String descrizione, String nomeOrganizzatore, String emailResponsabile, Tipologia tipologia) {
        this.id = id;
        this.nome = nome;
        this.orarioInizio = orarioInizio;
        this.orarioFine = orarioFine;
        this.descrizione = descrizione;
        this.nomeOrganizzatore = nomeOrganizzatore;
        this.emailResponsabile = emailResponsabile;
        this.tipologia = tipologia;
    }

    public Evento() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getOrarioInizio() {
        return orarioInizio;
    }

    public void setOrarioInizio(LocalDateTime orarioInizio) {
        this.orarioInizio = orarioInizio;
    }

    public LocalDateTime getOrarioFine() {
        return orarioFine;
    }

    public void setOrarioFine(LocalDateTime orarioFine) {
        this.orarioFine = orarioFine;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNomeOrganizzatore() {
        return nomeOrganizzatore;
    }

    public void setNomeOrganizzatore(String nomeOrganizzatore) {
        this.nomeOrganizzatore = nomeOrganizzatore;
    }

    public String getEmailResponsabile() {
        return emailResponsabile;
    }

    public void setEmailResponsabile(String emailResponsabile) {
        this.emailResponsabile = emailResponsabile;
    }

    public Tipologia getTipologia() {
        return tipologia;
    }

    public void setTipologia(Tipologia tipologia) {
        this.tipologia = tipologia;
    }
    
    
    
}
