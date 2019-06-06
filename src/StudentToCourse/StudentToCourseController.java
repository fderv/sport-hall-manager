package StudentToCourse;

import DBUtils.DBConnection;
import Student.StudentData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentToCourseController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateStudentCB();
    }

    private Stage stage;
    private int idOfReceived;

    public void setIdOfReceived(int idOfReceived) {
        this.idOfReceived = idOfReceived;
    }

    @FXML
    private ComboBox<String> studentCB;

    @FXML
    public void doneButtonS() {

        stage = (Stage) studentCB.getScene().getWindow();

        int indexOfStudent = studentCB.getSelectionModel().getSelectedIndex();
        int idOfStudent = studentList.get(indexOfStudent).getId();

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO enrollment (studentId, sectionId) VALUES(?,?)");
            pstm.setInt(1, idOfStudent);
            pstm.setInt(2, idOfReceived);
            pstm.execute();

            conn.close();

        } catch (Exception e) {

        }

        stage.close();

    }

    @FXML
    public void cancelButtonS() {
        stage = (Stage) studentCB.getScene().getWindow();
        stage.close();
    }

    ObservableList<StudentData> studentList;

    private void populateStudentCB() {

        studentList = FXCollections.observableArrayList();
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstm = conn.prepareStatement("SELECT * FROM student");
            pstm.executeQuery();
            ResultSet studentSet = pstm.executeQuery();

            while(studentSet.next()) {
                studentList.add(new StudentData(studentSet.getInt("id"), studentSet.getString("name"),
                                studentSet.getString("surname"), studentSet.getString("email")));
            }

            ObservableList<String> studentIdentityList = FXCollections.observableArrayList();

            for (int i = 0; i < studentList.size(); i++) {
                studentIdentityList.add(i, studentList.get(i).getName() + " " + studentList.get(i).getSurName() + " " + studentList.get(i).getId());
            }

            studentCB.setItems(studentIdentityList);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
