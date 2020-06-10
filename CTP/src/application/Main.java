package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	
	public void start(Stage stage) throws IOException {
		BorderPane borderPane;
		stage.setTitle("Quixo");
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource("vue/menu.fxml"));
		borderPane = loader.load();
		
		Scene scene = new Scene(borderPane);
		stage.setScene(scene);
		stage.show();	
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
	
}
