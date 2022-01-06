package Test;

import Ultility.State;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectedUser implements Runnable {
    public String myName;
    private Socket socket;
    private ConnectedUser pairedWith;
    public int currState;
    BufferedReader in;
    BufferedWriter out;

    public static ArrayList<ConnectedUser> clients = new ArrayList<>();

    public ConnectedUser(Socket s) throws IOException {
        this.socket = s;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        this.currState = State.NEW_USER.getID();
        this.myName = "";
    }

    public static  boolean CheckIfExisted(String username)
    {
        for (ConnectedUser client : clients)
        {
            if (client.myName.equals(username))
            {
                return true;
            }
        }
        return false;
    }

    public void run() {
        System.out.println( socket.toString() + " accepted");
        try {
            String input = "";
            while(true)
            {
                input = in.readLine();
                String[] processedInput = new String[] {"",""};

                if(input.equals("exit"))
                {
                    this.out.write("exit");
                    this.out.newLine();
                    this.out.flush();
                    clients.remove(this);
                    break;
                }

                if (input.contains(";"))
                {
                    processedInput = input.split(";");
                }

                else
                {
                    if (!(this.currState == State.IN_CHAT.getID()))
                    {
                        this.out.write("User currently not in chat mode");
                        this.out.newLine();
                        this.out.flush();
                    }
                    else
                    {
                        this.pairedWith.out.write(this.pairedWith.myName + ": " + input);
                        this.pairedWith.out.newLine();
                        this.pairedWith.out.flush();
                    }
                    continue;
                }

                if (!processedInput[1].equals("create") && this.currState == State.NEW_USER.getID())
                {
                    this.out.write("Username not created, user are not permitted to use available command");
                    this.out.newLine();
                    this.out.flush();
                    continue;
                }

                if (processedInput[1].equals("create"))
                {
                    if (CheckIfExisted(processedInput[0]))
                    {
                        this.out.write("Username already existed, choose another username");
                        this.out.newLine();
                        this.out.flush();
                        continue;
                    }
                    else
                    {
                        this.out.write("Connected to lobby, to chat with another user use invite user command");
                        this.out.newLine();
                        this.out.flush();
                        this.currState = State.IN_LOBBY.getID();
                        this.myName = processedInput[0];
                        clients.add(this);
                    }
                }

                for (ConnectedUser client : clients)
                {

                    if (CheckIfExisted(processedInput[0]) == false && !processedInput[0].equals("chat") && !processedInput[0].equals("exit"))
                    {
                        this.out.write("There is no user with username " + processedInput[0]);
                        this.out.newLine();
                        this.out.flush();
                        break;
                    }

                    if(processedInput[1].equals("exit"))
                    {
                        if (client == this.pairedWith)
                        {
                            this.out.write("chat;disable");
                            this.out.newLine();
                            this.out.flush();

                            client.out.write("chat;disable");
                            client.out.newLine();
                            client.out.flush();

                            this.currState = State.IN_LOBBY.getID();
                            this.pairedWith = null;

                            client.currState = State.IN_LOBBY.getID();
                            client.pairedWith = null;

                            break;
                        }
                    }

                    if (!client.myName.equals(this.myName) && client.myName.equals(processedInput[0]))
                    {

                        if (processedInput[1].equals("invite"))
                        {

                            if (client.currState == State.MID_INVITE.getID() || this.currState == State.MID_INVITE.getID())
                            {
                                out.write( "User is being invite to chat");
                                out.newLine();
                                out.flush();
                                break;
                            }

                            if (client.currState == State.IN_CHAT.getID()) {
                                out.write(client.myName + " is in chat mode");
                                out.newLine();
                                out.flush();
                                break;
                            }
                            else if (this.currState == State.IN_CHAT.getID())
                            {
                                out.write("you are in chat mode");
                                out.newLine();
                                out.flush();
                                break;
                            }

                            client.currState = State.MID_INVITE.getID();
                            this.currState = State.MID_INVITE.getID();

                            client.out.write(this.myName + ";" + "request");
                            client.out.newLine();
                            client.out.flush();
                            break;
                        }
                        if (processedInput[1].equals("accept"))
                        {
                            this.out.write("chat;enable");
                            this.out.newLine();
                            this.out.flush();

                            client.out.write("chat;enable");
                            client.out.newLine();
                            client.out.flush();

                            this.currState = State.IN_CHAT.getID();
                            client.currState = State.IN_CHAT.getID();
                            this.pairedWith = client;
                            client.pairedWith = this;
                            break;
                        }
                        if (processedInput[1].equals("deny"))
                        {
                            client.out.write(this.myName + " denies chat request");
                            client.out.newLine();
                            client.out.flush();
                            break;
                        }
                    }
                }
            }
            System.out.println("Closed socket for client " + myName + " " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

