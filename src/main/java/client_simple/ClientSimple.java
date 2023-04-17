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

    public final static int PORT = 1337;
    public static ArrayList<Course> listeCourse = new ArrayList<Course>();
    public final static String[] Session = new String[3];
    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    public final static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args)
    {
        Session[0] = "Automne"; Session[1] = "Hiver"; Session[2] = "Ete";
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");
        ChoisirSession();
    }
    /*private static void AjouterListeCour()
    {
        try
        {
            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.writeObject(LOAD_COMMAND + " " + "argument");
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
            //System.out.println(is.readObject().toString());
            CréerListeCour(is.readObject().toString().split("\n"));
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }
    }*/
    /*private static void CréerListeCour(String[] coursListe)
    {
        for(int i = 0; i < coursListe.length; ++i)
        {
            String[] cours = coursListe[i].split("\t");
            listeCourse.add(new Course(cours[1], cours[0], cours[2]));
        }
    }*/
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
        }
    }
    private static void InscriptionCours(ArrayList<Integer> indexCours, String session)
    {
        try
        {
            System.out.println();
            String prenom = SaisieGénérale("prénom");
            String nom = SaisieGénérale("nom");
            String email = SaisieGénérale("email");
            String matricule = SaisieGénérale("matricule");
            Course cours = SaisieCours(indexCours, session);
            if(cours != null)
            {
                RegistrationForm coursInscrit = new RegistrationForm(prenom, nom, email, matricule, cours);

                Socket clientSocket = new Socket("127.0.0.1", 1337);
                ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
                os.writeObject(REGISTER_COMMAND + " ");
                os.flush();
                os.writeObject(coursInscrit);
                System.out.println("Félicitation! Inscription réussie de "+prenom+" au cours "+cours.getCode()+".");
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
    private static String SaisieGénérale(String info)
    {
        System.out.print("Veuillez saisir votre "+info+": ");
        String input = scanner.nextLine();
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
