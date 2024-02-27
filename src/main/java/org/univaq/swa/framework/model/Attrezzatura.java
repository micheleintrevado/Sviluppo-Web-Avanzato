
package org.univaq.swa.framework.model;

import java.util.List;

/**
 *
 * @author Giuseppe
 */
public class Attrezzatura {
    private int id;
    private String tipo;
    private List<Aula> auleAssociate;

    public Attrezzatura(int id, String tipo, List<Aula> auleAssociate) {
        this.id = id;
        this.tipo = tipo;
        this.auleAssociate = auleAssociate;
    }

    public Attrezzatura() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Aula> getAuleAssociate() {
        return auleAssociate;
    }

    public void setAuleAssociate(List<Aula> auleAssociate) {
        this.auleAssociate = auleAssociate;
    }
}
