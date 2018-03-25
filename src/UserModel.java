import java.net.Socket;

public class UserModel {
    private String username;
    private Socket socket;

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}