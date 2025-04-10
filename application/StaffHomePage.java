package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

public class StaffHomePage {

    private final DatabaseHelper databaseHelper;
    private final StaffManager staffManager;
    private TableView<User> table;
    private ObservableList<User> staffData;

    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.staffManager = new StaffManager(databaseHelper);
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label label = new Label("Staff Management");
        table = new TableView<>();
        staffData = FXCollections.observableArrayList(staffManager.getAllStaff());

        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUserName()));

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getRole()));

        table.getColumns().addAll(userCol, emailCol, roleCol);
        table.setItems(staffData);

        Button addBtn = new Button("Add Staff");
        Button deleteBtn = new Button("Delete Staff");
        Button refreshBtn = new Button("Refresh");

        addBtn.setOnAction(e -> addStaff());
        deleteBtn.setOnAction(e -> deleteStaff());
        refreshBtn.setOnAction(e -> refreshTable());

        layout.getChildren().addAll(label, table, addBtn, deleteBtn, refreshBtn);
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Staff Management Page");
        primaryStage.show();
    }

    private void refreshTable() {
        staffData.setAll(staffManager.getAllStaff());
    }

    private void addStaff() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter new staff details (comma-separated: username,password,email)");
        dialog.showAndWait().ifPresent(input -> {
            try {
                String[] parts = input.split(",");
                if (parts.length < 3) throw new IllegalArgumentException("Incomplete input.");
                User staff = new User(parts[0].trim(), parts[1].trim(), parts[2].trim(), "staff");
                if (!staffManager.exists(staff.getUserName())) {
                    staffManager.addStaff(staff);
                    refreshTable();
                } else {
                    showAlert("Staff already exists.");
                }
            } catch (Exception e) {
                showAlert("Error adding staff: " + e.getMessage());
            }
        });
    }

    private void deleteStaff() {
        User selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                staffManager.deleteStaff(selected.getUserName());
                refreshTable();
            } catch (SQLException e) {
                showAlert("Error deleting staff: " + e.getMessage());
            }
        } else {
            showAlert("No staff selected.");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }
}
