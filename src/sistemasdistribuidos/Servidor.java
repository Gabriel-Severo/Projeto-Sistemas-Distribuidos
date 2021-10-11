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
        questao1.setDescricao("Descricao");
        questao1.addAlternativa("1) AASD");
        questao1.addAlternativa("2) BBSD");
        questao1.addAlternativa("3) CCSD");
        questao1.addAlternativa("4) DDSD");
        questao1.setAlternativaCorreta(1);
        questoes.add(questao1);

        Questao questao2 = new Questao();
        questao2.setDescricao("Descricao2");
        questao2.addAlternativa("1) A");
        questao2.addAlternativa("2) B");
        questao2.addAlternativa("3) C");
        questao2.addAlternativa("4) D");
        questao2.setAlternativaCorreta(1);
        questoes.add(questao2);

        Questao questao3 = new Questao();
        questao3.setDescricao("Descricaoasdasd");
        questao3.addAlternativa("1) AA");
        questao3.addAlternativa("2) BB");
        questao3.addAlternativa("3) CC");
        questao3.addAlternativa("4) DD");
        questao3.setAlternativaCorreta(1);
        questoes.add(questao3);

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
