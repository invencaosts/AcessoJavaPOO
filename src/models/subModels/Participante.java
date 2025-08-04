package models.subModels;

import models.PessoaAbstract;

import java.util.HashSet;
import java.util.Set;

public class Participante extends PessoaAbstract {
    private Set<String> eventosInscritos = new HashSet<>();

    public Participante(int id, String nome, String email) {
        super(id, nome, email);
    }

    public boolean inscrever(String idEvento) {
        return eventosInscritos.add(idEvento);
    }

    public boolean cancelarInscricao(String idEvento) {
        return eventosInscritos.remove(idEvento);
    }

    public Set<String> getEventosInscritos() {
        return eventosInscritos;
    }
}
