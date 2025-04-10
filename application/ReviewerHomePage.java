package application;

import java.sql.SQLException;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import QuestionAnswerSystem.*;

/**
 * This class represents the home page for a reviewer in the Question Answer System application.
 * It displays a list of reviews submitted by the currently logged-in reviewer.
 * Each review includes the original answer and the original question it was reviewing.
 */
public class ReviewerHomePage {

    /** Instance of the QASystemDatabase used to retrieve data. */
    private final QASystemDatabase QADatabase;

    /** The currently logged-in user (assumed to have a reviewer role). */
    private application.User user;

    /**
     * Constructs a ReviewerHomePage with the provided database and user.
     *
     * @param QADatabase The database instance used to fetch reviews.
     * @param user The user currently logged in (assumed to be a reviewer).
     */
    public ReviewerHomePage(QASystemDatabase QADatabase, application.User user) {
        this.user = user;
        this.QADatabase = QADatabase;
    }

    /**
     * Displays the reviewer's home page in the provided primaryStage.
     * It shows a list of reviews made by the user along with the corresponding
     * original answer and question for each review.
     *
     * @param primaryStage The JavaFX stage where the home page UI will be displayed.
     */
    public void show(Stage primaryStage) {
        VBox layout = new VBox(5);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Display the reviewer's name as a heading
        Label existingQuestionsLabel = new Label("Reviews from " + user.getUserName());
        layout.getChildren().add(existingQuestionsLabel);

        // Retrieve and display all reviews by the user
        ArrayList<Review> reviews = QADatabase.getReviewsByUser(user.getUserName());
        if (!reviews.isEmpty()) {
            for (Review r : reviews) {
                // Display the review text
                Label review = new Label(r.getInput());
                review.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

                // Display the original answer associated with the review
                Label answer = new Label("Original answer: " + r.getAnswer().getInput());
                answer.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

                // Display the original question associated with the answer
                Label question = new Label("Original question: " + r.getAnswer().getQuestion().getInput());
                question.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

                layout.getChildren().addAll(review, answer, question);
            }
        }

        // Set the scene and show the UI
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Questions and Answers");
        primaryStage.show();
    }
}