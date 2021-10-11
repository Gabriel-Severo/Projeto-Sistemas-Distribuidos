package sistemasdistribuidos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Servidor {
    public static List<Questao> buscarQuestoes() {
        List<Questao> questoes = new ArrayList<>();
        
        Questao questao1 = new Questao();
        questao1.setDescricao("Questão 1 - Qual os principais objetivos de sistemas distribuidos?");
        questao1.addAlternativa("1) Transparência e Recursos compartilhados");
        questao1.addAlternativa("2) Execução sem falhas e Escalabidade");
        questao1.addAlternativa("3) Transparência e Segurança");
        questao1.addAlternativa("4) Escalabidade e Portabilidade");
        questao1.setAlternativaCorreta(1);
        questoes.add(questao1);

        Questao questao2 = new Questao();
        questao2.setDescricao("Questão 2 - Qual protocolo é utilizado na comunicação entre processos em sistemas distribuidos?");
        questao2.addAlternativa("1) RPC");
        questao2.addAlternativa("2) RMI");
        questao2.addAlternativa("3) Soquetes");
        questao2.addAlternativa("4) Filha de mensagem");
        questao2.setAlternativaCorreta(3);
        questoes.add(questao2);

        Questao questao3 = new Questao();
        questao3.setDescricao("Questão 3 - Qual a principal diferença entre TCP e UDP?");
        questao3.addAlternativa("1) TCP e UDP são procolos idênticos porém TCP é mais novo e por isso mais utilizado");
        questao3.addAlternativa("2) TCP depende de uma conexão mais rápida e traz mais estabilidade para a conexão, já UDP tem mais perdas de pacote");
        questao3.addAlternativa("3) TCP o cliente não espera a resposta do servidor se o pacote foi recebido com sucesso, já no UDP isso ocorre");
        questao3.addAlternativa("4) TCP o cliente espera a resposta do servidor se o pacote foi recebido com sucesso, já no UDP isso não ocorre");
        questao3.setAlternativaCorreta(4);
        questoes.add(questao3);

        Questao questao4 = new Questao();
        questao4.setDescricao("Questão 4 - Enviar um objeto usando soquete é preciso que a classe implemente um interface, que nome é dado a essa interface?");
        questao4.addAlternativa("1) HttpRequest");
        questao4.addAlternativa("2) Serializable");
        questao4.addAlternativa("3) CompileObject");
        questao4.addAlternativa("4) ObjectToString");
        questao4.setAlternativaCorreta(2);
        questoes.add(questao4);

        Questao questao5 = new Questao();
        questao5.setDescricao("Questão 5 - Qual a diferença entre RPC e RMI na invocação remota.");
        questao5.addAlternativa("1) RPC consegue se comunicar com vários nós na rede, mesmo que aquela rede tenha um grande volume de requisições no momento consequentemente dando perda de pacotes, já o RMI evita isso");
        questao5.addAlternativa("2) RPC permite que um processo chame um outro processo em um nó remoto como se fosse em um ambiente local, já o RMI desempenha o mesmo papel mas voltado para orientação a objetos");
        questao5.addAlternativa("3) RPC e RMI são idênticos, entretanto o RMI é mais novo e por isso mais usado atualmente");
        questao5.addAlternativa("4) RPC foi desenvolvido no começo conseguir enviar arquivos de texto com bastante segurança e alta velocidade e RMI conseguiu integrar o mesmo para os de mais processos na rede");
        questao5.setAlternativaCorreta(2);
        questoes.add(questao5);

        return questoes;
    }

    public static HashMap<Long, Usuario> buscarUsuarios() {
        HashMap<Long, Usuario> usuarios = new HashMap<>();
        Usuario usuario1 = new Usuario(1L, "123", "Maria");
        Usuario usuario2 = new Usuario(2L, "1234", "João");

        usuarios.put(usuario1.getMatricula(), usuario1);
        usuarios.put(usuario2.getMatricula(), usuario2);
        return usuarios;
    }

    public static void main(String[] args) {
        Boolean usuarioAutenticado = false;
        HashMap<Long, Usuario> usuarios = buscarUsuarios();
        List<Questao> questoes = buscarQuestoes();

        try {
            ServerSocket servidor = new ServerSocket(8888);
            System.out.println("Servidor iniciado na porta 8888");

            while(true) {
                Socket socket = servidor.accept();
                System.out.println("Estudante conectado: " + socket.getInetAddress().getHostAddress());
                ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
                saida.flush();
    
                saida.writeBoolean(usuarioAutenticado);
                saida.close();
                socket.close();
                
                while(!usuarioAutenticado) {
                    socket = servidor.accept();
                    ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                    Usuario usuario = (Usuario) entrada.readObject();
                    System.out.println("Estudante com matricula " + usuario.getMatricula() + " tentando conectar");
                    if(Objects.nonNull(usuario)) {
                        Usuario usuarioInformacoes = usuarios.get(usuario.getMatricula());
                        if(Objects.nonNull(usuarioInformacoes) && usuarioInformacoes.getSenha().equals(usuario.getSenha())) {
                            System.out.println("Estudante " + usuarioInformacoes.getNome() + " conectado");
                            usuarioAutenticado = true;
                        }else{
                            System.out.println("Falha ao tentar conectar");
                        }
                    }else{
                        System.out.println("Falha ao tentar conectar");
                    }
                    entrada.close();
                    socket.close();
    
                    socket = servidor.accept();
                    saida = new ObjectOutputStream(socket.getOutputStream());
                    saida.writeBoolean(usuarioAutenticado);
    
                    saida.close();
                    socket.close();
                }

                int quantidadeAcertos = 0;

                for(int i = 0; i < questoes.size(); i++) {
                    socket = servidor.accept();
                    saida = new ObjectOutputStream(socket.getOutputStream());
                    Questao questao = new Questao();
                    
                    questao.setDescricao(questoes.get(i).getDescricao());
                    questao.setAlternativas(questoes.get(i).getAlternativas());

                    saida.writeObject(questao);

                    socket.close();
                    saida.close();

                    socket = servidor.accept();
                    ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                    int resposta = entrada.readInt();

                    if(resposta == questoes.get(i).getAlternativaCorreta())
                        quantidadeAcertos++;

                    socket = servidor.accept();
                    saida = new ObjectOutputStream(socket.getOutputStream());
                    Boolean questionarioFinalizado = (i >= questoes.size()-1);
                    saida.writeBoolean(questionarioFinalizado);
                    
                    saida.close();
                    socket.close();
                }

                socket = servidor.accept();
                saida = new ObjectOutputStream(socket.getOutputStream());
                saida.writeInt(quantidadeAcertos);
                saida.close();
                socket.close();

                System.out.println("Estudante desconectado");
                usuarioAutenticado = false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
