package Course;

import DBUtils.DBConnection;
import Place.PlaceData;
import Trainer.TrainerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class CourseController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateComboBoxes();
    }


    @FXML
    TextField courseTitleTF;
    @FXML
    ComboBox<String> courseTrainerCB;
    @FXML
    ComboBox<String> coursePlaceCB;
    @FXML
    ComboBox<Integer> quota;
    @FXML
    TextArea descriptionTA;

    @FXML
    Label errorLabel;


    ObservableList<TrainerData> trainers = FXCollections.observableArrayList();
    ObservableList<String> trainerNames = FXCollections.observableArrayList();

    ObservableList<PlaceData> places = FXCollections.observableArrayList();
    ObservableList<String> placeNames = FXCollections.observableArrayList();

    ObservableList<Integer> quotaNum = FXCollections.observableArrayList();

    public void populateComboBoxes() {


        for (int i = 0; i < 100; i++) {
            quotaNum.add(i);
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstForTrainers = conn.prepareStatement("SELECT * FROM trainer");
            PreparedStatement pstForPlaces = conn.prepareStatement("SELECT * FROM place");

            ResultSet placeSet = pstForPlaces.executeQuery();
            ResultSet trainerSet = pstForTrainers.executeQuery();

            while(trainerSet.next()) {
                trainers.add(new TrainerData(trainerSet.getInt("id"), trainerSet.getString("name"), trainerSet.getString("surname"),
                        trainerSet.getString("email"), trainerSet.getString("branch")));
                trainerNames.add(trainerSet.getString("name") + " " + trainerSet.getString("surname"));
            }
            while(placeSet.next()) {
                places.add((new PlaceData(placeSet.getInt("id"), placeSet.getString("name"), placeSet.getString("description"))));
                placeNames.add(placeSet.getString("name"));
            }
            courseTrainerCB.setItems(trainerNames);
            coursePlaceCB.setItems(placeNames);
            quota.setItems(quotaNum);


        } catch (Exception e) {
            if(e instanceof SQLException) {
                errorLabel.setText("Some SQL Exception");
            } else {
                errorLabel.setText("Some exception");
            }
        }
    }




    private Stage stage;

    @FXML
    public void doneButton() {
        stage = (Stage) errorLabel.getScene().getWindow();

        if(validateInput()) {
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement courseStatement = conn.prepareStatement("INSERT INTO course (title, description, quota) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
                courseStatement.setString(1, this.courseTitleTF.getText());
                courseStatement.setString(2, this.descriptionTA.getText());
                courseStatement.setInt(3, this.quota.getValue());
                courseStatement.executeUpdate();
                ResultSet rs = courseStatement.getGeneratedKeys();
                int idOfLastAddedCourse = 0;
                while(rs.next()) {
                    idOfLastAddedCourse = rs.getInt(1);
                }
                PreparedStatement sectionStatement = conn.prepareStatement("INSERT INTO section (trainerId, courseId, placeId) VALUES(?,?,?)");

                int indexOfTrainer = courseTrainerCB.getSelectionModel().getSelectedIndex();
                int indexOfPlace = coursePlaceCB.getSelectionModel().getSelectedIndex();

                int idOfSelectedTrainer = trainers.get(indexOfTrainer).getId();
                int idOfSelectedPlace = places.get(indexOfPlace).getId();

                sectionStatement.setInt(1, idOfSelectedTrainer);
                sectionStatement.setInt(2, idOfLastAddedCourse);
                sectionStatement.setInt(3, idOfSelectedPlace);

                sectionStatement.execute();

                conn.close();

                stage.close();
            } catch (Exception e) {
                if(e instanceof SQLException) {
                    errorLabel.setText("Some SQL Exception");
                } else {
                    errorLabel.setText("Some exception");
                }
            }
        }
    }

    @FXML
    public void cancelButton() {
        stage = (Stage) errorLabel.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if((courseTitleTF.getText().length() > 1) && (courseTitleTF.getText().length() < 30) && !(courseTrainerCB.getSelectionModel().isEmpty())
        && !(coursePlaceCB.getSelectionModel().isEmpty()) && !(quota.getSelectionModel().isEmpty())) {
            return true;
        }
        return false;
    }
}






























