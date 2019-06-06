package Main;

import Course.CourseController;
import Course.CourseData;
import DBUtils.DBConnection;
import DBUtils.DBLog;
import Place.PlaceController;
import Place.PlaceData;
import Student.StudentController;
import Student.StudentData;
import StudentToCourse.StudentToCourseController;
import Trainer.TrainerController;
import Trainer.TrainerData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {


//  GENERAL

    @FXML
    private Label logLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            getPlaceData();
            getTrainerData();
            getStudentData();
            getCourseData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        courseTable.setOnMouseClicked(e-> {
            getCourseDetails();
        });

    }


//  ------GENERAL

//------------------------

//  ---- COURSE TAB

    @FXML
    private TableView<CourseData> courseTable;
    @FXML
    private TableColumn<CourseData, String> courseNameCol;
    @FXML
    private TableColumn<CourseData, String> courseQuotaCol;

    @FXML
    private TreeView<String> courseDetails;



    @FXML
    private void addCourse(ActionEvent event) {
        try {
            displayCourseAdder();
        } catch (Exception e) {
            logLabel.setText("Something went wrong");
        }
    }

    @FXML
    private void deleteCourse(ActionEvent event) {

        try {
            int idOfToBeDeleted = courseTable.getSelectionModel().getSelectedItem().getId();
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM course WHERE id=?");
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM section WHERE courseId=?");
            PreparedStatement pstm3 = conn.prepareStatement("SELECT id FROM section WHERE courseId=?");

            pstm3.setInt(1, idOfToBeDeleted);
            ResultSet rs = pstm3.executeQuery();
            int idOfToBeDeletedSec = 0;
            while(rs.next()) {
                idOfToBeDeletedSec = rs.getInt(1);
            }

            PreparedStatement pstm4 = conn.prepareStatement("DELETE FROM enrollment WHERE sectionId=?");

            pstmt2.setInt(1, idOfToBeDeleted);
            pstmt.setInt(1, idOfToBeDeleted);
            pstm4.setInt(1, idOfToBeDeletedSec);


            pstm4.execute();
            pstmt.execute();
            pstmt2.execute();


            conn.close();
        } catch (Exception e) {
            logLabel.setText("Something went wrong");
        }

        getCourseData();
    }

    @FXML
    private void studentToCourse(ActionEvent event) {
        try {
            displayStudentToCourse();
        } catch (Exception e) {
            logLabel.setText("Something went wrong"+e.toString());
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    private void displayCourseAdder() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Course/course.fxml"));

        Parent root = (Parent) loader.load();
        Stage stage = new Stage();

        CourseController cController = loader.getController();

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setTitle("NEW COURSE");
        stage.showAndWait();
        getCourseData();
    }

    public void getCourseData() {
        ObservableList<CourseData> courseList = FXCollections.observableArrayList();


        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            logLabel.setText(DBLog.DB_CONNECTED);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM course");

            while(rs.next()) {
                courseList.add(new CourseData(rs.getInt("id"), rs.getString("title"), rs.getString("description"), rs.getInt("quota")));
            }
            courseNameCol.setCellValueFactory(new PropertyValueFactory<>("title"));
            courseQuotaCol.setCellValueFactory(new PropertyValueFactory<>("quota"));
            courseTable.setItems(courseList);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getCourseDetails() {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            TreeItem<String> courseRoot;
            TreeItem<String> courseTrainer = new TreeItem<>();
            TreeItem<String> coursePlace = new TreeItem<>();
            TreeItem<String> courseStudents = new TreeItem<>();
            courseRoot = new TreeItem<>();
            courseRoot.setExpanded(true);

            courseTrainer.setValue("Trainer");
            courseRoot.getChildren().add(courseTrainer);

            coursePlace.setValue("Place");
            courseRoot.getChildren().add(coursePlace);

            courseStudents.setValue("Students");
            courseRoot.getChildren().add(courseStudents);

            courseDetails.setRoot(courseRoot);
            courseDetails.setShowRoot(false);

            int idOfTheSelectedCourse = courseTable.getSelectionModel().getSelectedItem().getId();

            PreparedStatement statementToGetSectionID = conn.prepareStatement("SELECT id FROM section WHERE courseId=?");
            statementToGetSectionID.setInt(1, idOfTheSelectedCourse);
            ResultSet sectionIDSet = statementToGetSectionID.executeQuery();
            int idOfTheSelectedSection = 0;

            while(sectionIDSet.next()) {
                idOfTheSelectedSection = sectionIDSet.getInt(1);
            }

            PreparedStatement statementToGetStudents = conn.prepareStatement("SELECT studentId FROM enrollment WHERE sectionId=?");
            statementToGetStudents.setInt(1, idOfTheSelectedSection);
            ResultSet studentIDSet = statementToGetStudents.executeQuery();

            ObservableList<String> studentNameSurname = FXCollections.observableArrayList();

            while(studentIDSet.next()) {
                PreparedStatement statementToGetStudentName = conn.prepareStatement("SELECT name FROM student WHERE id=?");
                statementToGetStudentName.setInt(1, studentIDSet.getInt(1));
                ResultSet rsforstudentname = statementToGetStudentName.executeQuery();

                PreparedStatement statementToGetStudentSurname = conn.prepareStatement("SELECT surname FROM student WHERE id=?");
                statementToGetStudentSurname.setInt(1, studentIDSet.getInt(1));
                ResultSet rsforstudentsurname = statementToGetStudentSurname.executeQuery();

                while(rsforstudentname.next() && rsforstudentsurname.next()) {
                    studentNameSurname.add(rsforstudentname.getString(1) + " " + rsforstudentsurname.getString(1));
                }
            }

            for (int i = 0; i < studentNameSurname.size(); i++) {
                courseStudents.getChildren().add(new TreeItem<>(studentNameSurname.get(i)));
            }


            PreparedStatement statementToGetTrainer = conn.prepareStatement("SELECT trainerId FROM section WHERE courseId=?");
            statementToGetTrainer.setInt(1, idOfTheSelectedCourse);
            ResultSet trainerOfSelectedCourse = statementToGetTrainer.executeQuery();
            int trainerIdOfSelCourse = 0;
            while(trainerOfSelectedCourse.next()) {
                trainerIdOfSelCourse = trainerOfSelectedCourse.getInt(1);
            }

            PreparedStatement statementToGetTrainerName = conn.prepareStatement("SELECT name FROM trainer WHERE id=?");
            PreparedStatement statementToGetTrainerSurname = conn.prepareStatement("SELECT surname FROM trainer WHERE id=?");
            statementToGetTrainerName.setInt(1, trainerIdOfSelCourse);
            statementToGetTrainerSurname.setInt(1, trainerIdOfSelCourse);
            ResultSet trainerNameOfSelectedCourse = statementToGetTrainerName.executeQuery();
            ResultSet trainerSurnameOfSelectedCourse = statementToGetTrainerSurname.executeQuery();
            String nameOfTheTrainer = null;
            while(trainerNameOfSelectedCourse.next() && trainerSurnameOfSelectedCourse.next()) {
                nameOfTheTrainer = trainerNameOfSelectedCourse.getString(1) + " " + trainerSurnameOfSelectedCourse.getString(1);

            }


            PreparedStatement statementToGetPlace = conn.prepareStatement("SELECT placeId FROM section WHERE courseId=?");
            statementToGetPlace.setInt(1, idOfTheSelectedCourse);
            ResultSet placeOfSelectedCourse = statementToGetPlace.executeQuery();
            int placeIdOfSelCourse = 0;
            while(placeOfSelectedCourse.next()) {
                placeIdOfSelCourse = placeOfSelectedCourse.getInt(1);
            }

            PreparedStatement statementToGetPlaceName = conn.prepareStatement("SELECT name FROM place WHERE id=?");
            statementToGetPlaceName.setInt(1, placeIdOfSelCourse);
            ResultSet placeNameOfSelectedCourse = statementToGetPlaceName.executeQuery();
            String nameOfThePlace = null;
            while(placeNameOfSelectedCourse.next()) {
                nameOfThePlace = placeNameOfSelectedCourse.getString(1);
            }


            coursePlace.getChildren().add(new TreeItem<>(nameOfThePlace));
            courseTrainer.getChildren().add(new TreeItem<>(nameOfTheTrainer));

            conn.close();
        } catch (Exception e) {
            if(e instanceof SQLException) {
                logLabel.setText("Some SQL Exception"+e.toString());
            } else {
                logLabel.setText("Some exception");
            }
        }
    }

    private int getNumberOfStudents(int idOfCourse) throws Exception {
        Connection conn = DBConnection.getConnection();
        PreparedStatement getSectionStatement = conn.prepareStatement("SELECT id FROM section WHERE courseId=?");
        getSectionStatement.setInt(1, idOfCourse);
        ResultSet sectionSet = getSectionStatement.executeQuery();
        int sectId = 0;

        while(sectionSet.next()) {
            sectId = sectionSet.getInt(1);
        }

        PreparedStatement getCountStatement = conn.prepareStatement("SELECT COUNT(*) FROM enrollment WHERE sectionId=?");
        getCountStatement.setInt(1, sectId);
        ResultSet countStudentSet = getCountStatement.executeQuery();
        int numberOfStudents = 0;
        while(countStudentSet.next()) {
            numberOfStudents = countStudentSet.getInt(1);
        }

        return numberOfStudents;
    }

    private void displayStudentToCourse() throws Exception {
        int idOfSelectedCourse = courseTable.getSelectionModel().getSelectedItem().getId();
        int quotaOfSelectedCourse = courseTable.getSelectionModel().getSelectedItem().getQuota();

        if(getNumberOfStudents(idOfSelectedCourse) < quotaOfSelectedCourse) {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstm = conn.prepareStatement("SELECT id FROM section WHERE courseId=?");
            pstm.setInt(1, idOfSelectedCourse);
            ResultSet rs = pstm.executeQuery();
            int idToSend = 0;
            while(rs.next()) {
                idToSend = rs.getInt(1);
            }


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StudentToCourse/studentcourse.fxml"));

            Parent root = (Parent) loader.load();
            Stage stage = new Stage();

            StudentToCourseController stcController = loader.getController();
            stcController.setIdOfReceived(idToSend);

            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.setTitle("ADD STUDENT TO COURSE");
            stage.showAndWait();
            getCourseDetails();
        }
    }



//  ------COURSE TAB

//  --------------------------------

//  PLACE TAB

    @FXML
    private TableView<PlaceData> placeTable;
    @FXML
    private TableColumn<PlaceData, String> placeNameCol;
    @FXML
    private TableColumn<PlaceData, String> placeDescCol;

    @FXML
    private void addPlace(ActionEvent event) throws SQLException {

        try {
            displayPlaceAdder(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editPlace(ActionEvent event) throws SQLException {
        try {
            displayPlaceAdder(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deletePlace(ActionEvent event) {

        try {
            int idOfToBeDeleted = placeTable.getSelectionModel().getSelectedItem().getId();
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM place WHERE id=?");
            pstmt.setInt(1, idOfToBeDeleted);
            pstmt.execute();
            conn.close();

            getPlaceData();

        } catch (SQLException e) {
            logLabel.setText("SQL Exception");
        }
    }

    private void displayPlaceAdder(int option) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Place/place.fxml"));

        Parent root = (Parent) loader.load();
        Stage stage = new Stage();

        PlaceController pController = loader.getController();
        pController.setOption(option);

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        stage.setScene(scene);


        if (option == 0) {

            stage.setTitle("NEW PLACE");
            stage.showAndWait();
            pController.setOption(0);
            getPlaceData();


        } else if (option == 1) {

            stage.setTitle("EDIT PLACE");

            try {
                ((TextField)stage.getScene().lookup("#placeNameTF")).setText(placeTable.getSelectionModel().getSelectedItem().getName());
                ((TextArea)stage.getScene().lookup("#placeDescTA")).setText(placeTable.getSelectionModel().getSelectedItem().getDesc());
                pController.setIdOfReceived(placeTable.getSelectionModel().getSelectedItem().getId());
                pController.setOption(1);
                stage.showAndWait();
                getPlaceData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getPlaceData() throws SQLException {
        ObservableList<PlaceData> placeList = FXCollections.observableArrayList();


        Connection conn = DBConnection.getConnection();
        logLabel.setText(DBLog.DB_CONNECTED);
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM place");

        while(rs.next()) {
            placeList.add(new PlaceData(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
        }

        placeNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        placeDescCol.setCellValueFactory(new PropertyValueFactory<>("desc"));

        placeTable.setItems(placeList);
        conn.close();
    }

//  -----PLACE TAB

//  TRAINER TAB

    @FXML
    private TableView<TrainerData> trainerTable;
    @FXML
    private TableColumn<TrainerData, String> trainerNameCol;
    @FXML
    private TableColumn<TrainerData, String> trainerSNameCol;
    @FXML
    private TableColumn<TrainerData, String> trainerMailCol;
    @FXML
    private TableColumn<TrainerData, String> trainerBranchCol;

    @FXML
    private void addTrainer(ActionEvent event) throws SQLException {

        try {
            displayTrainerPopup(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editTrainer(ActionEvent event) throws SQLException {
        try {
            displayTrainerPopup(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteTrainer(ActionEvent event) {

        try {
            int idOfToBeDeleted = trainerTable.getSelectionModel().getSelectedItem().getId();
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM trainer WHERE id=?");
            pstmt.setInt(1, idOfToBeDeleted);
            pstmt.execute();
            conn.close();

            getTrainerData();

        } catch (SQLException e) {
            logLabel.setText("SQL Exception");
        }
    }

    private void displayTrainerPopup(int option) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Trainer/trainer.fxml"));

        Parent root = (Parent) loader.load();
        Stage stage = new Stage();

        TrainerController tController = loader.getController();
        tController.setOption(option);

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        if (option == 0) {

            stage.setTitle("NEW TRAINER");
            stage.showAndWait();
            getTrainerData();


        } else if (option == 1) {

            stage.setTitle("EDIT TRAINER");

            try {
                ((TextField)stage.getScene().lookup("#traiName")).setText(trainerTable.getSelectionModel().getSelectedItem().getName());
                ((TextField)stage.getScene().lookup("#traiSName")).setText(trainerTable.getSelectionModel().getSelectedItem().getSurName());
                ((TextField)stage.getScene().lookup("#traiMail")).setText(trainerTable.getSelectionModel().getSelectedItem().getEmail());
                ((TextField)stage.getScene().lookup("#traiBranch")).setText(trainerTable.getSelectionModel().getSelectedItem().getBranch());
                tController.setIdOfReceived(trainerTable.getSelectionModel().getSelectedItem().getId());
                stage.showAndWait();
                getTrainerData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getTrainerData() throws SQLException {
        ObservableList<TrainerData> trainerList = FXCollections.observableArrayList();

        Connection conn = DBConnection.getConnection();
        logLabel.setText(DBLog.DB_CONNECTED);
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM trainer");

        while(rs.next()) {
            trainerList.add(new TrainerData(rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
                    rs.getString("email"), rs.getString("branch")));
        }

        trainerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        trainerSNameCol.setCellValueFactory(new PropertyValueFactory<>("surName"));
        trainerMailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        trainerBranchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));

        trainerTable.setItems(trainerList);
        conn.close();
    }

    //  -----TRAINER

    //  STUDENT TAB

    @FXML
    private TableView<StudentData> studentTable;
    @FXML
    private TableColumn<StudentData, String> studentNameCol;
    @FXML
    private TableColumn<StudentData, String> studentSNameCol;
    @FXML
    private TableColumn<StudentData, String> studentMailCol;
    @FXML
    private TableColumn<StudentData, String> studentIDCol;

    @FXML
    private void addStudent(ActionEvent event) throws SQLException {

        try {
            displayStudentPopup(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editStudent(ActionEvent event) throws SQLException {
        try {
            displayStudentPopup(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteStudent(ActionEvent event) throws SQLException {
        int idOfToBeDeleted = studentTable.getSelectionModel().getSelectedItem().getId();
        Connection conn = DBConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("DELETE FROM student WHERE id=?");
        PreparedStatement pstmt2 = conn.prepareStatement("DELETE FROM enrollment WHERE studentId=?");
        pstmt2.setInt(1, idOfToBeDeleted);
        pstmt.setInt(1, idOfToBeDeleted);
        pstmt2.execute();
        pstmt.execute();

        conn.close();

        getStudentData();

    }

    private void displayStudentPopup(int option) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Student/student.fxml"));

        Parent root = (Parent) loader.load();
        Stage stage = new Stage();

        StudentController sController = loader.getController();
        sController.setOption(option);

        stage.initModality(Modality.APPLICATION_MODAL);
        Scene scene = new Scene(root);
        stage.setScene(scene);

        if (option == 0) {

            stage.setTitle("NEW STUDENT");
            stage.showAndWait();
            getStudentData();


        } else if (option == 1) {

            stage.setTitle("EDIT STUDENT");

            try {
                ((TextField)stage.getScene().lookup("#stuName")).setText(studentTable.getSelectionModel().getSelectedItem().getName());
                ((TextField)stage.getScene().lookup("#stuSName")).setText(studentTable.getSelectionModel().getSelectedItem().getSurName());
                ((TextField)stage.getScene().lookup("#stuMail")).setText(studentTable.getSelectionModel().getSelectedItem().getEmail());
                ((TextField)stage.getScene().lookup("#stuID")).setText(String.valueOf(studentTable.getSelectionModel().getSelectedItem().getId()));
                sController.setIdOfReceived(studentTable.getSelectionModel().getSelectedItem().getId());
                stage.showAndWait();
                getStudentData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getStudentData() throws SQLException {
        ObservableList<StudentData> studentList = FXCollections.observableArrayList();

        Connection conn = DBConnection.getConnection();
        logLabel.setText(DBLog.DB_CONNECTED);
        ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM student");

        while(rs.next()) {
            studentList.add(new StudentData(rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
                    rs.getString("eMail")));
        }

        studentNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentSNameCol.setCellValueFactory(new PropertyValueFactory<>("surName"));
        studentMailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        studentTable.setItems(studentList);
        conn.close();
    }

    //  -----STUDENT
}














