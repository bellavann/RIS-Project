package system.ris;

import static system.ris.App.ds;
import datastorage.User;
import datastorage.Appointment;
import datastorage.InputValidation;
import datastorage.Order;
import datastorage.Payment;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.swing.*;

public class Billing extends Stage {
//<editor-fold>
    
    //Navbar
    HBox navbar = new HBox();
    Label username = new Label("Logged In as Biller: " + App.user.getFullName());
    ImageView pfp = new ImageView(App.user.getPfp());
    Button logOut = new Button("Log Out");
    //End Navbar

    //Table
    TableView table = new TableView();
    
    //Scene
    BorderPane main = new BorderPane();
    Scene scene = new Scene(main);
    //End Scene

    //Search Bar
    FilteredList<Appointment> flAppointment;
    ChoiceBox<String> choiceBox = new ChoiceBox();
    TextField search = new TextField("Search Appointments");

    //Buttons
    Button refreshTable = new Button("Refresh Appointments");
    
    //Containers
    HBox searchContainer = new HBox(choiceBox, search);
    HBox buttonContainer = new HBox(refreshTable, searchContainer);
    VBox tableContainer = new VBox(table, buttonContainer);
    
    ArrayList<Order> varName = new ArrayList<>();

//</editor-fold>
    
    /*
        Billing Constructor.
        Creates and populates the Billing Page
     */
    Billing() {
        this.setTitle("RIS- Radiology Information System (Billing)");
    
        //Navbar
        navbar.setAlignment(Pos.TOP_RIGHT);
        logOut.setPrefHeight(30);
        pfp.setPreserveRatio(true);
        pfp.setFitHeight(38);
        username.setId("navbar");
        username.setOnMouseClicked(eh -> userInfo());
        navbar.getChildren().addAll(username, pfp, logOut);
        navbar.setStyle("-fx-background-color: #2f4f4f; -fx-spacing: 15;");
        main.setTop(navbar);
        //End navbar

        //Center
        loadCenter();
        varName = populateOrders();
        //End Center
        
        //Searchbar Structure
        searchContainer.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(searchContainer, Priority.ALWAYS);
        choiceBox.setPrefHeight(40);
        search.setPrefHeight(40);
        choiceBox.getItems().addAll("Appointment ID", "Patient ID", "Full Name", "Date/Time", "Status");
        choiceBox.setValue("Appointment ID");
        search.textProperty().addListener((obs, oldValue, newValue) -> {
            if (choiceBox.getValue().equals("Appointment ID")) {
                flAppointment.setPredicate(p -> new String(p.getApptID() + "").contains(newValue));//filter table by Appt ID
            }
            if (choiceBox.getValue().equals("Patient ID")) {
                flAppointment.setPredicate(p -> new String(p.getPatientID() + "").contains(newValue));//filter table by Patient Id
            }
            if (choiceBox.getValue().equals("Full Name")) {
                flAppointment.setPredicate(p -> p.getFullName().toLowerCase().contains(newValue.toLowerCase()));//filter table by Full name
            }
            if (choiceBox.getValue().equals("Date/Time")) {
                flAppointment.setPredicate(p -> p.getTime().contains(newValue));//filter table by Date/Time
            }
            if (choiceBox.getValue().equals("Status")) {
                flAppointment.setPredicate(p -> p.getStatus().toLowerCase().contains(newValue.toLowerCase()));//filter table by Status
            }
            table.getItems().clear();
            table.getItems().addAll(flAppointment);
        });
        //End Searchbar Structure
        
        //Buttons
        logOut.setOnAction((ActionEvent e) -> {
            logOut();
        });
        refreshTable.setOnAction((ActionEvent e) -> {
            populateTable();
        });
        
        //Set Scene and Structure
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
        //End scene

        populateTable();
    }

