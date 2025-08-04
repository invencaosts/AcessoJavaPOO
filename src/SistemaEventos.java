import models.*;
import models.exceptions.*;
import models.subModels.Organizador;
import models.subModels.Participante;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class SistemaEventos {
    private Map<String, Organizador> organizadores = new HashMap<>();
    private Map<String, Participante> participantes = new HashMap<>();
    private Espaco espaco = new Espaco(); // Implementa Agendavel

    private static final String ARQUIVO_EVENTOS = "eventos.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ===================== PARTICIPANTES =====================
    public void adicionarParticipante(Participante p) {
        participantes.put(String.valueOf(p.getId()), p);
    }

    public Participante getParticipante(String id) {
        return participantes.get(id);
    }

    // ===================== ORGANIZADORES =====================
    public void adicionarOrganizador(Organizador o) {
        organizadores.put(String.valueOf(o.getId()), o);
    }

    public Organizador getOrganizador(String id) {
        return organizadores.get(id);
    }

    // ===================== AGENDA DE EVENTOS =====================
    public boolean agendarEvento(Evento evento) throws EventoConflitanteException {
        boolean sucesso = espaco.agendar(evento);
        if (!sucesso) {
            throw new EventoConflitanteException("Conflito com outro evento no mesmo local e horário.");
        }

        Organizador org = organizadores.get(evento.getOrganizadorId());
        if (org != null) {
            org.adicionarEvento(evento.getIdEvento());
        }

        return true;
    }

    public boolean cancelarEvento(String idEvento, String idOrganizador) {
        Organizador org = organizadores.get(idOrganizador);
        if (org != null && org.getEventosCriados().contains(idEvento)) {
            org.getEventosCriados().remove(idEvento);
            return espaco.cancelar(idEvento);
        }
        return false;
    }

    // ===================== INSCRIÇÃO =====================
    public boolean inscreverEmEvento(String idEvento, String idParticipante) throws LimiteParticipantesException {
        Participante p = participantes.get(idParticipante);
        Optional<Evento> eventoOpt = espaco.getTodosEventos()
                .stream()
                .filter(e -> e.getIdEvento().equals(idEvento))
                .findFirst();

        if (eventoOpt.isEmpty()) return false;

        Evento evento = eventoOpt.get();
        if (evento.getParticipantes().size() >= evento.getLimiteParticipantes()) {
            throw new LimiteParticipantesException("Evento lotado.");
        }

        boolean novo = p.inscrever(idEvento);
        if (novo) evento.getParticipantes().add(p);
        return novo;
    }

    public boolean cancelarInscricao(String idEvento, String idParticipante) {
        Participante p = participantes.get(idParticipante);
        Optional<Evento> eventoOpt = espaco.getTodosEventos()
                .stream()
                .filter(e -> e.getIdEvento().equals(idEvento))
                .findFirst();

        if (eventoOpt.isEmpty()) return false;

        Evento evento = eventoOpt.get();
        if (p.cancelarInscricao(idEvento)) {
            return evento.getParticipantes().removeIf(part -> String.valueOf(part.getId()).equals(idParticipante));
        }

        return false;
    }

    // ===================== RELATÓRIOS =====================
    public void gerarRelatorioEventosOrdenados() {
        System.out.println("\n📅 Relatório de Eventos por Data:");
        espaco.getTodosEventos()
                .stream()
                .sorted(Evento.porData())
                .forEach(e -> System.out.println(e.getIdEvento() + " - " + e.getTitulo() + " em " + e.getDataInicio()));
    }

    public void gerarRelatorioParticipacao() {
        System.out.println("\n👥 Relatório de Participação:");
        participantes.values()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getEventosInscritos().size(), a.getEventosInscritos().size()))
                .forEach(p -> System.out.println(p.getNome() + " participou de " + p.getEventosInscritos().size() + " evento(s)"));
    }

    // ===================== LISTAGEM =====================
    public void listarEventosDoDia(LocalDate data) {
        System.out.println("\n📆 Eventos em " + data + ":");
        List<Evento> eventos = espaco.listarEventos(data);
        if (eventos.isEmpty()) {
            System.out.println("Nenhum evento encontrado.");
        } else {
            eventos.forEach(e -> System.out.println(e.getTitulo() + " - " + e.getDataInicio().toLocalTime()));
        }
    }

    public List<Evento> getTodosEventos() {
        return espaco.getTodosEventos();
    }

    // ===================== PERSISTÊNCIA =====================
    public void salvarEventos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_EVENTOS))) {
            for (Evento e : espaco.getTodosEventos()) {
                // Salva IDs dos participantes separados por vírgula
                String participantesIds = e.getParticipantes().stream()
                        .map(p -> String.valueOf(p.getId()))
                        .collect(Collectors.joining(","));
                writer.write(e.getIdEvento() + ";" +
                        e.getTitulo() + ";" +
                        e.getLocal() + ";" +
                        e.getOrganizadorId() + ";" +
                        e.getDataInicio().format(formatter) + ";" +
                        e.getDataFim().format(formatter) + ";" +
                        e.getLimiteParticipantes() + ";" +
                        participantesIds);
                writer.newLine();
            }
        } catch (IOException ex) {
            System.out.println("Erro ao salvar eventos: " + ex.getMessage());
        }
    }

    public void carregarEventos() {
        File file = new File(ARQUIVO_EVENTOS);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length < 7) continue;
                List<Participante> listaParticipantes = new ArrayList<>();
                if (partes.length >= 8 && !partes[7].isEmpty()) {
                    String[] ids = partes[7].split(",");
                    for (String id : ids) {
                        Participante p = participantes.get(id);
                        if (p != null) {
                            listaParticipantes.add(p);
                            // Garante que o participante também tenha o evento em seus inscritos
                            p.inscrever(partes[0]);
                        }
                    }
                }
                Evento e = new Evento(
                        partes[0],
                        partes[1],
                        partes[2],
                        partes[3],
                        LocalDateTime.parse(partes[4], formatter),
                        LocalDateTime.parse(partes[5], formatter),
                        Integer.parseInt(partes[6]),
                        listaParticipantes
                );
                espaco.getTodosEventos().add(e);
            }
        } catch (IOException ex) {
            System.out.println("Erro ao carregar eventos: " + ex.getMessage());
        }
    }
}