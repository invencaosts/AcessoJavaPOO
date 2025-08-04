package models;

import interfaces.Agendavel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Espaco implements Agendavel {
    private List<Evento> eventos = new ArrayList<>();

    @Override
    public boolean agendar(Evento novo) {
        for (Evento e : eventos) {
            if (e.temConflito(novo)) {
                return false;
            }
        }
        eventos.add(novo);
        return true;
    }

    @Override
    public boolean cancelar(String idEvento) {
        return eventos.removeIf(e -> e.getIdEvento().equals(idEvento));
    }

    @Override
    public List<Evento> listarEventos(LocalDate data) {
        return eventos.stream()
                .filter(e -> e.getDataInicio().toLocalDate().equals(data))
                .sorted(Evento.porData())
                .collect(Collectors.toList());
    }

    public List<Evento> getTodosEventos() {
        return eventos;
    }
}
