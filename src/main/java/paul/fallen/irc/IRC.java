package paul.fallen.irc;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class IRC {

    private static final String SERVER_ADDRESS = "";
    private static final int SERVER_PORT = 1234;
    private final ArrayList<String> chatPool;
    private Socket socket;
    private Scanner inputStream;
    private PrintWriter outputStream;

    public IRC() {
        chatPool = new ArrayList<>();
        connectToServer();
    }

    public void connectToServer() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            inputStream = new Scanner(socket.getInputStream());
            outputStream = new PrintWriter(socket.getOutputStream(), true); // autoFlush
            startListening();
        } catch (IOException e) {
            System.out.println("Error connecting to server.");
        }
    }

    public void startListening() {
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    if (inputStream.hasNextLine()) {
                        String message = inputStream.nextLine();
                        synchronized (chatPool) {
                            if (!chatPool.contains(message)) {
                                chatPool.add(message);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String content) {
        if (socket != null && socket.isConnected()) {
            outputStream.println(content);
            outputStream.flush(); // Ensure message is sent immediately
        }
    }

    public void sendMessage(String content, int id) {
        if (socket != null && socket.isConnected()) {
            outputStream.println(id + " " + content);
            outputStream.flush(); // Ensure message is sent immediately
        }
    }

    public ArrayList<String> getChatPool() {
        synchronized (chatPool) {
            return new ArrayList<>(chatPool);
        }
    }
}