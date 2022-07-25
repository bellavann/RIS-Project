package system.ris;

import static system.ris.App.ds;
import datastorage.Appointment;
import datastorage.InputValidation;
import datastorage.Order;
import datastorage.Patient;
import datastorage.PatientAlert;
import datastorage.Payment;
import datastorage.User;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import static system.ris.App.ds;

public class PatientPortal extends Stage{
    /*
        personal information
            full name
            email
            username
            dob
            address
            insurance
            patient alerts
            orders -- Should patients be able to see their orders?
            CHANGE PASSWORD
        appointments
        bills
        medical history
    */

//<editor-fold>
    
    //Navbar
    HBox navbar = new HBox();
    Label username = new Label("Logged In as Patient: " + App.patient.getFullName());
    Label info = new Label("My Info");
    Label appointments = new Label("Appointments");
    Label bills = new Label("Bills");
    Button logOut = new Button("Log Out");
    //End Navbar
    
    //Table
    TableView table = new TableView();
    VBox infoContainer = new VBox();
    VBox appointmentsContainer = new VBox();
    VBox billsContainer = new VBox();
    
    //Scene
    BorderPane main = new BorderPane();
    Scene scene = new Scene(main);
    //End Scene
    
    private FilteredList<Appointment> flAppointment;
    
//</editor-fold>
    
    /*
        PatientPortal Constructor.
        Creates and populates the Patient Portal Page
    */
    public PatientPortal(){
        this.setTitle("RIS - Radiology Information System (Patient)");
        
        //Navbar
        navbar.setAlignment(Pos.TOP_RIGHT);
        logOut.setPrefHeight(30);
        username.setId("navbar");
        HBox navButtons = new HBox(info, appointments, bills);
        navButtons.setAlignment(Pos.TOP_LEFT);
        navButtons.setSpacing(10);
        HBox.setHgrow(navButtons, Priority.ALWAYS);
        navbar.getChildren().addAll(navButtons, username, logOut);
        navbar.setStyle("-fx-background-color: #2f4f4f; -fx-spacing: 15;");
        main.setTop(navbar);

        info.setId("navbar");
        appointments.setId("navbar");
        bills.setId("navbar");
        //End Navbar

        //Center
        Label tutorial = new Label("Select one of the buttons above to get started!");
        main.setCenter(tutorial);
        info.setOnMouseClicked(eh -> infoPageView());
        appointments.setOnMouseClicked(eh -> appointmentsPageView());
        bills.setOnMouseClicked(eh -> billsPageView());
        //End Center
        
        //Buttons
        logOut.setOnAction((ActionEvent e) -> {
            logOut();
        });
        
        //Set Scene and Structure
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
        //End Scene
    }
    
    /*
        Logout
     */
    private void logOut() {
        App.user = new User();
        Stage x = new Login();
        x.show();
        x.setMaximized(true);
        this.close();
    }
    
    //<editor-fold defaultstate="collapsed" desc="Patient Info Section">
    
    private void infoPageView() {
        infoContainer.getChildren().clear();

        main.setCenter(infoContainer);
        
        Label fullNameTxt = new Label("Full Name: " + App.patient.getFullName() + "");
        Label emailTxt = new Label("Email: " + App.patient.getEmail() + "");
        Label usernameTxt = new Label("Username: " + App.patient.getUsername() + "");
        Label dobTxt = new Label("Date of Birth: " + App.patient.getDob() + "");
        Label addressTxt = new Label("Address: " + App.patient.getAddress() + "");
        Label insuranceTxt = new Label("Insurance: " + App.patient.getInsurance() + "");
        
        HBox hb1 = new HBox(fullNameTxt, usernameTxt);
        hb1.setSpacing(10);
        HBox hb2 = new HBox(emailTxt, dobTxt);
        hb2.setSpacing(10);;
        HBox hb3 = new HBox(addressTxt, insuranceTxt);
        hb3.setSpacing(10);
        
        Button updateInfo = new Button("Update Information");
        Button updatePassword = new Button("Update Password");
        HBox buttonContainer = new HBox(updateInfo, updatePassword);
        buttonContainer.setSpacing(10);
        
        infoContainer.getChildren().addAll(hb1, hb2, hb3, buttonContainer);
        infoContainer.setSpacing(10);
        
        info.setId("selected");
        appointments.setId("navbar");
        bills.setId("navbar");
        
        updateInfo.setOnAction((ActionEvent e) -> {
            updateInfo();
        });
        
        updatePassword.setOnAction((ActionEvent e) -> {
            updatePassword();
        });
    }

