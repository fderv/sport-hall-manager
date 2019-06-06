package Trainer;

import DBUtils.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class TrainerController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private int option;
    private int idOfReceived;

    public void setOption(int option) {
        this.option = option;
    }
    public void setIdOfReceived(int idOfReceived) {
        this.idOfReceived = idOfReceived;
    }

    private String insertSQL = "INSERT INTO trainer (name, surname, email, branch) VALUES(?,?,?,?)";
    private String updateSQL = "UPDATE trainer SET name=?, surname=?, email=?, branch=? WHERE id=?";

    @FXML
    private TextField traiName;
    @FXML
    private TextField traiSName;
    @FXML
    private TextField traiMail;
    @FXML
    private TextField traiBranch;

    @FXML
    private Label errorLabel;


    private Stage stage;

    @FXML
    private void doneButton(ActionEvent event) throws SQLException {
        stage = (Stage) errorLabel.getScene().getWindow();

        if (validateInput()) {
            if (option == 0) { //NEW ENTRY

                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, this.traiName.getText());
                    pstmt.setString(2, this.traiSName.getText());
                    pstmt.setString(3, this.traiMail.getText());
                    pstmt.setString(4, this.traiBranch.getText());
                    pstmt.execute();
                    conn.close();

                    stage.close();
                } catch (SQLException e) {
                    errorLabel.setText("Some SQL exception.");
                }

            } else if (option == 1) { //MODIFY ENTRY

                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(updateSQL);
                    pstmt.setString(1, this.traiName.getText());
                    pstmt.setString(2, this.traiSName.getText());
                    pstmt.setString(3, this.traiMail.getText());
                    pstmt.setString(4, this.traiBranch.getText());
                    pstmt.setInt(5, this.idOfReceived);
                    pstmt.execute();
                    conn.close();

                    stage.close();
                } catch (SQLException e) {
                    errorLabel.setText("Some SQL exception.");
                }
            }
        } else {
            errorLabel.setText("Some error occured. \nCheck inputs.");
        }
    }

    @FXML
    private void cancelButton(ActionEvent event) throws SQLException {
        stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if ((traiName.getText().length() > 1) && (traiName.getText().length() < 40) && (traiSName.getText().length() > 1) &&
                (traiSName.getText().length() < 25) && (traiMail.getText().length() > 3) && (traiMail.getText().length() < 35) &&
                (traiBranch.getText().length() > 1) && (traiBranch.getText().length() < 25)) {
            return true;
        }
        return false;
    }
}










