package server;

public class ServerLauncher {
    /**
     * Constante du port utilisé pour ce connecter au serveur
     */
    public final static int PORT = 1337;

    /**
     * Cette fonction démarre le server. Elle doit être appeler en premier.
     * @param args ne fais absolument rien
     */
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}