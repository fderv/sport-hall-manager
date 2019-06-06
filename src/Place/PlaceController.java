package Place;

import DBUtils.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PlaceController implements Initializable {


    private int option;
    private int idOfReceived;

    public void setOption(int option) {
        this.option = option;
    }
    public void setIdOfReceived(int idOfReceived) {
        this.idOfReceived = idOfReceived;
    }

    private String insertSQL = "INSERT INTO place (name, description) VALUES(?,?)";
    private String updateSQL = "UPDATE place SET name=?, description=? WHERE id=?";


    @FXML
    private TextField placeNameTF;
    @FXML
    private TextArea placeDescTA;

    @FXML
    private Label errorLabel;

    private Stage stage;

    @FXML
    private void doneButton(ActionEvent event) {

        stage = (Stage) errorLabel.getScene().getWindow();


        if(validateInput()) {
            if (option == 0) {

                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                    pstmt.setString(1, this.placeNameTF.getText());
                    pstmt.setString(2, this.placeDescTA.getText());
                    pstmt.execute();
                    conn.close();

                    stage.close();
                } catch (SQLException e) {
                    errorLabel.setText("Some SQL exception.");
                }


            } else if (option == 1) {
                try {
                    Connection conn = DBConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(updateSQL);
                    pstmt.setString(1, this.placeNameTF.getText());
                    pstmt.setString(2, this.placeDescTA.getText());
                    pstmt.setInt(3, this.idOfReceived);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private boolean validateInput() {
        if ((placeNameTF.getText().length() < 21) && (placeNameTF.getText().length() > 1) &&
                (placeDescTA.getText().length() < 121)) {
            return true;
        }
        return false;
    }
}



















