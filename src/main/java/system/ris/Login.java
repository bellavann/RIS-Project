package system.ris;

import static system.ris.App.ds;
import static system.ris.App.url;
import datastorage.InputValidation;
import datastorage.Patient;
import datastorage.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Login extends Stage {
    
    //Creating all individual elements in scene
    //Create Username/Password Label and Textbox 

    private Label textTitle = new Label("User Login");
    private Label textUsername = new Label("Enter your Username:");
    private TextField inputUsername = new TextField("here");
    private Label textPassword = new Label("Enter your Password:");
    private PasswordField inputPassword = new PasswordField();
    
    private Label textTitle2 = new Label("Patient Login");
    private Label textUsername2 = new Label("Enter your Username:");
    private TextField inputUsername2 = new TextField("here");
    
    //Create Login Button. Logic for Button at End.
    private Button btnLogin = new Button("Login");
    private Button btnLogin2 = new Button("Login");
    private GridPane grid = new GridPane();
    VBox center = new VBox();
    Scene scene = new Scene(center, 1000, 1000);

    //Constructor. Displays view
    Login() {
        //Setting the Title
        this.setTitle("RIS- Radiology Information System (Logging In)");
        
        //Edit gridPane to look better
        changeGridPane();
        
        //ON button click
        btnLogin.setOnAction((ActionEvent e) -> {
            loginCheck();
        });
        btnLogin2.setOnAction((ActionEvent e) -> {
            patientLoginCheck();
        });
        
        inputUsername.setId("textfield");
        inputPassword.setId("textfield");
        inputUsername2.setId("textfield");
        center.setId("loginpage");
        center.setSpacing(10);
        
        try {
            
            //Setting the logo
            FileInputStream file = new FileInputStream("logo.png");
            Image logo = new Image(file);
            ImageView logoDisplay = new ImageView(logo);
            center.setAlignment(Pos.CENTER);
            center.getChildren().addAll(logoDisplay, grid);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Setting scene appropriately
        scene.getStylesheets().add("file:stylesheet.css");
        this.setScene(scene);
        this.setMaximized(true);
        this.show();
        connectToDatabase();
    }

    private void changeGridPane() {
        //Gridpane does what Gridpane does best
        //Everything's on a grid. 
        //Follows-> Column (x), then Row (y)
        grid.setAlignment(Pos.CENTER_LEFT);
        GridPane.setConstraints(textTitle, 115, 1);
        GridPane.setConstraints(textUsername, 115, 2);
        GridPane.setConstraints(inputUsername, 116, 2);
        GridPane.setConstraints(textPassword, 115, 4);
        GridPane.setConstraints(inputPassword, 116, 4);
        GridPane.setConstraints(btnLogin, 115, 5, 3, 2);
        
        GridPane.setConstraints(textTitle2, 160, 1);
        GridPane.setConstraints(textUsername2, 160, 2);
        GridPane.setConstraints(inputUsername2, 161, 2);
        GridPane.setConstraints(btnLogin2, 160, 3, 3, 2);
        grid.setPadding(new Insets(5));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.getChildren().addAll(textTitle, textUsername, inputUsername, textPassword, inputPassword, btnLogin, textTitle2, textUsername2, inputUsername2, btnLogin2);
        
    }

    /*
        loginCheck()
        Checks user inputted username/password
        Gets user, sets local user
        Opens new stage for user's role
     */
    private void loginCheck() {
        String username = inputUsername.getText();
        String password = inputPassword.getText();

        if (!InputValidation.validateUsername(username)) {
            return;
        }

        if (!InputValidation.validatePassword(password)) {
            return;
        }

        String sql = "Select * FROM users WHERE username = '" + username + "' AND password = '" + password + "' AND enabled = true;";

        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String fullName = rs.getString("full_name");
                int role = rs.getInt("role");
                App.user = new User(userId, fullName, role);
                App.user.setEmail(rs.getString("email"));
                App.user.setUsername(rs.getString("username"));
                try {
                    App.user.setPfp(new Image(new FileInputStream(App.imagePathDirectory + rs.getString("pfp"))));
                } catch (FileNotFoundException ex) {
                    App.user.setPfp(null);
                }
            }

            rs.close();
            stmt.close();
            conn.close();

            if (App.user.getRole() == 0) {
                throw new SQLException("Invalid Username / Password");
            }
            if (App.user.getRole() == 2) {
                //Receptionist
                Stage x = new Receptionist();
                x.show();
                x.setMaximized(true);
                this.hide();
            } else if (App.user.getRole() == 3) {
                //technician
                Stage x = new Technician();
                x.show();
                x.setMaximized(true);
                this.hide();
            } else if (App.user.getRole() == 4) {
                //radiologist
                Stage x = new Rad();
                x.show();
                x.setMaximized(true);
                this.hide();
            } else if (App.user.getRole() == 5) {
                //Referral Doctor
                Stage x = new ReferralDoctor();
                x.show();
                x.setMaximized(true);
                this.hide();
            } else if (App.user.getRole() == 6) {
                Stage x = new Billing();
                x.show();
                x.setMaximized(true);
                this.hide();
            } else if (App.user.getRole() == 1) {
                Stage x = new Administrator();
                x.show();
                x.setMaximized(true);
                this.hide();
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Error");
            if (e.getMessage().contains("password authentication failed")) {
                a.setHeaderText("Try Again");
                a.setContentText("URL Username/Password incorrect. Please correct the url.\n(Restart the Program)");
                File credentials = new File("../credentials.ris");
                credentials.delete();
            } else {
                a.setHeaderText("Try Again");
                a.setContentText("Username / Password not found. \nPlease contact an administrator if problem persists. ");
            }
            a.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Error");
            a.setHeaderText("Try Again");
            a.setContentText("Username / Password not found. \nPlease contact an administrator if problem persists. ");
            a.show();
        }
    }
    
    private void patientLoginCheck() {
        String username = inputUsername2.getText();

        if (!InputValidation.validateUsername(username)) {
            return;
        }

        String sql = "Select docPatientConnector.patientID, patients.email, patients.full_name, patients.username, patients.dob, patients.address, patients.insurance"
                + " FROM docPatientConnector"
                + " INNER JOIN patients ON docPatientConnector.patientID = patients.patientID"
                + " WHERE patients.email = '" + username + "';";

        try {
            
            Connection conn = ds.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String userId = rs.getString("patientID");
                String fullName = rs.getString("full_name");
                App.user = new User(userId, fullName, 7);
                App.user.setEmail(rs.getString("email"));
                App.user.setUsername(rs.getString("username"));
                
            }

            rs.close();
            stmt.close();
            conn.close();
            
            
            if (App.user == null) {
                throw new SQLException("Invalid Username / Password");
            }
            else {
                Stage x = new PatientUser();
                x.show();
                x.setMaximized(true);
                this.hide();
            }
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Error");
            if (e.getMessage().contains("password authentication failed")) {
                a.setHeaderText("Try Again");
                a.setContentText("URL Username/Password incorrect. Please correct the url.\n(Restart the Program)");
                File credentials = new File("../credentials.ris");
                credentials.delete();
            } else {
                a.setHeaderText("Try Again");
                a.setContentText("Username / Password not found. \nPlease contact an administrator if problem persists. ");
            }
            a.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Error");
            a.setHeaderText("Try Again");
            a.setContentText("Username / Password not found. \nPlease contact an administrator if problem persists. ");
            a.show();
        }
    }

    //Checks for valid database connection
    private void connectToDatabase() {
        try {

            File credentials = new File("../credentials.ris");
            if (!credentials.exists()) {
                credentials.createNewFile();
                Stage x = new Stage();
                x.initOwner(this);
                x.initModality(Modality.WINDOW_MODAL);
                BorderPane y = new BorderPane();
                Scene z = new Scene(y);
                z.getStylesheets().add("file:stylesheet.css");
                x.setScene(z);
                Text text = new Text("Insert URL");
                TextArea area = new TextArea();
                Button submit = new Button("Submit");
                HBox container = new HBox(text, area, submit);
                y.setCenter(container);
                x.show();
                submit.setOnAction((ActionEvent eh) -> {
                    if (!area.getText().isEmpty()) {
                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(credentials);
                            byte[] strToBytes = area.getText().getBytes();
                            outputStream.write(strToBytes);
                            App.url = area.getText();
                            ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalArgumentException ex) {
                            credentials.delete();
                            Alert a = new Alert(AlertType.INFORMATION);
                            a.setTitle("Error");
                            a.setHeaderText("URL Invalid");
                            a.setContentText("URL Invalid. Please contact your Administrator. (Restart Application)");
                            a.showAndWait();
                            
                        } finally {
                            try {
                                outputStream.close();
                            } catch (IOException ex) {
                                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        x.close();
                    }
                });
            } else {
                FileReader fr = new FileReader(credentials);    //reads the file  
                BufferedReader br = new BufferedReader(fr);     //creates a buffering character input stream  
                StringBuffer sb = new StringBuffer();           //constructs a string buffer with no characters  
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);    //appends line to string buffer  
                }
                fr.close();     //closes the stream and release the resources
                try {
                    
                    App.url = sb.toString();
                    ds.setUrl(Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("JDBC_DATABASE_URL is not set.")));
                
                } catch (IllegalArgumentException ex) {
                    credentials.delete();
                    Alert a = new Alert(AlertType.INFORMATION);
                    a.setTitle("Error");
                    a.setHeaderText("URL Invalid");
                    a.setContentText("URL Invalid. Please contact your Administrator. (Restart Application)");
                    a.showAndWait();
                }

            }
            
        } catch (IOException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