    private void updateInfo(){
        VBox container = new VBox();
        container.setPadding(new Insets(10));
        Stage x = new Stage();
        x.initOwner(this);
        x.setTitle("Update Information");
        x.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(container);
        x.setScene(scene);
        x.setHeight(400);
        x.setWidth(300);
        scene.getStylesheets().add("file:stylesheet.css");
        
        Label usernameLabel = new Label("Username: ");
        TextField usernameTxt = new TextField(App.patient.getUsername());
        HBox usernameContainer = new HBox(usernameLabel, usernameTxt);
        
        Label emailLabel = new Label("Email: ");
        TextField emailTxt = new TextField(App.patient.getEmail());
        HBox emailContainer = new HBox(emailLabel, emailTxt);

        Label addressLabel = new Label("Address: ");
        TextField addressTxt = new TextField(App.patient.getAddress());
        HBox addressContainer = new HBox(addressLabel, addressTxt);

        Label insuranceLabel = new Label("Insurance: ");
        TextField insuranceTxt = new TextField(App.patient.getInsurance());
        HBox insuranceContainer = new HBox(insuranceLabel, insuranceTxt);

        Button submit = new Button("Submit");
        submit.setId("complete");
        
        container.getChildren().addAll(usernameContainer, emailContainer, addressContainer, insuranceContainer, submit);
        x.show();
        
        submit.setOnAction((ActionEvent eh) -> {
            //Validation
            if (!InputValidation.validateUsername(usernameTxt.getText())) {
                return;
            }
            if (!InputValidation.validateEmail(emailTxt.getText())) {
                return;
            }
            if (!InputValidation.validateAddress(addressTxt.getText())) {
                return;
            }
            if (!InputValidation.validateInsurance(insuranceTxt.getText())) {
                return;
            }
            //End Validation
            App.patient.setUsername(usernameTxt.getText());
            App.patient.setAddress(addressTxt.getText());
            App.patient.setEmail(emailTxt.getText());
            App.patient.setInsurance(insuranceTxt.getText());
            String sql = "UPDATE patients SET username = '" + usernameTxt.getText() + ",, email = '" + emailTxt.getText() + "', address = '" + addressTxt.getText() + "', insurance = '" + insuranceTxt.getText() + "' WHERE patientID = '" + App.patient.getPatientID() + "';";
            App.executeSQLStatement(sql);
            x.close();
        });
    }
    
    private void updatePassword(){
        VBox container = new VBox();
        container.setPadding(new Insets(10));
        Stage x = new Stage();
        x.initOwner(this);
        x.setTitle("Update Password");
        x.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(container);
        x.setScene(scene);
        x.setHeight(400);
        x.setWidth(300);
        scene.getStylesheets().add("file:stylesheet.css");
        
        Label passwordLabel = new Label("Password: (Good passwords are long but easy to remember, try phrases with numbers and special chars mixed in.)");
        TextField passwordTxt = new TextField("");
        
        
        Button submit = new Button("Submit");
        submit.setId("complete");
        
        container.getChildren().addAll(passwordLabel, passwordTxt, submit);
        x.show();
        
        submit.setOnAction((ActionEvent eh) -> {
            //Validation
            if (!InputValidation.validatePassword(passwordTxt.getText())) {
                return;
            }
            String sql = "UPDATE patients SET password = '" + passwordTxt.getText() + "' WHERE patient_id = '" + App.patient.getPatientID() + "';";
            App.executeSQLStatement(sql);
            x.close();    
        });
    }
    
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Appointments Section">
    
    private void appointmentsPageView() {
        appointmentsContainer.getChildren().clear();

        main.setCenter(appointmentsContainer);
        createTableAppointments();
        populateTableAppointments();

        appointmentsContainer.getChildren().addAll(table);
        appointmentsContainer.setSpacing(10);
        info.setId("navbar");
        appointments.setId("selected");
        bills.setId("navbar");
    }

