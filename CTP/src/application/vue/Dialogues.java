package application.vue;

import java.util.Optional;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Dialogues {

	public static Alert getAPropos() {
		Alert dialogue = new Alert(Alert.AlertType.INFORMATION);
		Label header = new Label("Quixo\nUn jeu Gigamic");
		header.setStyle("-fx-font-size: 48pt; -fx-text-fill: blue; -fx-font-weight: bolder; -fx-text-alignment: center");
		dialogue.getDialogPane().setHeader(header);
		HBox content = new HBox(new Label("Réalisé par LEBARBANCHON Valentin"));
		content.setAlignment(Pos.BOTTOM_CENTER);
		dialogue.getDialogPane().setContent(content);
		dialogue.setTitle("A propos");
		return dialogue;
	}

	public static boolean confirmation(String titre) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle(titre);
		alert.setHeaderText("Action critique");
		alert.setContentText("Vous êtes sur le point d'effectuer une action critique :\n - "
				+titre+"\n Veuillez confirmer ou non votre choix.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
		    return true;
		} else {
		    return false;
		}
	}
	
	public static Alert victoire(String gagnant) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Avertissement");
		alert.setHeaderText("Avertissement");
		alert.setContentText("Le joueur "+gagnant+" a gagné !");
		alert.showAndWait();
		return alert;
	}
}