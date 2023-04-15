package com.example.tp2v2;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import server.models.Course;

import java.io.IOException;

public class ClientFX extends Application {
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

        TableView tableView = new TableView();
        TableColumn<Course, String> column1 =
                new TableColumn<>("Code");
        TableColumn<Course, String> column2 =
                new TableColumn<>("Cours");
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        gauche.getChildren().add(tableView);

        HBox bas = new HBox();
        gauche.getChildren().add(bas);
        ChoiceBox cb = new ChoiceBox(FXCollections.observableArrayList("Automne", "Hiver", "Ete"));
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


        stage.setScene(scene);

        stage.show();
        /*
        HBox root = new HBox();

        Scene scene = new Scene(root, 1000, 750);
        ClientFX.class.getResource("hello-view.fxml");
        stage.setTitle("Inscription UdeM");

        VBox gauche = new VBox();

        VBox liste = new VBox();
        Text texte1 = new Text("Liste des cours");
        liste.getChildren().add(texte1);

        VBox selection = new VBox();

        HBox categorie = new HBox();
        Text texte2 = new Text("Code");
        Text texte3 = new Text("Cours");
        categorie.getChildren().add(texte2);
        categorie.getChildren().add(new Separator());
        categorie.getChildren().add(texte3);

        selection.getChildren().add(categorie);
        categorie.getChildren().add(new Text("Coming soon ;)"));
        gauche.getChildren().add(selection);
        gauche.getChildren().add(new Separator());

        HBox bas = new HBox();
        bas.getChildren().add(new Text("Coming Soon :O"));
        Button charger = new Button("Charger");
        bas.getChildren().add(charger);
        gauche.getChildren().add(bas);

        root.getChildren().add(gauche);
        root.getChildren().add(new Separator());


        VBox droite = new VBox();
        Text texte4 = new Text("Formulaire d'inscription");
        droite.getChildren().add(texte4);

        VBox information = new VBox();
        HBox prenom = new HBox();
        Text texte5 = new Text("Prénom");
        prenom.getChildren().add(texte5);

        HBox nom = new HBox();
        Text texte6 = new Text("Nom");
        prenom.getChildren().add(texte6);

        HBox email = new HBox();
        Text texte7 = new Text("Email");
        prenom.getChildren().add(texte7);

        HBox matricule = new HBox();
        Text texte8 = new Text("Matricule");
        prenom.getChildren().add(texte8);

        information.getChildren().add(prenom);
        information.getChildren().add(nom);
        information.getChildren().add(email);
        information.getChildren().add(matricule);
        root.getChildren().add(information);

        root.getChildren().add(droite);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        stage.setScene(scene);
        stage.show();
        */
    }

    public static void main(String[] args) {
        launch();
    }
}