    //Add stuff to the center, and make it look good.
    private void loadCenter() {
        table.getColumns().clear();
        
        //Vbox to hold the table
        tableContainer.setAlignment(Pos.TOP_CENTER);
        tableContainer.setPadding(new Insets(20, 10, 10, 10));
        buttonContainer.setPadding(new Insets(10));
        buttonContainer.setSpacing(10);
        
        //All of the Columns
        TableColumn apptIDCol = new TableColumn("Appointment ID");
        TableColumn patientIDCol = new TableColumn("Patient ID");
        TableColumn firstNameCol = new TableColumn("Full Name");
        TableColumn timeCol = new TableColumn("Time of Appt.");
        TableColumn orderCol = new TableColumn("Orders Requested");
        TableColumn status = new TableColumn("Status");
        TableColumn updateAppt = new TableColumn("Update Billing");
        TableColumn totalCost = new TableColumn("Total Cost");
        TableColumn makePayment = new TableColumn("Make Payment");
        TableColumn delAppt = new TableColumn("Remove Appointment");
        
        //All of the Value setting
        apptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        patientIDCol.setCellValueFactory(new PropertyValueFactory<>("patientID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        orderCol.setCellValueFactory(new PropertyValueFactory<>("order"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        updateAppt.setCellValueFactory(new PropertyValueFactory<>("placeholder"));
        totalCost.setCellValueFactory(new PropertyValueFactory<>("total"));
        delAppt.setCellValueFactory(new PropertyValueFactory<>("placeholder1"));
        makePayment.setCellValueFactory(new PropertyValueFactory<>("button"));
        
        //Set Column Widths
        apptIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        patientIDCol.prefWidthProperty().bind(table.widthProperty().multiply(0.09));
        firstNameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        timeCol.prefWidthProperty().bind(table.widthProperty().multiply(0.06));
        orderCol.prefWidthProperty().bind(table.widthProperty().multiply(0.2));
        updateAppt.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        status.prefWidthProperty().bind(table.widthProperty().multiply(0.08));
        totalCost.prefWidthProperty().bind(table.widthProperty().multiply(0.1));

        //Add columns to table
        table.getColumns().addAll(apptIDCol, patientIDCol, firstNameCol, timeCol, orderCol, totalCost, status, updateAppt, makePayment, delAppt);
        table.setStyle("-fx-background-color: #25A18E; -fx-text-fill: WHITE; ");
        main.setCenter(tableContainer);
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
    
    private void populateTable() {
        table.getItems().clear();

        //Connect to database
        String sql = "Select appt_id, patient_id, patients.full_name, time, statusCode.status"
                + " FROM appointments"
                + " INNER JOIN statusCode ON appointments.statusCode = statusCode.statusID "
                + " INNER JOIN patients ON patients.patientID = appointments.patient_id"
                + " WHERE statusCode = 6 AND viewable != 0 "
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
                appt.button.setText("Make Payment");
                appt.button.setOnAction(eh -> makePayment(appt));
                appt.placeholder.setText("View Bill");
                appt.placeholder.setOnAction(eh -> viewBill(appt));
                appt.placeholder1.setText("Remove Appointment");
                appt.placeholder1.setId("cancel");
                appt.placeholder1.setOnAction(eh -> removeAppointment(appt));
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

    private void viewBill(Appointment appt) {
        Stage x = new Stage();
        x.setTitle("View Bill");
        x.setMaximized(true);

        BorderPane bp = new BorderPane();
        Scene sc = new Scene(bp);
        x.setScene(sc);
        sc.getStylesheets().add("file:stylesheet.css");
        
        //Header
        HBox header = new HBox();
        Label patientName = new Label("Patient Name:\n" + appt.getFullName());
        Label patientEmail = new Label("Email:\n" + getEmail(appt.getPatientID()));
        Label patientAddress = new Label("Address:\n" + getAddress(appt.getPatientID()));
        Label patientInsurance = new Label("Insurance:\n" + getInsurance(appt.getPatientID()));
        header.getChildren().addAll(patientName, patientEmail, patientAddress, patientInsurance);
        bp.setTop(header);
        //End Header

        //Center
        float paybox = 0;
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);

        VBox center = new VBox(grid);
        ScrollPane sp = new ScrollPane(center);
        String order[] = appt.getOrder().split(",");
        int counter = 0;
        for (int i = 0; i < order.length - 1; i++) {
            Label tempOrder = new Label(order[i].trim());
            Label tempCost = new Label("Hello");
            for (Order a : varName) {
                if (a.getOrder().equals(order[i].trim())) {
                    tempCost.setText(a.getCost() + "");
                    paybox += a.getCost();
                }
            }
            Label apptDate = new Label(appt.getTime().split(" ")[0]);
            grid.add(apptDate, 1, i);
            grid.add(tempOrder, 0, i);
            grid.add(tempCost, 2, i);
            counter = i;
        }
        counter++;
        ArrayList<Payment> payment = populatePayment(appt.getApptID());
        for (Payment p : payment) {
            Label byWhom = new Label("Patient Paid");
            if (p.getByPatient() == 0) {
                byWhom.setText("Insurance Paid");
            }
            Label tempPaymentDate = new Label(p.getTime());
            float num = -1 * p.getPayment();
            String positive = "";
            if (num > 0) {
                positive = "+";
            }
            Label tempPayment = new Label(positive + num);
            if (num > 0) {
                byWhom.setId("shadeRed");
                tempPaymentDate.setId("shadeRed");
                tempPayment.setId("shadeRed");
            } else {
                byWhom.setId("shadeGreen");
                tempPaymentDate.setId("shadeGreen");
                tempPayment.setId("shadeGreen");

            }

            grid.add(byWhom, 0, counter);
            grid.add(tempPaymentDate, 1, counter);
            grid.add(tempPayment, 2, counter);
            paybox -= p.getPayment();
            counter++;
        }
        bp.setCenter(sp);
        //End Center

        //Footer
        Button btn = new Button("Go Back");
        btn.setId("cancel");
        btn.setOnAction((ActionEvent eh) -> {
            x.close();
        });
        Button btn1 = new Button("Make Payment");
        btn1.setId("complete");
        btn1.setOnAction((ActionEvent eh) -> {
            makePayment(appt);
            x.close();
            viewBill(appt);
        });
        HBox footer = new HBox();
        Label blank = new Label("Total Bill Remaining: ");
        Label tc = new Label("" + paybox);

        footer.getChildren().addAll(btn, blank, tc, btn1);
        bp.setBottom(footer);
        //End Footer
        
        x.show();
    }

    private String getAddress(String patientID) {
        String sql = "SELECT address FROM patients WHERE patientID = '" + patientID + "';";

        String value = "";
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                value += rs.getString("address");
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    private String getEmail(String patientID) {
        String sql = "SELECT email FROM patients WHERE patientID = '" + patientID + "';";

        String value = "";
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                value += rs.getString("email");
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    private String getInsurance(String patientID) {
        String sql = "SELECT insurance FROM patients WHERE patientID = '" + patientID + "';";

        String value = "";
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                value += rs.getString("insurance");
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;
    }

    private ArrayList<Order> populateOrders() {
        String sql = "SELECT * FROM orderCodes;";

        ArrayList<Order> value = new ArrayList<>();
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Order order = new Order(rs.getString("orderID"), rs.getString("orders"));
                order.setCost(rs.getFloat("cost"));
                value.add(order);
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;

    }

    private ArrayList<Payment> populatePayment(String apptID) {
        String sql = "SELECT * FROM patientPayments WHERE apptID ='" + apptID + "';";

        ArrayList<Payment> value = new ArrayList<>();
        
        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Payment payment = new Payment(rs.getString("apptID"), rs.getString("time"), rs.getFloat("patientPayment"));
                payment.setByPatient(rs.getInt("byPatient"));
                value.add(payment);
            }

            rs.close();
            stmt.close();
            conn.close();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return value;

    }

    private void makePayment(Appointment appt) {
        Stage x = new Stage();
        VBox container = new VBox();
        Scene scene = new Scene(container);
        scene.getStylesheets().add("file:stylesheet.css");
        x.setScene(scene);

        HBox hello = new HBox();
        Label enterpay = new Label("Enter Payment Here");
        TextField ep = new TextField();
        ComboBox dropdown = new ComboBox();
        dropdown.getItems().addAll("Patient", "Insurance");
        dropdown.setValue("Patient");
        Button b = new Button("Submit");
        hello.getChildren().addAll(enterpay, ep, dropdown, b);
        container.getChildren().addAll(hello);
        b.setOnAction((ActionEvent eh) -> {
            if (!InputValidation.validatePayment(ep.getText())) {
                return;
            }
            String sql = "";
            if (dropdown.getValue().toString().equals("Patient")) {
                sql = "INSERT INTO patientPayments(apptID, time, patientPayment, byPatient) VALUES ('" + appt.getApptID() + "', '" + LocalDate.now() + "' , '" + ep.getText() + "', '1' )";
            } else {
                sql = "INSERT INTO patientPayments(apptID, time, patientPayment, byPatient) VALUES ('" + appt.getApptID() + "', '" + LocalDate.now() + "' , '" + ep.getText() + "', '0' )";
            }
            App.executeSQLStatement(sql);
            x.close();
        });
        x.showAndWait();
    }

    private void removeAppointment(Appointment appt) {
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the appointment?", "Remove Appointment", JOptionPane.DEFAULT_OPTION);
        // 0 = ok
        
        if (result == 0) {
        String sql = "UPDATE appointments SET viewable = 0 WHERE appt_id = '" + appt.getApptID() + "';";
        App.executeSQLStatement(sql);
        populateTable();
        }
    }

}
