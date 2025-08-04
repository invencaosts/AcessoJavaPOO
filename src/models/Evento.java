package models;

import models.subModels.Participante;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Evento {
    private String idEvento, titulo, local, organizadorId;
    private LocalDateTime dataInicio, dataFim;
    private int limiteParticipantes;
    private List<Participante> participantes = new ArrayList<>();

    public Evento(String idEvento, String titulo, String local, String organizadorId, LocalDateTime dataInicio,
                  LocalDateTime dataFim, int limiteParticipantes, List<Participante> participantes) {
        this.idEvento = idEvento;
        this.titulo = titulo;
        this.local = local;
        this.organizadorId = organizadorId;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.limiteParticipantes = limiteParticipantes;
        this.participantes = participantes;
    }

    public boolean temConflito(Evento outro) {
        return this.local.equals(outro.local) &&
                !(this.dataFim.isBefore(outro.dataInicio) || this.dataInicio.isAfter(outro.dataFim));
    }

    public boolean adicionarParticipante(Participante p) throws Exception {
        if (participantes.size() >= limiteParticipantes) throw new Exception("Limite de participantes atingido");
        return participantes.add(p);
    }

    // Getters and Setters do IdEvento
    public String getIdEvento() {
        return idEvento;
    }
    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    // Getters and Setters do titulo
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Getters and Setters do local
    public String getLocal() {
        return local;
    }
    public void setLocal(String local) {
        this.local = local;
    }

    // Getters and Setters do organizadorId
    public String getOrganizadorId() {
        return organizadorId;
    }
    public void setOrganizadorId(String organizadorId) {
        this.organizadorId = organizadorId;
    }

    // Getters and Setters do dataInicio
    public LocalDateTime getDataInicio() {
        return dataInicio;
    }
    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    // Getters and Setters do dataFim
    public LocalDateTime getDataFim() {
        return dataFim;
    }
    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    // Getters and Setters do limiteParticipantes
    public int getLimiteParticipantes() {
        return limiteParticipantes;
    }
    public void setLimiteParticipantes(int limiteParticipantes) {
        this.limiteParticipantes = limiteParticipantes;
    }

    // Getters and Setters da lista de participantes
    public List<Participante> getParticipantes() {
        return participantes;
    }
    public void setParticipantes(List<Participante> participantes) {
        this.participantes = participantes;
    }

    public static Comparator<Evento> porData() {
        return Comparator.comparing(Evento::getDataInicio);
    }
}
