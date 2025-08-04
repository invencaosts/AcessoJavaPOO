package interfaces;

import models.Evento;

import java.time.LocalDate;
import java.util.List;

public interface Agendavel {
    boolean agendar(Evento evento);
    boolean cancelar(String idEvento);
    List<Evento> listarEventos(LocalDate data);
}
