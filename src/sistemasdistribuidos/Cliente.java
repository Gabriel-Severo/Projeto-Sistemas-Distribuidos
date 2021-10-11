package sistemasdistribuidos;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Cliente {
    public static Boolean realizarAutenticacao(Socket cliente, ObjectInputStream entrada, Scanner scanner) throws Exception {
        Long matricula = 0L;

        while(matricula==0) {
            System.out.print("Digite a matricula: ");
            try{
                matricula = scanner.nextLong();
                if(matricula <= 0) {
                    System.out.println("A matrícula informada é inválida.");
                }
            }catch(InputMismatchException e) {
                System.out.println("A matrícula informada é inválida.");
                scanner.nextLine();
            }
        }

        scanner.nextLine();
        System.out.print("Digite a senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = new Usuario(matricula, senha);

        cliente = new Socket("localhost", 8888);
        ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
        saida.writeObject(usuario);
        saida.close();
        cliente.close();

        cliente = new Socket("localhost", 8888);
        entrada = new ObjectInputStream(cliente.getInputStream());
        Boolean usuarioAutenticado = entrada.readBoolean();

        entrada.close();
        cliente.close();

        if(!usuarioAutenticado) {
            System.out.println("Credenciais inválidas");
        }

        return usuarioAutenticado;
    }

    public static void exibirQuestionario(Questao questao) {
        System.out.println(questao.getDescricao());
        for(String alternativa : questao.getAlternativas()) {
            System.out.println(alternativa);
        }
    }

    public static void main(String[] args) {
        try {
            Socket cliente = new Socket("localhost", 8888);
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());

            Boolean usuarioAutenticado = entrada.readBoolean();
            Scanner scanner = new Scanner(System.in);
            entrada.close();
            cliente.close();

            while(!usuarioAutenticado) {
                usuarioAutenticado = realizarAutenticacao(cliente, entrada, scanner);
            }

            Boolean questinarioFinalizado = false;

            while(!questinarioFinalizado) {
                cliente = new Socket("localhost", 8888);
                entrada = new ObjectInputStream(cliente.getInputStream());

                Questao questao = (Questao) entrada.readObject();

                entrada.close();
                cliente.close();
                int opcao=0;

                while(opcao==0) {
                    exibirQuestionario(questao);
                    System.out.print("Digite sua opção: ");
                    try{
                        opcao = scanner.nextInt();
                        if(opcao <= 0 || opcao > questao.getAlternativas().size()) {
                            System.out.println("Opção inválida");
                            opcao=0;
                            continue;
                        }
                    }catch(InputMismatchException e) {
                        System.out.println("Opção inválida");
                        scanner.nextLine();
                        continue;
                    }
                }

                cliente = new Socket("localhost", 8888);
                ObjectOutputStream saida = new ObjectOutputStream(cliente.getOutputStream());
                saida.writeInt(opcao);

                saida.close();
                cliente.close();

                cliente = new Socket("localhost", 8888);
                entrada = new ObjectInputStream(cliente.getInputStream());
                questinarioFinalizado = entrada.readBoolean();
                
                cliente.close();
                entrada.close();
            }

            cliente = new Socket("localhost", 8888);
            entrada = new ObjectInputStream(cliente.getInputStream());
            int quantidadeAcertos = entrada.readInt();
            cliente.close();
            entrada.close();
            System.out.println("Você acertou " + quantidadeAcertos + " questões");

            System.out.println("Conexão encerrada");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
