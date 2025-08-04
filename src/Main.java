import models.*;
import models.exceptions.*;
import models.subModels.Organizador;
import models.subModels.Participante;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static final SistemaEventos sistema = new SistemaEventos();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void main(String[] args) {
        carregarDadosFicticios();
        sistema.carregarEventos();

        System.out.println("🔐 Login - Digite seu ID:");
        String id = scanner.nextLine();

        if (sistema.getOrganizador(id) != null) {
            menuOrganizador(sistema.getOrganizador(id));
        } else if (sistema.getParticipante(id) != null) {
            menuParticipante(sistema.getParticipante(id));
        } else {
            System.out.println("ID não encontrado.");
        }
    }

    // ================== MENU ORGANIZADOR ===================
    private static void menuOrganizador(Organizador org) {
        int opcao;
        do {
            System.out.println("\n🎓 Menu Organizador - " + org.getNome());
            System.out.println("1. Agendar novo evento");
            System.out.println("2. Cancelar evento");
            System.out.println("3. Listar eventos do dia");
            System.out.println("4. Relatório de eventos");
            System.out.println("5. Relatório de participação");
            System.out.println("0. Sair");
            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> agendarEvento(org);
                case 2 -> cancelarEvento(org);
                case 3 -> listarEventosDoDia();
                case 4 -> sistema.gerarRelatorioEventosOrdenados();
                case 5 -> sistema.gerarRelatorioParticipacao();
            }
        } while (opcao != 0);
    }

    // ================== MENU PARTICIPANTE ===================
    private static void menuParticipante(Participante part) {
        int opcao;
        do {
            System.out.println("\n🙋 Menu Participante - " + part.getNome());
            System.out.println("1. Listar eventos do dia");
            System.out.println("2. Inscrever-se em evento");
            System.out.println("3. Cancelar inscrição");
            System.out.println("4. Meus eventos");
            System.out.println("0. Sair");
            opcao = Integer.parseInt(scanner.nextLine());

            switch (opcao) {
                case 1 -> listarEventosDoDia();
                case 2 -> inscreverEvento(part);
                case 3 -> cancelarInscricao(part);
                case 4 -> listarEventosDoParticipante(part);
            }
        } while (opcao != 0);
    }

    // ================== FUNCIONALIDADES ===================

    private static void agendarEvento(Organizador org) {
        try {
            System.out.println("📌 Título do evento:");
            String titulo = scanner.nextLine();

            System.out.println("📍 Local:");
            String local = scanner.nextLine();

            System.out.println("📅 Data e hora de início (dd/MM/yyyy HH:mm):");
            LocalDateTime ini = LocalDateTime.parse(scanner.nextLine(), formatter);

            System.out.println("📅 Data e hora de término (dd/MM/yyyy HH:mm):");
            LocalDateTime fim = LocalDateTime.parse(scanner.nextLine(), formatter);

            System.out.println("👥 Limite de participantes:");
            int limite = Integer.parseInt(scanner.nextLine());

            String idEvento = UUID.randomUUID().toString().substring(0, 6);
            Evento e = new Evento(
                    idEvento,
                    titulo,
                    local,
                    String.valueOf(org.getId()),
                    ini,
                    fim,
                    limite,
                    new ArrayList<>()
            );

            sistema.agendarEvento(e);
            sistema.salvarEventos();
            System.out.println("✅ Evento agendado com sucesso! ID: " + idEvento);
        } catch (EventoConflitanteException ex) {
            System.out.println("❌ " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Erro ao agendar: " + ex.getMessage());
        }
    }

    private static void cancelarEvento(Organizador org) {
        System.out.println("Digite o ID do evento a cancelar:");
        String id = scanner.nextLine();
        if (sistema.cancelarEvento(id, String.valueOf(org.getId()))) {
            sistema.salvarEventos();
            System.out.println("✅ Evento cancelado.");
        } else {
            System.out.println("❌ Evento não encontrado ou não pertence a você.");
        }
    }

    private static void inscreverEvento(Participante p) {
        System.out.println("Digite o ID do evento para inscrição:");
        String id = scanner.nextLine();
        try {
            if (sistema.inscreverEmEvento(id, String.valueOf(p.getId()))) {
                sistema.salvarEventos();
                System.out.println("✅ Inscrição realizada com sucesso!");
            } else {
                System.out.println("❌ Já está inscrito ou evento não existe.");
            }
        } catch (LimiteParticipantesException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void cancelarInscricao(Participante p) {
        System.out.println("Digite o ID do evento para cancelar inscrição:");
        String id = scanner.nextLine();
        if (sistema.cancelarInscricao(id, String.valueOf(p.getId()))) {
            sistema.salvarEventos();
            System.out.println("✅ Inscrição cancelada.");
        } else {
            System.out.println("❌ Não está inscrito nesse evento.");
        }
    }

    private static void listarEventosDoDia() {
        System.out.println("Digite a data (dd/MM/yyyy):");
        LocalDate data = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        sistema.listarEventosDoDia(data);
    }

    private static void listarEventosDoParticipante(Participante p) {
        System.out.println("\n📄 Meus Eventos:");
        for (String id : p.getEventosInscritos()) {
            sistema.getTodosEventos()
                    .stream()
                    .filter(e -> e.getIdEvento().equals(id))
                    .forEach(e -> System.out.println(e.getTitulo() + " - " + e.getDataInicio()));
        }
    }

    // ================== DADOS FICTÍCIOS ===================
    private static void carregarDadosFicticios() {
        sistema.adicionarOrganizador(new Organizador(1, "João", "joao@ifsergipe.edu.br"));
        sistema.adicionarParticipante(new Participante(2, "Maria", "maria@gmail.com"));
        sistema.adicionarParticipante(new Participante(3, "Carlos", "carlos@gmail.com"));
    }
}