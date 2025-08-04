package models;

import java.util.regex.Pattern;

public abstract class PessoaAbstract {
    private int id;
    private String nome, email;

    public PessoaAbstract(int id, String nome, String email) {
        if (!validarEmail(email)) throw new IllegalArgumentException("Email inválido: " + email);
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    private boolean validarEmail(String email) {
        return Pattern.matches("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$", email);
    }

    // Getters and Setters do ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Getters and Setters do Nome
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getters and Setters do Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (!validarEmail(email)) throw new IllegalArgumentException("Email inválido: " + email);
        this.email = email;
    }

    // Método toString
    @Override
    public String toString() {
        return "PessoaAbstract{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