    private void createTableAppointments() {
        table.getColumns().clear();
        
        //All of the Columns
        TableColumn apptIDCol = new TableColumn("Appointment ID");
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn firstNameCol = new TableColumn("Full Name");
        TableColumn timeCol = new TableColumn("Time of Appt.");
        TableColumn orderCol = new TableColumn("Orders Requested");
        TableColumn status = new TableColumn("Status");
        
        //All of the Value setting
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        orderCol.setCellValueFactory(new PropertyValueFactory<>("order"));
        status.setCellValueFactory(new PropertyValueFactory<>("statusAsLabel"));

        //Set Column Widths
        apptIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        patientIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        firstNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        timeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        orderCol.prefWidthProperty().bind(table.widthProperty().multiply(0.4));
        status.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        
        //Add columns to table
        table.getColumns().addAll(apptIDCol, patientIDCol, firstNameCol, timeCol, orderCol, status);
    }

    private void populateTableAppointments() {
        table.getItems().clear();
        
        //Connect to database
        String sql = "Select appt_id, patient_id, patients.full_name, time, statusCode.status"
                + " FROM appointments"
                + " INNER JOIN statusCode ON appointments.statusCode = statusCode.statusID "
                + " INNER JOIN patients ON patients.patientID = appointments.patient_id"
                + " WHERE patient_id = '" + App.patient.getPatientID() + "'"
                + " ORDER BY time ASC;";

        try {

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
    
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="Bills Section">
    
    private void billsPageView(){
        billsContainer.getChildren().clear();

        main.setCenter(billsContainer);
        createTableBills();
        populateTableBills();

        billsContainer.getChildren().addAll(table);
        billsContainer.setSpacing(10);
        info.setId("navbar");
        appointments.setId("navbar");
        bills.setId("selected");
    }
    
    private void createTableBills(){
        table.getColumns().clear();
        
        //All of the Columns
        TableColumn apptIDCol = new TableColumn("Appointment ID");
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn firstNameCol = new TableColumn("Full Name");
        TableColumn timeCol = new TableColumn("Time of Appt.");
        TableColumn orderCol = new TableColumn("Orders Requested");
        TableColumn status = new TableColumn("Status");
        TableColumn totalCost = new TableColumn("Total Cost");
        
        //All of the Value setting
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        orderCol.setCellValueFactory(new PropertyValueFactory<>("order"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalCost.setCellValueFactory(new PropertyValueFactory<>("total"));
      
        //Set Column Widths
        apptIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        patientIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        firstNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        timeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.06));
        orderCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        status.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        totalCost.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

        //Add columns to table
        table.getColumns().addAll(apptIDCol, patientIDCol, firstNameCol, timeCol, orderCol, totalCost, status);
    }
    
    private void populateTableBills(){
        table.getItems().clear();

        //Connect to database
        String sql = "Select appt_id, patient_id, patients.full_name, time, statusCode.status"
                + " FROM appointments"
                + " INNER JOIN statusCode ON appointments.statusCode = statusCode.statusID "
                + " INNER JOIN patients ON patients.patientID = appointments.patient_id"
                + " WHERE patient_id = '" + App.patient.getPatientID() + "' AND statusCode = 6 AND viewable != 0 "
                + " ORDER BY time ASC;";

        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            List<Appointment> list = new ArrayList<Appointment>();

            while (rs.next()) {
                //What I receieve:  apptId, patientID, fullname, time, address, insurance, referral, status, order
                Appointment appt = new Appointment(rs.getString("appt_id"), rs.getString("patient_id"), rs.getString("time"), rs.getString("status"), getPatOrders(rs.getString("patient_id"), rs.getString("appt_id")));
                appt.setFullName(rs.getString("full_name"));
                appt.setTotal(calculateTotalCost(appt));
                
                System.out.println(appt);
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
    
    private float calculateTotalCost(Appointment appt) {
        String sql = "Select orderCodes.cost "
                + " FROM appointmentsOrdersConnector "
                + " INNER JOIN orderCodes ON appointmentsOrdersConnector.orderCodeID = orderCodes.orderID "
                + " WHERE apptID = '" + appt.getApptID() + "';";

        float value = 0;
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                value += rs.getFloat("cost");
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

//</editor-fold>
    
}
