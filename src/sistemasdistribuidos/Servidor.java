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
        questao2.addAlternativa("1) AASD");
        questao2.addAlternativa("2) BBSD");
        questao2.addAlternativa("3) CCSD");
        questao2.addAlternativa("4) DDSD");
        questao2.setAlternativaCorreta(1);
        questoes.add(questao2);

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
                    System.out.println(usuario.getMatricula());
                    if(Objects.nonNull(usuario)) {
                        Usuario credenciais = usuarios.get(usuario.getMatricula());
                        if(Objects.nonNull(credenciais) && credenciais.getSenha().equals(usuario.getSenha())) {
                            usuarioAutenticado = true;
                        }
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

                usuarioAutenticado = false;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
