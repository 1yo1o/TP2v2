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
    public static ArrayList<Course> listeCourse = new ArrayList<Course>();
    public static TableView tableView;
    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    public static ArrayList<String> listeErreur = new ArrayList<>();
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
            //String a = infoPrenom.getText();
        });
        stage.setScene(scene);

        envoyerInfo.setOnAction((event) ->
        {
            int index = tableView.getSelectionModel().selectedIndexProperty().get();
            Inscription(infoPrenom.getText(), infoNom.getText(), infoEmail.getText(), infoMatricule.getText(),index);
        });

        stage.show();


    }
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
            }
            else
            {
                AfficherErreur();
            }

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            System.out.println("No session selected");
        }
    }
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
    private void AfficherErreur()
    {
        String messageTotal = "";
        for(int i = 0; i< listeErreur.size(); ++i)
        {
            messageTotal += listeErreur.get(i);
        }
        Alert alert = new Alert(Alert.AlertType.ERROR, messageTotal, ButtonType.OK);
        alert.showAndWait();
    }
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

    public static void applicationLauch()
    {
        launch();
    }
}