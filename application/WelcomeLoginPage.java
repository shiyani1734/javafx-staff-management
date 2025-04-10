package application;

import javafx.scene.Scene;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import databasePart1.*;
import QuestionAnswerSystem.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {

    private final DatabaseHelper databaseHelper;
    private static final QASystemDatabase QAdatabase = new QASystemDatabase();

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        try {
            QAdatabase.connectToDatabase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        layout.getChildren().add(welcomeLabel);

        String[] role = user.getRole().strip().split(",");
        System.out.println(Arrays.toString(role));

        for (int i = 0; i < role.length; i++) {
            Button roleButton = new Button("Click here to continue to " + role[i] + " page");

            if (role[i].strip().equals("student")) {
                roleButton.setOnAction(b -> new StudentHomePage(QAdatabase, user).show(primaryStage));
            } else if (role[i].strip().equals("reviewer")) {
                roleButton.setOnAction(b -> new ReviewerHomePage(QAdatabase, user).show(primaryStage));
            } else if (role[i].strip().equals("staff")) {
                roleButton.setOnAction(b -> new StaffHomePage(databaseHelper).show(primaryStage));
            } else {
                roleButton.setOnAction(b -> new UserHomePage().show(primaryStage));
            }

            layout.getChildren().add(roleButton);
        }

        if ("admin".equals(user.getRole())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> new InvitationPage().show(databaseHelper, primaryStage));

            Label otPassword = new Label("Generate a one-time password");
            TextField usernameField = new TextField();
            usernameField.setPromptText("Enter user here");
            usernameField.setMaxWidth(250);

            Button generate = new Button("Generate Password");
            layout.getChildren().addAll(otPassword, usernameField, generate);

            generate.setOnAction(a -> {
                String userInput = usernameField.getText();
                try {
                    User u = databaseHelper.getUserByUsername(userInput);
                    String newPW = databaseHelper.generateInvitationCode(u.getRole());
                    u.setPassword(newPW);
                    databaseHelper.updateUser(u);
                    Label newPWLabel = new Label("The new password for " + userInput + " is " + newPW);
                    layout.getChildren().add(newPWLabel);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            TableView<ObservableList<String>> tb = new TableView<>();
            tb.setEditable(true);

            ArrayList<ArrayList<String>> data = databaseHelper.getTable();
            ObservableList<ObservableList<String>> users = FXCollections.observableArrayList();
            for (ArrayList<String> row : data) {
                users.add(FXCollections.observableArrayList(row));
            }

            TableColumn<ObservableList<String>, String> usernames = new TableColumn<>("Username");
            usernames.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(1)));

            TableColumn<ObservableList<String>, String> email = new TableColumn<>("Email Address");
            email.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(3)));

            TableColumn<ObservableList<String>, String> roles = new TableColumn<>("Role(s)");
            roles.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().get(4)));

            tb.getColumns().addAll(usernames, email, roles);
            tb.setItems(users);
            layout.getChildren().addAll(inviteButton, tb);
        }

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            Platform.exit();
        });

        layout.getChildren().add(quitButton);
        Scene welcomeScene = new Scene(layout, 800, 400);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Welcome Page");
    }
}
