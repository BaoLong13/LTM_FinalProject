package Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SendMessage implements Runnable {
    private BufferedWriter out;
    private Socket socket;

    public SendMessage(Socket s, BufferedWriter o) {
        this.socket = s;
        this.out = o;
    }

    public void run() {
        try {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input create user command");
            while(true) {
                String data = stdIn.readLine();
                out.write(data + '\n');
                out.flush();
                if(data.equals("exit"))
                    break;
            }
            System.out.println("Client closed connection");
            out.close();
            socket.close();
        } catch (IOException e) {}
    }
}

class ReceiveMessage implements Runnable {
    private BufferedReader in;
    private Socket socket;
    public ReceiveMessage(Socket s, BufferedReader i) {
        this.socket = s;
        this.in = i;
    }
    public void run() {
        try {
            while(true) {
                String data = in.readLine();
                 String[] processedData = new String[] {"",""};

                 if (data.equals("exit"))
                 {
                     break;
                 }

                if (data.contains(";"))
                {
                    processedData = data.split(";");
                }

                else
                {
                    System.out.println(data);
                }

                if (data.equals("chat;enable"))
                {
                    System.out.println("Chat enable");
                }

                if (data.equals("chat;disable"))
                {
                    System.out.println("Chat exited, returning to lobby");
                }

                if (processedData[1].equals("request"))
                {
                    System.out.println("Chat request from " + processedData[0]);
                }
            }
        } catch (IOException e) {}
    }
}

public class Client {
    private static String host = "localhost";
    private static int port = 1234;
    private static Socket socket;
    private static BufferedWriter out;
    private static BufferedReader in;

    public static void main(String[] args) throws IOException{
        socket = new Socket(host, port);
        System.out.println("Client connected");
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        ExecutorService executor = Executors.newFixedThreadPool(Server.numThread);
        SendMessage send = new SendMessage(socket, out);
        ReceiveMessage receive = new ReceiveMessage(socket, in);
        executor.execute(send);
        executor.execute(receive);

    }
}
