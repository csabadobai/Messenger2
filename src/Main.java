import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<UserModel> userModelList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket = new ServerSocket(2014);
        while (true) {
            UserModel user = new UserModel();
            // Connect the client to the socket
            Socket socket = serverSocket.accept();
            System.out.println("client connected");
            // Prompt the user for a username
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            printWriter.println("Enter your username:");
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String username = bufferedReader.readLine();
            user.setSocket(socket);
            user.setUsername(username);
            // Append current user to the userModelList
            userModelList.add(user);
            printWriter.println("Welcome " + username);

            // Send a message to all participants that the current user has joined in
            for (UserModel userModel : userModelList) {
                if (userModel.getSocket() != user.getSocket()) {
                    printWriter = new PrintWriter(userModel.getSocket().getOutputStream(), true);
                    printWriter.println(username + " has joined the chat room");
                }
            }

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
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                System.out.println(line);

                                for (UserModel userItem : userModelList) {
                                    if (userItem.getSocket() != user.getSocket()) {
                                        PrintWriter msgWriter = new PrintWriter(userItem.getSocket().getOutputStream(), true);
                                        msgWriter.println(username + " said " + line + "\r");
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