package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    /**
     * Constante qui représente la commande pour inscrire un cour.
     */

    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constante qui représente la commande pour charger la liste des cours disponnibles.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * Socket du server, facilite dans la manipulation du transfert d'objet
     */
    private final ServerSocket server;
    /**
     * Socket du client, facilite dans la manipulation du transfert d'objet
     */
    private Socket client;
    /**
     * Facilite dans la manipulation du transfert d'objet
     */
    private ObjectInputStream objectInputStream;
    /**
     * Facilite dans la manipulation du transfert d'objet
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Facilite la dissection de la commande envoyé.
     */
    private final ArrayList<EventHandler> handlers;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Boucle principale du serveur. Elle s'occupe d'acceuillir les clients et de les referer au bonne fonction pour traité leurs demmandes.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cette fonction attend la commende du client et la traduit
     * @throws IOException Si le client n'envoit pas la bonne chose
     * @throws ClassNotFoundException Si le client n'envoit pas la bonne chose
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    /**
     * Cette fonction traduit la ligne de commande envoier.
     * @param line Un string représentant la ligne a traduire.
     * @return Retourne la commande et un argument.
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Cette fonction déconnecte le client du serveur
     * @throws IOException Si jamais un telle chose est impossible
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Cette fonction va appliquer la ligne de commande qui lui a été envoyé.
     * @param cmd Un string représentant la commande a executer.
     * @param arg Un string représentant l'argument de la fonction.
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     Lire un fichier texte contenant des informations sur les cours.txt et les transofmer en liste d'objets 'Course'.
     La méthode filtre les cours.txt par la session spécifiée en argument.
     Ensuite, elle renvoie la liste des cours.txt pour une session au client en utilisant l'objet 'objectOutputStream'.
     La méthode gère les exceptions si une erreur se produit lors de la lecture du fichier ou de l'écriture de l'objet dans le flux.
     @param arg la session pour laquelle on veut récupérer la liste des cours.txt
     */
    public void handleLoadCourses(String arg)
    {
        try
        {
            File texte = new File("./src/main/java/server/data/cours.txt");
            Scanner scanner = new Scanner(texte);
            ArrayList<Course> listeCourSession = new ArrayList<Course>();
            while(scanner.hasNextLine())
            {
                String[] data = scanner.nextLine().split("\t");
                if(data[2].equals(arg))
                {
                    listeCourSession.add(new Course(data[1], data[0], data[2]));
                }
            }
            objectOutputStream.writeObject(listeCourSession);
            scanner.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant 'objectInputStream', l'enregistrer dans un fichier texte
     et renvoyer un message de confirmation au client.
     La méthode gére les exceptions si une erreur se produit lors de la lecture de l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration()
    {
        try
        {
            RegistrationForm nouvelleInscription = (RegistrationForm) objectInputStream.readObject();
            File texte = new File("./src/main/java/server/data/inscription.txt");
            FileOutputStream fos = new FileOutputStream(texte, true);

            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            Course coursInscrit = nouvelleInscription.getCourse();
            String message = coursInscrit.getSession() + "\t" + coursInscrit.getCode() + "\t"
                    + nouvelleInscription.getMatricule() + "\t" + nouvelleInscription.getPrenom()
                    + "\t" + nouvelleInscription.getNom() + "\t" + nouvelleInscription.getEmail() + "\n";
            BufferedWriter writer = new BufferedWriter(osw);
            writer.append(message);
            writer.flush();
            writer.close();

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }
}
