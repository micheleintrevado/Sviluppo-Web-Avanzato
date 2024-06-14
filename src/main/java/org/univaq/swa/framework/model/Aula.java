
package org.univaq.swa.framework.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 *
 * @author Giuseppe
 */
public class Aula {
    private int id;
    private String nome;
    private String luogo;
    private String edificio;
    private String piano;
    private int capienza;
    private String emailResponsabile;
    private int preseElettriche;
    private int preseRete;
    private String note;
    private List<Attrezzatura> attrezzatureAssociate;
    private List<Gruppo> gruppiAssociati;

    public Aula() {
    }

    public Aula(int id, String nome, String luogo, String edificio, String piano, int capienza, String emailResponsabile, int preseElettriche, int preseRete, String note, List<Attrezzatura> attrezzatureAssociate, List<Gruppo> gruppiAssociati) {
        this.id = id;
        this.nome = nome;
        this.luogo = luogo;
        this.edificio = edificio;
        this.piano = piano;
        this.capienza = capienza;
        this.emailResponsabile = emailResponsabile;
        this.preseElettriche = preseElettriche;
        this.preseRete = preseRete;
        this.note = note;
        this.attrezzatureAssociate = attrezzatureAssociate;
        this.gruppiAssociati = gruppiAssociati;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getPiano() {
        return piano;
    }

    public void setPiano(String piano) {
        this.piano = piano;
    }

    public int getCapienza() {
        return capienza;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public String getEmailResponsabile() {
        return emailResponsabile;
    }

    public void setEmailResponsabile(String emailResponsabile) {
        this.emailResponsabile = emailResponsabile;
    }

    public int getPreseElettriche() {
        return preseElettriche;
    }

    public void setPreseElettriche(int preseElettriche) {
        this.preseElettriche = preseElettriche;
    }

    public int getPreseRete() {
        return preseRete;
    }

    public void setPreseRete(int preseRete) {
        this.preseRete = preseRete;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Attrezzatura> getAttrezzatureAssociate() {
        return attrezzatureAssociate;
    }

    public void setAttrezzatureAssociate(List<Attrezzatura> attrezzatureAssociate) {
        this.attrezzatureAssociate = attrezzatureAssociate;
    }

    public List<Gruppo> getGruppiAssociati() {
        return gruppiAssociati;
    }

    public void setGruppiAssociati(List<Gruppo> gruppiAssociati) {
        this.gruppiAssociati = gruppiAssociati;
    }
    
    
    
    
}
