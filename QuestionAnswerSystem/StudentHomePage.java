package QuestionAnswerSystem;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays the student's home page, showing existing questions,
 * allowing students to post new questions and answer existing ones,
 * and providing a filter for resolved answers.
 */
public class StudentHomePage {
    private boolean filter = false; // Flag to indicate if only resolved answers should be displayed.
    private boolean canFilter = false; // Flag to indicate if there are answers available to filter.
    private boolean reviewer = false; // Flag to indicate if the current user has the 'reviewer' role (currently not used in the provided code).
    private final QASystemDatabase QADatabase; // Instance of the database class to interact with the database.
    private application.User user; // The currently logged-in user.

    /**
     * Constructor for the StudentHomePage.
     * @param QADatabase The database instance to be used.
     * @param user The currently logged-in user.
     */
    public StudentHomePage(QASystemDatabase QADatabase, application.User user) {
        this.user = user;
        this.QADatabase = QADatabase;
    }

	//VBox questionDisplay = new VBox();
    public void show(Stage primaryStage) {
    	
    	
        // Check if the user has the 'reviewer' role.
        String[] role = user.getRole().strip().split(",");
    	System.out.println(Arrays.toString(role) + role.length);
    	
        for(int i = 0; i < role.length; i++) {
        	String s = role[i];
        	System.out.println(s);
        	if(s.strip().equals("reviewer")) {
        		reviewer = true;
        		System.out.println("Reviewer: " + reviewer);
        	}
        }
    	System.out.println("Reviewer: " + reviewer);
    	
    	
    	
    	VBox layout = new VBox(5);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");


        // Label for the list of posted questions.
        Label existingQuestionsLabel = new Label("Questions:");
        layout.getChildren().add(existingQuestionsLabel);
        updatePage(layout); // Initial population of the page with questions and answers.
	    
        // Textbox for the user to write their question.
        TextField postQuestion = new TextField("Write your Question here");
        Button submit = new Button("Submit");
        submit.setOnAction(a -> {
            String input = postQuestion.getText();
            try {
                Question newQ = new Question(user.getUserName(), input);
                QADatabase.addQuestion(newQ); // Add the new question to the database.
                System.out.println(user.getUserName() + " posted a new question");
                updatePage(layout); // Refresh the page to display the newly posted question.
            } catch (SQLException e) {
                // Handle potential SQL exceptions during question submission.
                e.printStackTrace();
            }
        });
        layout.getChildren().addAll(postQuestion, submit);
	    
	    //Checkbox to remove answers that did not resolve the issue
        CheckBox filterCheckbox = new CheckBox("Show only resolved answers");
        filterCheckbox.setOnAction(e -> {
        	//Send an error if there is nothing to filter
        	if(canFilter) {
                filter = filterCheckbox.isSelected();
                System.out.println("filter checkbox changed");
                updatePage(layout);
        	}else {
        		System.out.println("Error: nothing to filter");
        	}
        });
        layout.getChildren().add(filterCheckbox);
    	
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Questions and Answers");
        primaryStage.show();
    }
    
    
    public void updatePage(VBox layout) {
    	QADatabase.printAnswers(QADatabase.getAnswers());
    	VBox questionDisplay = new VBox();
    	layout.getChildren().removeIf(node -> node instanceof VBox);
    	ArrayList<Question> questions = QADatabase.getQuestions();
    	
		for(Question q : questions) {
			//iterate through the questions
			/*For each question:
			 * add a button to delete the question
			 * 
			 * display the corresponding answers to each question
			 * add a button for each answer to mark as resolved
			 * add a button to delete the answer
			 * 
			 * add a textbox to write a new answer
			 * add a button to submit the new answer
			 * 
			 * */
			
			Label ql = new Label("Question: " + q.getInput());
			ql.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
			Label ul = new Label("By User: " + q.getUserName());
			ul.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
			
			Button deleteQuestion= new Button("Delete this Question");
		    deleteQuestion.setOnAction(e -> {
		        try {
		        	QADatabase.deleteQuestion(q);
		            System.out.println("Question: " + q.getInput() + " successfully deleted");
		        }catch (SQLException e2) {
		        	e2.printStackTrace();
		        }
		        updatePage(layout);
		    });
		    
			questionDisplay.getChildren().addAll(ql, ul, deleteQuestion);
			
				ArrayList<Answer> answers = QADatabase.getAnswersbyQuestion(q);
                ArrayList<Answer> resolvedAnswers = QADatabase.getResolvedAnswers();
                
				if(!answers.isEmpty()) {
					canFilter = true;
		            Label answerLabel = new Label("Answers");
		            answerLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
		            questionDisplay.getChildren().add(answerLabel);
		            
		            
		            for (int j = 0; j < answers.size(); j++) {
		            	Answer ans = answers.get(j);
		            	
		            	//do not display the answer if it is unresolved and the filter check is on
		            	try {
			            	System.out.println("Filter on: " + filter);
			            	System.out.println("Resolved: " + QADatabase.isResolved(ans));
							if(filter && !QADatabase.isResolved(ans)) {
								continue;
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
		            	
		            	
		                Label answerText = new Label(ans.getInput());
		                answerText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
		                Label answerFrom = new Label("By User: " + user.getUserName());
			            answerFrom.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");
		                
			            //Checkbox to mark an answer that resolves the issue
		                CheckBox resolvedCheckBox = new CheckBox("This resolved my issue?");
		                resolvedCheckBox.setStyle("-fx-font-size: 10px;");
		                resolvedCheckBox.setOnAction(
		                		a -> {
		                			ans.changeResolve(resolvedCheckBox.isSelected());
		                    		try {
										QADatabase.changeResolveAnswer(ans);
										System.out.println(QADatabase.isResolved(ans));
										QADatabase.printAnswers(QADatabase.getResolvedAnswers());
									} catch (SQLException e1) {
										e1.printStackTrace();
									}
		                		}
		                );
		                
		                
		                //add a button to delete an answer
		                Button deleteAnswer = new Button("Delete this answer");
		                deleteAnswer.setStyle("-fx-font-size: 10px;");
		                deleteAnswer.setOnAction(e -> {
		                    try {
								QADatabase.deleteAnswer(ans);
								updatePage(layout);
		                        System.out.println("Answer deleted: " + ans.getInput());
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
		                    
		                });
		                questionDisplay.getChildren().addAll(answerText, answerFrom, resolvedCheckBox, deleteAnswer);
		            
		            
		    		if(reviewer) {
		    				ArrayList<Review> reviews = QADatabase.getReviewsByAnswer(ans);
		    				if(!reviews.isEmpty()) {
	 		    			for(Review r : reviews) {
				                Label reviewText = new Label(r.getInput());
				                reviewText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-alignment: right;");
				                Label reviewFrom = new Label("By Reviewer: " + user.getUserName());
					            reviewFrom.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-alignment: right;");
					            
					            questionDisplay.getChildren().addAll(reviewText, reviewFrom);
			    			}
		    				}
		    			
		    			
		    			    TextField postReview = new TextField("Write your Review here");
		    			    postReview.setStyle("-fx-font-size: 10px;");
		    			    Button submitReview = new Button("Submit Review");
		    			    submitReview.setStyle("-fx-font-size: 10px;");
		    			    submitReview.setOnAction(c -> {
		    			    	String input = postReview.getText();
		    			    	//send an error if there is an empty text box
		    			        if (!input.isEmpty()) {
		    			            Review review = new Review(ans, user.getUserName(), input);
		    			            try {
		    							QADatabase.addReview(review);
		    							QADatabase.printReviews(QADatabase.getReviews());
		    			                updatePage(layout); // Refresh UI to display the new answer;
		    			                canFilter = true;
		    						} catch (SQLException e1) {
		    							e1.printStackTrace();
		    						}
		    			        }else {
		    			        	System.out.println("Error: Empty answer. Please try again");
		    			        }
		    			        
		    			    });
		    			    questionDisplay.getChildren().addAll(postReview, submitReview);
		    				
		    		}
		            
		            
		            
		            
		            }
				}
				
			    TextField postAnswer = new TextField("Write your answer here");
			    postAnswer.setStyle("-fx-font-size: 10px;");
			    Button submitAnswer = new Button("Submit Answer");
			    submitAnswer.setStyle("-fx-font-size: 10px;");
			    
			    
			    submitAnswer.setOnAction(c -> {
			    	String input = postAnswer.getText();
			    	//send an error if there is an empty text box
			        if (!input.isEmpty()) {
			            Answer answer = new Answer(q, user.getUserName(), input);
			            try {
							QADatabase.addAnswer(answer);
			                updatePage(layout); // Refresh UI to display the new answer;
			                canFilter = true;
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
			        }else {
			        	System.out.println("Error: Empty answer. Please try again");
			        }
			        
			    });
			    questionDisplay.getChildren().addAll(postAnswer, submitAnswer);

		}
		layout.getChildren().add(questionDisplay);
 
    }
    
    
}
