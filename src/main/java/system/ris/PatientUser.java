package system.ris;

import static system.ris.App.ds;
import static system.ris.App.url;
import datastorage.User;
import datastorage.Appointment;
import datastorage.InputValidation;
import datastorage.Patient;
import datastorage.PatientAlert;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PatientUser extends Stage {
//<editor-fold>
    
    //Navbar
    HBox navbar = new HBox();
    Label username = new Label("Logged In as Patient: " + App.user.getFullName());
    ImageView pfp = new ImageView(App.user.getPfp());
    Label info = new Label("My Info");
    Label appointments = new Label("Appointments");
    Button logOut = new Button("Log Out");
    //End Navbar

    //Table
    TableView table = new TableView();
    VBox patientsContainer = new VBox();
    VBox appointmentsContainer = new VBox();

    //Scene
    BorderPane main = new BorderPane();
    Scene scene = new Scene(main);
    //End Scene
   
    private FilteredList<Patient> flPatient;
    private FilteredList<Appointment> flAppointment;
//</editor-fold>
    
    /*
        Receptionist Constructor.
        Creates and populates the Receptionist Page
     */
    PatientUser() {
        this.setTitle("RIS- Radiology Information System (Patient)");
        
        //Navbar
        navbar.setAlignment(Pos.TOP_RIGHT);
        logOut.setPrefHeight(30);
        pfp.setPreserveRatio(true);
        pfp.setFitHeight(38);
    //    username.setId("navbar");
    //    username.setOnMouseClicked(eh -> userInfo());
        HBox navButtons = new HBox(info, appointments);
        navButtons.setAlignment(Pos.TOP_LEFT);
        navButtons.setSpacing(10);
        HBox.setHgrow(navButtons, Priority.ALWAYS);
        navbar.getChildren().addAll(navButtons, username, pfp, logOut);
        navbar.setStyle("-fx-background-color: #2f4f4f; -fx-spacing: 15;");
        main.setTop(navbar);
        
        info.setId("navbar");
        appointments.setId("navbar");
        //End Navbar

        //Center
        Label tutorial = new Label("Select one of the buttons above to get started!");
        main.setCenter(tutorial);
        info.setOnMouseClicked(eh -> patientsPageView());
        appointments.setOnMouseClicked(eh -> appointmentsPageView());
        //End Center
        
        //Buttons
        logOut.setOnAction((ActionEvent e) -> {
            logOut();
        });
        
        //Set Scene and Structure
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
        //End scene
    }

    /*
        Logout
     */
    private void logOut() {
        App.user = new User();
        Stage x = new Login();
        x.show();
        x.setMaximized(true);
        this.hide();
    }

    /*
        User Info Page
     */
    private void userInfo() {
        Stage x = new UserInfo();
        x.show();
        x.setMaximized(true);
        this.close();
    } 

//<editor-fold defaultstate="collapsed" desc="Patients Section">

    private void patientsPageView() {
        patientsContainer.getChildren().clear();

        main.setCenter(patientsContainer);
        createTablePatients();
        populatePatientsTable();

        patientsContainer.getChildren().addAll(table);
        patientsContainer.setSpacing(10);
        info.setId("selected");
        appointments.setId("navbar");
        
    }

    private void createTablePatients() {
        table.getColumns().clear();
        
        //All of the Columns
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn fullNameCol = new TableColumn("Full Name");
        TableColumn usernameCol = new TableColumn("Username");
        TableColumn emailCol = new TableColumn("Email");
        TableColumn DOBCol = new TableColumn("Date of Birth");
        TableColumn insuranceCol = new TableColumn("Insurance");

        //All of the Value setting
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        DOBCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        insuranceCol.setCellValueFactory(new PropertyValueFactory<>("insurance"));

        //Set Column Widths
        patientIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        fullNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        usernameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        emailCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        DOBCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        insuranceCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));

        table.setStyle("-fx-background-color: #25A18E; -fx-text-fill: WHITE; ");

        //Add columns to table
        table.getColumns().addAll(patientIDCol, fullNameCol, usernameCol, emailCol, DOBCol, insuranceCol);
    }

    private void populatePatientsTable() {
        table.getItems().clear();
        
        //Connect to database
        String sql = "Select patients.patientID, patients.email, patients.full_name, patients.username, patients.dob, patients.address, patients.insurance"
                + " FROM patients"
                + " WHERE patients.email = '" + App.user.getEmail() + "';";

        try {

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            List<Patient> list = new ArrayList<>();
            
            while (rs.next()) {
                //What I receieve:  patientID, email, full_name, dob, address, insurance
                Patient pat = new Patient(rs.getString("patientID"), rs.getString("email"), rs.getString("full_name"), rs.getString("username"), rs.getString("dob"), rs.getString("address"), rs.getString("insurance"));
                list.add(pat);
            }

            for (Patient z : list) {
                z.placeholder.setText("Patient Overview");
                z.placeholder.setOnAction((ActionEvent e) -> {
                });
            }

            flPatient = new FilteredList(FXCollections.observableList(list), p -> true);
            table.getItems().addAll(flPatient);
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Appointments Section">

    private String getPatOrders(String patientID, String aInt) {
        String sql = "Select orderCodes.orders "
                + " FROM appointmentsOrdersConnector "
                + " INNER JOIN orderCodes ON appointmentsOrdersConnector.orderCodeID = orderCodes.orderID "
                + " WHERE apptID = '" + aInt + "';";

        String value = "";
        
        try {

            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                value += rs.getString("orders") + ", ";
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    private ArrayList<PatientAlert> populateAllergies(Patient z) {
        ArrayList<PatientAlert> allergies = new ArrayList<>();

        String sql = "Select patientAlerts.alertID, patientAlerts.alert "
                + " FROM patientAlerts "
                + " INNER JOIN alertsPatientConnector ON patientAlerts.alertID = alertsPatientConnector.alertID "
                + " WHERE alertsPatientConnector.patientID = '" + z.getPatientID() + "'"
                + ";";

        try {

            ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                PatientAlert pa = new PatientAlert(rs.getString("alertID"), rs.getString("alert"), getFlagsFromDatabase(rs.getString("alertID")));
                allergies.add(pa);
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return allergies;
    }

    private String getFlagsFromDatabase(String aInt) {
        String val = "";
        String sql = "Select orderCodes.orders "
                + " FROM flags "
                + " INNER JOIN orderCodes ON flags.orderID = orderCodes.orderID "
                + " WHERE alertID = '" + aInt + "' "
                + ";";

        try {

            ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            List<PatientAlert> list = new ArrayList<>();
            
            while (rs.next()) {
                //What I receieve:  patientID, email, full_name, dob, address, insurance
                val += rs.getString("orders") + ", ";
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return val;
    }

    private Patient pullPatientInfo(String patID) {
        Patient temp = null;

        String sql = "Select * "
                + " FROM patients"
                + " WHERE patientID = '" + patID + "';";

        try {

            ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                //What I receieve:  patientID, email, full_name, dob, address, insurance
                temp = new Patient(rs.getString("patientID"), rs.getString("email"), rs.getString("full_name"), rs.getString("username"), rs.getString("dob"), rs.getString("address"), rs.getString("insurance"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return temp;
    }

private void appointmentsPageView() {
        appointmentsContainer.getChildren().clear();

        main.setCenter(appointmentsContainer);
        createTableAppointments();
        populateTableAppointments();

        
        
        appointmentsContainer.getChildren().addAll(table);
        appointmentsContainer.setSpacing(10);
        info.setId("navbar");
        appointments.setId("selected");

        
    }

    private void createTableAppointments() {
        table.getColumns().clear();
        
        //All of the Columns
        TableColumn apptIDCol = new TableColumn("Appointment ID");
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn firstNameCol = new TableColumn("Full Name");
        TableColumn timeCol = new TableColumn("Time of Appt.");
        TableColumn orderCol = new TableColumn("Orders Requested");

        //All of the Value setting
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        orderCol.setCellValueFactory(new PropertyValueFactory<>("order"));

        
        //Set Column Widths
        apptIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        patientIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        firstNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        timeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        orderCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));

        
        //Add columns to table
        table.getColumns().addAll(apptIDCol, patientIDCol, firstNameCol, timeCol, orderCol);
    }

    private void populateTableAppointments() {
        table.getItems().clear();
        
        //Connect to database
        String sql = "Select appt_id, patient_id, patients.full_name, time, statusCode.status"
                + " FROM appointments"
                + " INNER JOIN statusCode ON appointments.statusCode = statusCode.statusID "
                + " INNER JOIN patients ON patients.patientID = appointments.patient_id"
                + " WHERE patients.email = '" + App.user.getEmail() + "'"
                + " ORDER BY time ASC;";

        try {

            ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            List<Appointment> list = new ArrayList<>();

            while (rs.next()) {
                //What I receieve:  apptId, patientID, fullname, time, address, insurance, referral, status, order
                Appointment appt = new Appointment(rs.getString("appt_id"), rs.getString("patient_id"), rs.getString("time"), rs.getString("status"), getPatOrders(rs.getString("patient_id"), rs.getString("appt_id")));
                appt.setFullName(rs.getString("full_name"));
                list.add(appt);
            }

            
            flAppointment = new FilteredList(FXCollections.observableList(list), p -> true);
            table.getItems().addAll(flAppointment);
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    
//</editor-fold>
    
}
