
package org.univaq.swa.framework.model;

import java.util.List;

/**
 *
 * @author Giuseppe
 */
public class Gruppo {
    private int id;
    private String nome;
    private String descrizione;
    private List<Aula> auleAssociate;

    public Gruppo(int id, String nome, String descrizione, List<Aula> auleAssociate) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.auleAssociate = auleAssociate;
    }

    public Gruppo() {
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Aula> getAuleAssociate() {
        return auleAssociate;
    }

    public void setAuleAssociate(List<Aula> auleAssociate) {
        this.auleAssociate = auleAssociate;
    }
}
