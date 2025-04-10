package QuestionAnswerSystem;

import application.User;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class StaffGUIPage {
    private final QASystemDatabase QADatabase;
    private final User user;
    private boolean showUnresolvedOnly = false;

    public StaffGUIPage(QASystemDatabase QADatabase, User user) {
        this.QADatabase = QADatabase;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label header = new Label("Staff Panel - Moderation Dashboard");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        layout.getChildren().add(header);

        // Checkbox to filter unresolved answers
        CheckBox unresolvedOnly = new CheckBox("Show only unresolved answers");
        unresolvedOnly.setOnAction(e -> {
            showUnresolvedOnly = unresolvedOnly.isSelected();
            updateLayout(layout);
        });
        layout.getChildren().add(unresolvedOnly);

        // Populate the rest of the page
        updateLayout(layout);

        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff GUI Page");
        primaryStage.show();
    }

    private void updateLayout(VBox layout) {
        layout.getChildren().removeIf(node -> node instanceof ScrollPane);

        VBox questionContainer = new VBox(15);
        questionContainer.setStyle("-fx-padding: 10;");

        ArrayList<Question> questions = QADatabase.getQuestions();
        if (questions == null || questions.isEmpty()) {
            Label noData = new Label("No questions found in the database.");
            layout.getChildren().add(noData);
            return;
        }

        for (Question q : questions) {
            VBox questionBox = new VBox(8);
            questionBox.setStyle("-fx-border-color: black; -fx-border-width: 1px; -fx-padding: 10;");

            Label qText = new Label("Question: " + q.getInput());
            qText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            Label qUser = new Label("Asked by: " + q.getUserName());

            Button deleteQ = new Button("Delete this Question");
            deleteQ.setOnAction(e -> {
                try {
                    QADatabase.deleteQuestion(q);
                    updateLayout(layout);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });

            questionBox.getChildren().addAll(qText, qUser, deleteQ);

            ArrayList<Answer> answers = QADatabase.getAnswersbyQuestion(q);
            if (answers != null && !answers.isEmpty()) {
                for (Answer a : answers) {
                    try {
                        if (showUnresolvedOnly && QADatabase.isResolved(a)) continue;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    VBox answerBox = new VBox(5);
                    answerBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 8;");

                    Label aText = new Label("Answer: " + a.getInput());
                    Label aUser = new Label("Answered by: " + a.getUserName());

                    CheckBox resolvedBox = new CheckBox("Mark as Resolved");
                    try {
                        resolvedBox.setSelected(QADatabase.isResolved(a));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    resolvedBox.setOnAction(e -> {
                        a.changeResolve(resolvedBox.isSelected());
                        try {
                            QADatabase.changeResolveAnswer(a);
                            updateLayout(layout);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    Button deleteA = new Button("Delete this Answer");
                    deleteA.setOnAction(e -> {
                        try {
                            QADatabase.deleteAnswer(a);
                            updateLayout(layout);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    answerBox.getChildren().addAll(aText, aUser, resolvedBox, deleteA);
                    questionBox.getChildren().add(answerBox);
                }
            }

            questionContainer.getChildren().add(questionBox);
        }

        ScrollPane scrollPane = new ScrollPane(questionContainer);
        scrollPane.setFitToWidth(true);
        layout.getChildren().add(scrollPane);
    }
}
