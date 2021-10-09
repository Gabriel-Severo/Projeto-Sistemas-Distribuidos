package sistemasdistribuidos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket cliente = new Socket("localhost", 8888);
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

            Boolean usuarioAutenticado = entrada.readBoolean();
            Scanner s = new Scanner(System.in);
            entrada.close();
            cliente.close();

            while(!usuarioAutenticado) {
                System.out.println("Usuário não autenticado");
                System.out.print("Digite a matricula");
                Long matricula = s.nextLong();
                s.nextLine();
                System.out.print("Digite a senha: ");
                String senha = s.nextLine();

                Usuario usuario = new Usuario(matricula, senha);

                cliente = new Socket("localhost", 8888);
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.writeObject(usuario);
                saida.close();
                cliente.close();

                cliente = new Socket("localhost", 8888);
                entrada = new ObjectInputStream(cliente.getInputStream());
                usuarioAutenticado = entrada.readBoolean();

                entrada.close();
                cliente.close();
            }
            System.out.println("Conexão encerrada");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
