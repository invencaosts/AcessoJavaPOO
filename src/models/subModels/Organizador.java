package models.subModels;

import models.PessoaAbstract;

import java.util.ArrayList;
import java.util.List;

public class Organizador extends PessoaAbstract {
    private List<String> eventosCriados = new ArrayList<>();

    public Organizador(int id, String nome, String email) {
        super(id, nome, email);
    }

    public void adicionarEvento(String idEvento) {
        eventosCriados.add(idEvento);
    }

    public List<String> getEventosCriados() {
        return eventosCriados;
    }
}
