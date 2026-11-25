package app;

import application.Main;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import users.Role;
import users.User;
import users.UserDatabase;

public class Main extends Application {

  private static User currentUser;

  private final Path userDbPath = Paths.get("users.dat");
  private UserDatabase userDatabase;

  public static User getCurrentUser() {
    return currentUser;
  }

  public static Role getCurrentRole() {
    return currentUser != null ? currentUser.getRole() : null;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    userDatabase = UserDatabase.loadOrCreate(userDbPath);
    showLoginScreen(primaryStage);
  }

  private void showLoginScreen(Stage stage) throws Exception {
    Parent fxmlRoot = FXMLLoader.load(Main.class.getResource("/application/logIn.fxml"));

    TextField emailField = (TextField) fxmlRoot.lookup("#emailField");
    PasswordField passField = (PasswordField) fxmlRoot.lookup("#passField");
    Button logButton = (Button) fxmlRoot.lookup("#logButton");
    Hyperlink createAccountButton = (Hyperlink) fxmlRoot.lookup("#createAccountButton");

    if (emailField != null) {
      emailField.setPromptText("Enter your email (e.g. juan@manager)");
    }
    if (passField != null) {
      passField.setPromptText("Enter your password");
    }

    if (logButton != null) {
      logButton.setOnAction(e -> {
        String email = emailField != null ? emailField.getText().trim() : "";
        String password = passField != null ? passField.getText() : "";

        if (email.isEmpty() || password.isEmpty()) {
          showAlert(AlertType.WARNING, "Please enter your email and password.");
          return;
        }

        if (!email.contains("@manager") && !email.contains("@clerk")) {
          showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
          return;
        }

        User authenticated = userDatabase.login(email, password);
        if (authenticated == null) {
          showAlert(AlertType.ERROR, "Invalid email or password.");
          return;
        }

        currentUser = authenticated;
        try {
          openAuthorizedEntry(stage, currentUser);
        } catch (Exception ex) {
          ex.printStackTrace();
          showAlert(AlertType.ERROR, "Could not open screen: " + ex.getMessage());
        }
      });
    }

    if (createAccountButton != null) {
      createAccountButton.setOnAction(e -> createAccountPopUp(stage));
    }

    Scene logScene = new Scene(fxmlRoot, 960, 683);
    if (Main.class.getResource("/application/logIn.css") != null) {
      logScene.getStylesheets().add(Main.class.getResource("/application/logIn.css").toExternalForm());
    }

    stage.setTitle("Log In to BukoKaDyan");
    stage.setScene(logScene);
    stage.show();
  }

  private void openAuthorizedEntry(Stage stage, User user) throws Exception {
    String fxml;
    String title;

    if (user.getRole() == Role.MANAGER) {
      fxml = "/application/DashboardScreen.fxml";
      title = "BukoKaDyan - Manager (" + user.getEmail() + ")";
    } else if (user.getRole() == Role.CLERK) {
      fxml = "/application/OrderScreen.fxml";
      title = "BukoKaDyan - Clerk (" + user.getEmail() + ")";
    } else {
      showAlert(AlertType.ERROR, "Unrecognized user role.");
      return;
    }

    Parent root = FXMLLoader.load(Main.class.getResource(fxml));
    Scene scene = new Scene(root);
    if (Main.class.getResource("/application/style.css") != null) {
      scene.getStylesheets().add(Main.class.getResource("/application/style.css").toExternalForm());
    }

    stage.setTitle(title);
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
  }

  private void createAccountPopUp(Stage ownerStage) {
    Stage createdStage = new Stage();
    createdStage.setTitle("Create an Account");
    createdStage.initOwner(ownerStage);
    createdStage.initModality(Modality.APPLICATION_MODAL);

    Label titleLabel = new Label("Create an Account");
    titleLabel.getStyleClass().add("popupTitle");

    Label nameLabel = new Label("Name");
    TextField nameField = new TextField();
    nameField.setPromptText("Enter your name (no @)");

    Label emailLabel = new Label("Email Tag / Role");
    TextField emailField = new TextField();
    emailField.setPromptText("Example: juan@manager");

    Label passLabel = new Label("Password");
    PasswordField passField = new PasswordField();
    passField.setPromptText("Enter your password");

    Button createButton = new Button("Create Account");
    createButton.setOnAction(e -> {
      String name = nameField.getText().trim();
      String email = emailField.getText().trim();
      String password = passField.getText();

      if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        showAlert(AlertType.WARNING, "Please fill in all the fields.");
        return;
      }

      Role role;
      if (email.contains("@manager")) {
        role = Role.MANAGER;
      } else if (email.contains("@clerk")) {
        role = Role.CLERK;
      } else {
        showAlert(AlertType.ERROR, "Email must contain @manager or @clerk.");
        return;
      }

      boolean added = userDatabase.addUser(name, password, role);
      if (!added) {
        showAlert(AlertType.ERROR, "Could not create account. Name may already exist or contain '@'.");
        return;
      }

      userDatabase.save(userDbPath);
      showAlert(AlertType.INFORMATION, "Account created for " + role + " " + name + "!");
      createdStage.close();
    });

    VBox popupBox = new VBox(
        10, titleLabel, nameLabel, nameField, emailLabel, emailField, passLabel, passField, createButton);
    popupBox.setPadding(new Insets(30));
    popupBox.getStyleClass().add("popupBox");

    Scene createScene = new Scene(popupBox, 380, 420);
    if (Main.class.getResource("/application/logIn.css") != null) {
      createScene.getStylesheets().add(Main.class.getResource("/application/logIn.css").toExternalForm());
    }

    createdStage.setScene(createScene);
    createdStage.showAndWait();
  }

  private void showAlert(AlertType type, String message) {
    Alert alert = new Alert(type);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
