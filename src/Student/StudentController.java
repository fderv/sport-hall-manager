package Student;

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

public class StudentController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private TextField stuName;
    @FXML
    private TextField stuSName;
    @FXML
    private TextField stuMail;
    @FXML
    private TextField stuID;

    @FXML
    private Label errorLabel;

    private int option;
    private int idOfReceived;
    public void setOption(int option) {
        this.option = option;
    }
    public void setIdOfReceived(int idOfReceived) {
        this.idOfReceived = idOfReceived;
    }

    private String insertSQL = "INSERT INTO student (id, name, surname, email) VALUES(?,?,?,?)";
    private String updateSQL = "UPDATE student SET id=?, name=?, surname=?, email=? WHERE id=?";

    private Stage stage;

    @FXML
    public void doneButton(ActionEvent event) throws SQLException {
        stage = (Stage) errorLabel.getScene().getWindow();

        if (validateInput()) {
            if (option == 0) { //NEW ENTRY

                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, this.stuID.getText());
                    pstmt.setString(2, this.stuName.getText());
                    pstmt.setString(3, this.stuSName.getText());
                    pstmt.setString(4, this.stuMail.getText());
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
                    pstmt.setString(1, this.stuID.getText());
                    pstmt.setString(2, this.stuName.getText());
                    pstmt.setString(3, this.stuSName.getText());
                    pstmt.setString(4, this.stuMail.getText());
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
    public void cancelButton(ActionEvent event) throws SQLException {
        stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if ((stuName.getText().length() > 1) && (stuName.getText().length() < 40) && (stuSName.getText().length() > 1) &&
                (stuSName.getText().length() < 25) && (stuMail.getText().length() > 3) && (stuMail.getText().length() < 35))
        {
            return true;
        }
        return false;
    }
}
