package sistemasdistribuidos;

import java.io.Serializable;

public class Usuario implements Serializable {
    private Long matricula;
    private String senha;

    public Usuario(Long matricula, String senha) {
        this.matricula = matricula;
        this.senha = senha;
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
}
