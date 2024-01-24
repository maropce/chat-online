import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final List<ClientHandler> clients;
    private PrintWriter output;

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            output = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String username = input.readLine();
            System.out.println(username + " dołączył do czatu.");

            broadcast(username + " dołączył do czatu.");

            String clientMessage;
            while ((clientMessage = input.readLine()) != null) {
                System.out.println(username + ": " + clientMessage);
                broadcast(username + ": " + clientMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.output.println(message);
            }
        }
    }
}
