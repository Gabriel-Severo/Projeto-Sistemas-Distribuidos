package sistemasdistribuidos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Questao implements Serializable {
    private String descricao;
    private List<String> alternativas;
    private int alternativaCorreta;

    public Questao() {
        alternativas = new ArrayList<>();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getAlternativas() {
        return alternativas;
    }

    public void addAlternativa(String alternativa) {
        alternativas.add(alternativa);
    }

    public void setAlternativas(List<String> alternativas) {
        this.alternativas = alternativas;
    }

    public int getAlternativaCorreta() {
        return alternativaCorreta;
    }

    public void setAlternativaCorreta(int alternativaCorreta) {
        this.alternativaCorreta = alternativaCorreta;
    }
}

