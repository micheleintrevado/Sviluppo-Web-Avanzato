
package org.univaq.swa.framework.model;

import java.util.List;

/**
 *
 * @author Giuseppe
 */
public class Corso {
    private int id;
    private String nome;
    private List<Evento> eventiAssociati;

    public Corso(int id, String nome, List<Evento> eventiAssociati) {
        this.id = id;
        this.nome = nome;
        this.eventiAssociati = eventiAssociati;
    }

    public Corso() {
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

    public List<Evento> getEventiAssociati() {
        return eventiAssociati;
    }

    public void setEventiAssociati(List<Evento> eventiAssociati) {
        this.eventiAssociati = eventiAssociati;
    }
}
