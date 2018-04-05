import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<UserModel> userModelList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(2014);
        Gson gson = new Gson();
        while (true) {
            UserModel user = new UserModel();
            // Connect the client to the socket
            Socket socket = serverSocket.accept();
            System.out.println("client connected");
            // Prompt the user for a username
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("Enter username:");
            String username = bufferedReader.readLine();
            user.setSocket(socket);
            user.setUsername(username);
            // Append current user to the userModelList
            userModelList.add(user);
            //printWriter.println("welcome " + username);

            // Send a message to all participants that the current user has joined in
//            for (UserModel userModel : userModelList) {
//                if (userModel.getSocket() != socket) {
//                    printWriter = new PrintWriter(userModel.getSocket().getOutputStream(), true);
//                    printWriter.println(username + " has joined the chat room");
//                }
//            }

            new Thread() {
                @Override
                public void run() {
                    BufferedReader bufferedReader = null;
                    try {
                        InputStreamReader inputStreamReader = new InputStreamReader(user.getSocket().getInputStream());
                        bufferedReader = new BufferedReader(inputStreamReader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    while (true) {
                        try {
                            String line; // declare an empty string to store the incoming messages
                            while ((line = bufferedReader.readLine()) != null) { // while line is not null
                                System.out.println(line); // print the line in console
                                // construct the message model
                                Messages message = new Messages();
                                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                                message.setMessage(line);
                                message.setUsername(username);
                                message.setTimestamp(timestamp);
                                String jsonString = gson.toJson(message);

                                for (UserModel userItem : userModelList) { // for each user
                                    if (userItem.getSocket() != user.getSocket()) { // excluding current user
                                          // set up a print writer
                                         PrintWriter msgWriter = new PrintWriter(userItem.getSocket().getOutputStream(), true);
                                         // to send the message
                                         msgWriter.println(jsonString);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            Thread.currentThread();
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
}