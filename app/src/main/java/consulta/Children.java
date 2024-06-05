package consulta;

/**
 * Created by henrique on 25/05/2016.
 */
public class Children {
    Long id;
    String texto;
    int modal;

    public Children(Long id, String texto, int modal) {
        super();
        this.id     = id;
        this.texto  = texto;
        this.modal  = modal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getModal() {
        return modal;
    }

    public void setModal(int modal) {
        this.modal = modal;
    }
}
