package client_simple;

import server.models.Course;
import server.models.RegistrationForm;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientSimple
{
    /**
     * Constante du port utilisé pour ce connecter au serveur
     */
    public final static int PORT = 1337;
    /**
     * liste qui contient les cours de la session choisit.
     */
    public static ArrayList<Course> listeCourse = new ArrayList<Course>();
    /**
     * Tableau contenant les string des choix possibles de session
     */
    public final static String[] Session = new String[3];
    /**
     * Constante qui représente la commande pour inscrire un cour au serveur.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constante qui représente la commande pour obtenir les cours overt lors de la session au server.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * Un scanner qui reçoit les input de l'utilisateur
     */
    public final static Scanner scanner = new Scanner(System.in);

    /**
     * La fonction main initialise le tableau session commence le processus d'inscirption.
     * @param args ne fait rien
     */
    public static void main(String[] args)
    {
        Session[0] = "Automne"; Session[1] = "Hiver"; Session[2] = "Ete";
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
        ChoisirSession();
    }

    /**
     * La fonction ChoisirSession s'occupe de s'assurer que l'utilisateur écris une session valide et charge les cours offert dans cette session.
     * Pour se faire elle communique avec le server.
     * Une fois la session bien choisit, il passe à la prochaine étape, VoirCourOffert().
     */
    private static void ChoisirSession()
    {
        try
        {
            System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
            for(int i=0; i<Session.length; ++i)
            {
                System.out.print((i+1));
                System.out.println(". "+Session[i]);
            }
            System.out.print("> Choix: ");
            int input = Integer.parseInt(scanner.nextLine());

            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.writeObject(LOAD_COMMAND + " " + Session[input - 1]);
            os.flush();
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
            listeCourse = (ArrayList<Course>) is.readObject();
            is.close();
            VoireCoursOffert(Session[input - 1]);
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Se qu'il a été rentré ne fait pas partie des options.");
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            System.out.println("Le nombre entré ne fait pas partie des choix.");
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

    /**
     * La fonction VoireCoursOffert montre les cours offert dans la session choisit, offre le choix de changer de session ou d'inscire l'utilisateur a un cours cette session.
     * Pour se faire elle lit une réponse de l'utilisateur.
     * @param session Une string qui représente la session choisit par l'utilisateur.
     */
    private static void VoireCoursOffert(String session)
    {
        try
        {
            System.out.println("Les cours offerts pendant la session d'"+session+" sont:");
            int inc =  0;
            ArrayList<Integer> indexCours = new ArrayList<Integer>();
            for(int i = 0; i < listeCourse.size(); ++i)
            {
                Course cours = listeCourse.get(i);
                if(cours.getSession().equals(session))
                {
                    ++inc;
                    System.out.println(inc + ". "+cours.getCode()+" "+cours.getName());
                    indexCours.add(i);
                }
            }
            System.out.println("> Choix:");//J'ai ajouter cette ligne pour copier l'exemple.
            System.out.println("1. Consulter les cours offerts pour une autre session\n" +
                    "2. Incription à un cours");
            System.out.print("> Choix: ");
            int input = Integer.parseInt(scanner.nextLine());
            if(input == 1)
            {
                ChoisirSession();
            }
            else if(input == 2)
            {
                InscriptionCours(indexCours, session);
            }
            else
            {
                throw new InputMismatchException();
            }
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Se qu'il a été rentré ne fait pas partie des options.");
            ChoisirSession();
        }
        catch (NumberFormatException ex)
        {
            System.out.println("Se qu'il a été rentré ne fait pas partie des options.");
            ChoisirSession();
        }
    }

    /**
     * La fonction InscriptionCours va questionner l'utilisateur de l'information et si le cours fait partie des options inscrire l'utilisateur.
     * Autrement il retournera à la fonction VoireCoursOffert.
     * @param indexCours Une liste de nombre naturel qui indique l'indexx des cours qui sont offerte dans la session désirer parmis la liste total.
     * @param session Un string qui contient la session choisit.
     */
    private static void InscriptionCours(ArrayList<Integer> indexCours, String session)
    {
        try
        {
            System.out.println();
            String prenom = SaisieGénérale("prénom");
            String nom = SaisieGénérale("nom");
            String email = SaisieEmail(session);
            if(email == null)
            {
                return;
            }
            String matricule = SaisieMatricule(session);
            if(matricule == null)
            {
                return;
            }
            Course cours = SaisieCours(indexCours, session);
            if(cours == null)
            {
                return;
            }
            RegistrationForm coursInscrit = new RegistrationForm(prenom, nom, email, matricule, cours);

            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.writeObject(REGISTER_COMMAND + " ");
            os.flush();
            os.writeObject(coursInscrit);
            System.out.println("Félicitation! Inscription réussie de "+prenom+" au cours "+cours.getCode()+".");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Cette fonction demmande une question selon la valeure de info envoyer. Puis il lie la valeur et la retourne.
     * @param info Un string qui représente l'information demmander à l'utilisateur.
     * @return l'information que l'utilisateur a inscrit pour la question. Noté qu'aucune vérification n'a été faite.
     */
    private static String SaisieGénérale(String info)
    {
        System.out.print("Veuillez saisir votre "+info+": ");
        String input = scanner.nextLine();
        return input;
    }
    private static String SaisieEmail(String session)
    {
        String verificationEmail = "@gmail.com";
        int longeurverif = verificationEmail.length();
        boolean valide = true;
        System.out.print("Veuillez saisir votre email: ");
        String input = scanner.nextLine();
        int longeurEmail = input.length();
        if(longeurEmail < longeurverif)
        {
            System.out.println("Le email entré n'est pas valide");
            System.out.println();
            VoireCoursOffert(session);
            return null;
        }
        for(int i = 0; i<longeurverif; ++i)
        {
            valide = valide && (verificationEmail.charAt(i) == input.charAt(longeurEmail-longeurverif+i));
        }
        if(!valide)
        {
            System.out.println("Le email entré n'est pas valide");
            System.out.println();
            VoireCoursOffert(session);
            return null;
        }
        return input;
    }
    private static String SaisieMatricule(String session)
    {
        System.out.print("Veuillez saisir votre matricule: ");
        String input = scanner.nextLine();
        if(!input.matches("\\d+") || input.length() != 8)
        {
            System.out.println("La matricule entré n'est pas valide");
            System.out.println();
            VoireCoursOffert(session);
            return null;
        }
        return input;
    }
    private static Course SaisieCours(ArrayList<Integer> indexCours, String session)
    {
        System.out.print("Veuillez saisir le code du cours: ");
        String input = scanner.nextLine();
        for(int i=0; i<indexCours.size(); ++i)
        {
            if(listeCourse.get(indexCours.get(i)).getCode().equals(input))
            {
                return listeCourse.get(indexCours.get(i));
            }
        }
        System.out.println("Le code entré ne fais pas partie des cours disponible pour la session");
        System.out.println();
        VoireCoursOffert(session);
        return null;
    }
}
