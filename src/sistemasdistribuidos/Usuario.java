package sistemasdistribuidos;

import java.io.Serializable;

public class Usuario implements Serializable {
    private Long matricula;
    private String senha;
    private String nome;

    public Usuario(Long matricula, String senha) {
        this.matricula = matricula;
        this.senha = senha;
    }

    public Usuario(Long matricula, String senha, String nome) {
        this.matricula = matricula;
        this.senha = senha;
        this.nome = nome;
    }

    public Long getMatricula() {
        return matricula;
    }

    public void setMatricula(Long matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
