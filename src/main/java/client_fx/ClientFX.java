package client_fx;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ClientFX extends Application {
    /**
     * Une liste de cours disponnible
     */
    private static ArrayList<Course> listeCourse = new ArrayList<Course>();
    /**
     * Facilite la manipulation du contenue dans la table
     */
    private static TableView tableView;
    /**
     * Constante qui représente la commande pour inscrire un cour au serveur.
     */
    public final static String REGISTER_COMMAND = "INSCRIRE";
    /**
     * Constante qui représente la commande pour charger les cours au serveur.
     */
    public final static String LOAD_COMMAND = "CHARGER";
    /**
     * Une liste contenant tous les erreurs rencontrées lors de l'execution d'une commande.
     */
    private static ArrayList<String> listeErreur = new ArrayList<>();
    /**
     * Une variable booléaine qui sert a savoir si le textField doivent être effacé ou non.
     */
    private static boolean effaceInfo = true;

    /**
     * Créer l'interface au complet
     * @param stage Une classe de type stage
     * @throws IOException permet au fonction ultérieure de traiter le problème.
     */
    @Override
    public void start(Stage stage) throws IOException {
        HBox root = new HBox();
        Scene scene = new Scene(root, 525, 475);
        stage.setTitle("Inscription UdeM");
        VBox gauche = new VBox();
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        VBox droite = new VBox();
        root.getChildren().addAll(gauche, separator, droite);
        Text texteListe = new Text("Liste des cours");
        texteListe.setTextAlignment(TextAlignment.CENTER);

        gauche.getChildren().add(texteListe);
        ChargerCours("Automne");
        tableView = new TableView();
        TableColumn<Map, String> column1 =
                new TableColumn<>("Code");
        column1.setCellValueFactory(new MapValueFactory<>("Code"));
        column1.setSortable(false);
        TableColumn<Map, String> column2 =
                new TableColumn<>("Cours");
        column2.setCellValueFactory(new MapValueFactory<>("Cours"));
        column2.setSortable(false);
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView = Update(tableView);




        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        gauche.getChildren().add(tableView);

        HBox bas = new HBox();
        gauche.getChildren().add(bas);
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList("Automne", "Hiver", "Ete"));
        cb.setValue("Automne");
        Button chargerCours = new Button("charger");
        bas.getChildren().addAll(cb, chargerCours);

        GridPane gridPane = new GridPane();
        gridPane.setMinSize(100, 100);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);

        TextField infoPrenom = new TextField();
        TextField infoNom = new TextField();
        TextField infoEmail = new TextField();
        TextField infoMatricule = new TextField();
        Button envoyerInfo = new Button("envoyer");
        gridPane.add(new Text("Prénom"), 0, 0);
        gridPane.add(infoPrenom, 1, 0);
        gridPane.add(new Text("Nom"), 0, 1);
        gridPane.add(infoNom, 1, 1);
        gridPane.add(new Text("Email"), 0, 2);
        gridPane.add(infoEmail, 1, 2);
        gridPane.add(new Text("Matricule"), 0, 3);
        gridPane.add(infoMatricule, 1, 3);
        gridPane.add(envoyerInfo, 1, 4);
        Text froIns = new Text("Formulaire d'inscription");
        froIns.setTextAlignment(TextAlignment.CENTER);
        droite.getChildren().addAll(froIns, gridPane);

        chargerCours.setOnAction((event) ->
        {
            ChargerCours(cb.getValue().toString());
            tableView = Update(tableView);
        });
        stage.setScene(scene);

        envoyerInfo.setOnAction((event) ->
        {
            int index = tableView.getSelectionModel().selectedIndexProperty().get();
            Inscription(infoPrenom.getText(), infoNom.getText(), infoEmail.getText(), infoMatricule.getText(),index);
            if(effaceInfo)
            {
                infoPrenom.clear();
                infoNom.clear();
                infoEmail.clear();
                infoMatricule.clear();
            }

        });

        stage.show();
    }

    /**
     * Cette fonction sera appeler pour charger les cours d'une session désirée. Elle communique avec le serveur pour obtenir l'information necessaire.
     * @param session Un string indiquant quelle sesion doit être charger.
     */
    private void  ChargerCours(String session)
    {
        try
        {
            Socket clientSocket = new Socket("127.0.0.1", 1337);
            ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
            os.writeObject(LOAD_COMMAND + " " + session);
            ObjectInputStream is = new ObjectInputStream(clientSocket.getInputStream());
            listeCourse = (ArrayList<Course>) is.readObject();
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
     * Cette fonction va tenter d'inscrire l'utilisateur à un cour. L'inscription ne se fera pas si l'une des conditions suivante n'est pas respecter.
     * Un cour a été selectionné, l'email fini par @gmail.com et la matricule est composé d'exactement 8 chiffres.
     * La fonction affichera un message a l'utilisateur pour lui informer du success ou non de l'opération.
     * @param prenom Une string représentant le prénom entré par l'utilisateur.
     * @param nom Une string représentant le nom entré par l'utilisateur.
     * @param email Une string représentant le email entré par l'utilisateur.
     * @param matricule Une string représentant la matricule entré par l'utilisateur.
     * @param index Un int représentant l'index du cours désiré par l'utilisateur.
     */
    private void Inscription(String prenom, String nom, String email, String matricule, int index)
    {
        try
        {
            Course cours = listeCourse.get(index);
            RegistrationForm coursInscrit = new RegistrationForm(prenom, nom, email, matricule, cours);
            VerifierInfo(email, matricule);
            if(listeErreur.size() == 0)
            {
                Socket clientSocket = new Socket("127.0.0.1", 1337);
                ObjectOutputStream os = new ObjectOutputStream(clientSocket.getOutputStream());
                os.writeObject(REGISTER_COMMAND + " ");
                os.flush();
                os.writeObject(coursInscrit);
                String message = "Felicitation! "+prenom+" "+nom+" est inscrit(e) avec succès pour le cours "+cours.getCode()+"!";
                Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
                alert.showAndWait();
                effaceInfo = true;
            }
            else
            {
                effaceInfo = false;
                AfficherErreur();
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (IndexOutOfBoundsException ex)
        {
            listeErreur.add("Le formulaire est invalide.\nVous devez sélectionner un cours!");
            effaceInfo = false;
            AfficherErreur();
        }
    }

    /**
     * Cette fonction met à jour la table des cours
     * @param tableView la table en question
     * @return la table modifié
     */
    private TableView Update(TableView tableView)
    {
        tableView.getItems().clear();
        ObservableList<Map<String, Object>> items = FXCollections.<Map<String, Object>>observableArrayList();
        for(int i = 0; i<listeCourse.size(); ++i)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("Code", listeCourse.get(i).getCode());
            item.put("Cours", listeCourse.get(i).getName());
            items.add(item);
        }
        tableView.getItems().addAll(items);
        tableView.refresh();
        return tableView;
    }

    /**
     * Cette fonction affiche a l'utilisateur les erreurs dans le remplissage de formulaire.
     */
    private void AfficherErreur()
    {
        String messageTotal = "";
        for(int i = 0; i< listeErreur.size(); ++i)
        {
            messageTotal += listeErreur.get(i);
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, messageTotal, ButtonType.OK);
        alert.showAndWait();
        listeErreur.clear();
    }

    /**
     * Cette fonction vérifie si l'email et la matricule entré respecte les conditions.
     * L'email doit finir par @gmail.com
     * La matricule doit contenir exactement 8 chiffre.
     * @param email Un string représentant le email de l'utilisateur
     * @param matricule Un string représentant la matricule de l'utilisateur.
     */
    private void VerifierInfo(String email, String matricule)
    {
        String verificationEmail = "@gmail.com";
        int longeurEmail = email.length();
        int longeurverif = verificationEmail.length();
        boolean valide = true;
        if(longeurEmail < longeurverif)
        {
            listeErreur.add("Le formulaire est invalide.\nLe champ'Email' est invalide!\nAssurer vous que le email finisse par @gmail.com.");
        }
        for(int i = 0; i<longeurverif; ++i)
        {
            valide = valide && (verificationEmail.charAt(i) == email.charAt(longeurEmail-longeurverif+i));
        }
        if(!valide)
        {
            listeErreur.add("Le formulaire est invalide.\nLe champ'Email' est invalide!\nAssurer vous que le email finisse par @gmail.com.");
        }
        if(!matricule.matches("\\d+"))
        {
            if(listeErreur.size() == 0)
            {
                listeErreur.add("Le formulaire est invalide.\n");
            }
            listeErreur.add("Le champ'Matricule' est invalide!\nAssurer vous que la matricule ne contienne que des chiffres.");
        }
        if(matricule.length() != 8)
        {
            if(listeErreur.size() == 0)
            {
                listeErreur.add("Le formulaire est invalide.\n");
            }
            listeErreur.add("Le champ'Matricule' est invalide!\nAssurer vous que la matricule contienne exactement 8 chiffres.");
        }
    }

    /**
     * Fonction qui launch la classe. Utilisé pour contourner un bug avec le jar
     */

    public static void applicationLauch()
    {
        launch();
    }
}