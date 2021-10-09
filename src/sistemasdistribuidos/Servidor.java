package sistemasdistribuidos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Objects;

public class Servidor {
    public static void main(String[] args) {
        Boolean usuarioAutenticado = false;
        HashMap<Long, Usuario> usuarios = new HashMap<>();
        Usuario usuario1 = new Usuario(123L, "123");

        usuarios.put(123L, usuario1);

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
                    System.out.println("Estudante conectado: " + socket.getInetAddress().getHostAddress());
                    ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
                    Usuario usuario = (Usuario) entrada.readObject();
                    System.out.println(usuario.getMatricula());
                    if(Objects.nonNull(usuario)) {
                        Usuario credenciais = usuarios.get(usuario.getMatricula());
                        if(credenciais.getSenha().equals(usuario.getSenha())) {
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
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
