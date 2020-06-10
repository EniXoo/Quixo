package application.controleur;

import application.modele.ModelFx;
import application.vue.Dialogues;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Controleur {

	private ModelFx modelFx;

	@FXML
	private Label joueurTourCourant;

	@FXML
	private Rectangle rect00; // Bord
	@FXML
	private Rectangle rect01; // Bord
	@FXML
	private Rectangle rect02; // Bord
	@FXML
	private Rectangle rect03; // Bord
	@FXML
	private Rectangle rect04; // Bord
	@FXML
	private Rectangle rect10; // Bord
	@FXML
	private Rectangle rect11;
	@FXML
	private Rectangle rect12;
	@FXML
	private Rectangle rect13;
	@FXML
	private Rectangle rect14; // Bord
	@FXML
	private Rectangle rect20; // Bord
	@FXML
	private Rectangle rect21;
	@FXML
	private Rectangle rect22;
	@FXML
	private Rectangle rect23;
	@FXML
	private Rectangle rect24; // Bord
	@FXML
	private Rectangle rect30; // Bord
	@FXML
	private Rectangle rect31;
	@FXML
	private Rectangle rect32;
	@FXML
	private Rectangle rect33;
	@FXML
	private Rectangle rect34; // Bord
	@FXML
	private Rectangle rect40; // Bord
	@FXML
	private Rectangle rect41; // Bord
	@FXML
	private Rectangle rect42; // Bord
	@FXML
	private Rectangle rect43; // Bord
	@FXML
	private Rectangle rect44; // Bord
	
	private Rectangle[] bords = new Rectangle[16];
	
	private Rectangle[] des = new Rectangle[25];
	
	private Node firstNode;
	
	@FXML
	private void initialize() {
		modelFx = new ModelFx();
		joueurTourCourant.setText("Joueur "+ modelFx.colorName(modelFx.player.get()) +" : à vous de jouer !");
		initialiseBords();
		initialiseDes();
		setAideChoix();
	}

	private void initialiseBords() {
		bords[0]=rect00;
		bords[1]=rect01;
		bords[2]=rect02;
		bords[3]=rect03;
		bords[4]=rect04;
		bords[5]=rect10;
		bords[6]=rect14;
		bords[7]=rect20;
		bords[8]=rect24;
		bords[9]=rect30;
		bords[10]=rect34;
		bords[11]=rect40;
		bords[12]=rect41;
		bords[13]=rect42;
		bords[14]=rect43;
		bords[15]=rect44;
	}
	private void initialiseDes() {
		//Grille
		des[0]=rect00;
		des[1]=rect01;
		des[2]=rect02;
		des[3]=rect03;
		des[4]=rect04;
		des[5]=rect10;
		des[6]=rect11;
		des[7]=rect12;
		des[8]=rect13;
		des[9]=rect14;
		des[10]=rect20;
		des[11]=rect21;
		des[12]=rect22;
		des[13]=rect23;
		des[14]=rect24;
		des[15]=rect30;
		des[16]=rect31;
		des[17]=rect32;
		des[18]=rect33;
		des[19]=rect34;
		des[20]=rect40;
		des[21]=rect41;
		des[22]=rect42;
		des[23]=rect43;
		des[24]=rect44;
	}

	
	@FXML
	private void onAPropos(ActionEvent actionEvent) {
		Dialogues.getAPropos().showAndWait();
	}

	@FXML
	private void onNouvelle(ActionEvent actionEvent) {
		if(Dialogues.confirmation("Nouvelle partie")) {
			modelFx.newGame();
			resetDes();
			resetAides();
			setAideChoix();
		}	
	}

	private void resetDes() {
		for(int i = 0; i < des.length; i++) {
			des[i].setFill(Color.BURLYWOOD);
		}
	}
	
	@FXML
	private void onQuitter(ActionEvent actionEvent) {
		if(Dialogues.confirmation("Quitter l'application"))
			System.exit(0);
	}

	@FXML
	private void partieFinie() {
		if(modelFx.wonBlue.get() && modelFx.wonRed.get()) { // Ajout pour gérer l'égalité le joueur doit réussir à faire perdre l'autre
			joueurTourCourant.setText("Joueur "+ modelFx.colorName(modelFx.player.get()) +" : à vous de jouer !");
		}
		if(modelFx.wonBlue.get()) {
			Dialogues.victoire("bleu");
			modelFx.newGame();
			resetDes();
			resetAides();
			setAideChoix();
		}
			
		else if(modelFx.wonRed.get()) {
			Dialogues.victoire("rouge");
			modelFx.newGame();
			resetDes();
			resetAides();
			setAideChoix();
		}
		else {
			joueurTourCourant.setText("Joueur "+ modelFx.colorName(modelFx.player.get()) +" : à vous de jouer !");
		}		
	}

	@FXML
	private void onPress(MouseEvent mouseEvent) {
		/* Source :
		 * https://stackoverflow.com/questions/50012463/how-can-i-click-a-gridpane-cell-and-have-it-perform-an-action
		 */
		Node clickedNode = mouseEvent.getPickResult().getIntersectedNode();
		int x = GridPane.getColumnIndex(clickedNode) != null ? GridPane.getColumnIndex(clickedNode) : 0;
		int y = GridPane.getRowIndex(clickedNode) != null ? GridPane.getRowIndex(clickedNode) : 0;
		if(modelFx.getChosenX() == null) {
			try {
				firstNode = clickedNode;
				modelFx.chooseDice(x, y);
				fadeTransition(firstNode);
				resetAides();
				setAideInsertion(x, y);
			} catch (IllegalArgumentException e) {
			}
		}
		else {
			try {
				modelFx.insertAt(x, y);
				fadeTransition(clickedNode);
				fadeTransitionReturn(firstNode);
				fadeTransitionReturn(clickedNode);
				miseAJourGrille();
				partieFinie();
				resetAides();
				setAideChoix();
			} catch (IllegalStateException e) {
			} catch (IllegalArgumentException e) {
			}
		}	
	}
	
	private void fadeTransition(Node node) {
		FadeTransition fading = new FadeTransition(Duration.seconds(0.4), node);
		fading.setFromValue(1.0);
		fading.setToValue(0.0);
		fading.play();
	}
	
	private void fadeTransitionReturn(Node node) {
		FadeTransition fading = new FadeTransition(Duration.seconds(0.6), node);
		fading.setFromValue(0.0);
		fading.setToValue(1.0);
		fading.play();
	}
	
	private void miseAJourGrille() {
		for(int x = 0; x < 5; x++) {
			for(int y = 0; y < 5; y++) {
				Paint couleur = modelFx.getDice(x, y).get().getFills().get(0).getFill();
				switch(x) {
				case 0:
					switch(y) {
					case 0:
						rect00.setFill(couleur);
						break;
					case 1:
						rect01.setFill(couleur);
						break;
					case 2:
						rect02.setFill(couleur);
						break;
					case 3:
						rect03.setFill(couleur);
						break;
					case 4:
						rect04.setFill(couleur);
						break;
					}	
				case 1:
					switch(y) {
					case 0:
						rect10.setFill(couleur);
						break;
					case 1:
						rect11.setFill(couleur);
						break;
					case 2:
						rect12.setFill(couleur);
						break;
					case 3:
						rect13.setFill(couleur);
						break;
					case 4:
						rect14.setFill(couleur);
						break;
					}
				case 2:
					switch(y) {
					case 0:
						rect20.setFill(couleur);
						break;
					case 1:
						rect21.setFill(couleur);
						break;
					case 2:
						rect22.setFill(couleur);
						break;
					case 3:
						rect23.setFill(couleur);
						break;
					case 4:
						rect24.setFill(couleur);
						break;
					}
				case 3:
					switch(y) {
					case 0:
						rect30.setFill(couleur);
						break;
					case 1:
						rect31.setFill(couleur);
						break;
					case 2:
						rect32.setFill(couleur);
						break;
					case 3:
						rect33.setFill(couleur);
						break;
					case 4:
						rect34.setFill(couleur);
						break;
					}
				case 4:
					switch(y) {
					case 0:
						rect40.setFill(couleur);
						break;
					case 1:
						rect41.setFill(couleur);
						break;
					case 2:
						rect42.setFill(couleur);
						break;
					case 3:
						rect43.setFill(couleur);
						break;
					case 4:
						rect44.setFill(couleur);
						break;
					}
				}
			}		
		}
	}
	
	private void setAideInsertion(int x, int y) {
		aideGauche(y);
		aideDroite(y);
		aideHaut(x);
		aideBas(x);
	}
	
	private void aideGauche(int y) {
		if(modelFx.isLeftPossible.get()) {
			switch(y) {
			case 0:
				rect00.setStroke(Color.GREEN);
				rect00.setStrokeWidth(3);
				break;
			case 1:
				rect01.setStroke(Color.GREEN);
				rect01.setStrokeWidth(3);
				break;
			case 2:
				rect02.setStroke(Color.GREEN);
				rect02.setStrokeWidth(3);
				break;
			case 3:
				rect03.setStroke(Color.GREEN);
				rect03.setStrokeWidth(3);
				break;
			case 4:
				rect04.setStroke(Color.GREEN);
				rect04.setStrokeWidth(3);
				break;
			}
		}
	}
	
	private void aideDroite(int y) {
		if(modelFx.isRightPossible.get()) {
			switch(y) {
			case 0:
				rect40.setStroke(Color.GREEN);
				rect40.setStrokeWidth(3);
				break;
			case 1:
				rect41.setStroke(Color.GREEN);
				rect41.setStrokeWidth(3);
				break;
			case 2:
				rect42.setStroke(Color.GREEN);
				rect42.setStrokeWidth(3);
				break;
			case 3:
				rect43.setStroke(Color.GREEN);
				rect43.setStrokeWidth(3);
				break;
			case 4:
				rect44.setStroke(Color.GREEN);
				rect44.setStrokeWidth(3);
				break;
			}
		}
	}
	
	private void aideHaut(int x) {
		if(modelFx.isTopPossible.get()) {
			switch(x) {
			case 0:
				rect00.setStroke(Color.GREEN);
				rect00.setStrokeWidth(3);
				break;
			case 1:
				rect10.setStroke(Color.GREEN);
				rect10.setStrokeWidth(3);
				break;
			case 2:
				rect20.setStroke(Color.GREEN);
				rect20.setStrokeWidth(3);
				break;
			case 3:
				rect30.setStroke(Color.GREEN);
				rect30.setStrokeWidth(3);
				break;
			case 4:
				rect40.setStroke(Color.GREEN);
				rect40.setStrokeWidth(3);
				break;
			}
		}
	}
	
	private void aideBas(int x) {
		if(modelFx.isBottomPossible.get()) {
			switch(x) {
			case 0:
				rect04.setStroke(Color.GREEN);
				rect04.setStrokeWidth(3);
				break;
			case 1:
				rect14.setStroke(Color.GREEN);
				rect14.setStrokeWidth(3);
				break;
			case 2:
				rect24.setStroke(Color.GREEN);
				rect24.setStrokeWidth(3);
				break;
			case 3:
				rect34.setStroke(Color.GREEN);
				rect34.setStrokeWidth(3);
				break;
			case 4:
				rect44.setStroke(Color.GREEN);
				rect44.setStrokeWidth(3);
				break;
			}
		}
	}
	
	private void resetAides() {
		for(int i = 0; i < bords.length; i++) {
			bords[i].setStroke(Color.BLACK);
			bords[i].setStrokeWidth(1);
		}
	}
	
	private void setAideChoix() {
		for(int x = 0; x < 5; x+=4) {
			for(int y = 0; y < 5; y++) {
				if(modelFx.isChooseAllowed(x, y)) {
					switch(x) {
					case 0:
						switch(y) {
						case 0:
							rect00.setStroke(Color.GREEN);
							rect00.setStrokeWidth(3);
							break;
						case 1:
							rect01.setStroke(Color.GREEN);
							rect01.setStrokeWidth(3);
							break;
						case 2:
							rect02.setStroke(Color.GREEN);
							rect02.setStrokeWidth(3);
							break;
						case 3:
							rect03.setStroke(Color.GREEN);
							rect03.setStrokeWidth(3);
							break;
						case 4:
							rect04.setStroke(Color.GREEN);
							rect04.setStrokeWidth(3);
							break;
						}	
						break;
					case 4:
						switch(y) {
						case 0:
							rect40.setStroke(Color.GREEN);
							rect40.setStrokeWidth(3);
							break;
						case 1:
							rect41.setStroke(Color.GREEN);
							rect41.setStrokeWidth(3);
							break;
						case 2:
							rect42.setStroke(Color.GREEN);
							rect42.setStrokeWidth(3);
							break;
						case 3:
							rect43.setStroke(Color.GREEN);
							rect43.setStrokeWidth(3);
							break;
						case 4:
							rect44.setStroke(Color.GREEN);
							rect44.setStrokeWidth(3);
							break;
						}
						break;
					}
				}
			}
		}
		for(int x = 1; x < 4; x++) {
			for(int y = 0; y < 5; y+=4) {
				if(modelFx.isChooseAllowed(x, y)) {
					switch(x) {
					case 1:
						switch(y) {
						case 0:
							rect10.setStroke(Color.GREEN);
							rect10.setStrokeWidth(3);
							break;
						case 4:
							rect14.setStroke(Color.GREEN);
							rect14.setStrokeWidth(3);
							break;
						}
						break;
					case 2:
						switch(y) {
						case 0:
							rect20.setStroke(Color.GREEN);
							rect20.setStrokeWidth(3);
							break;
						case 4:
							rect24.setStroke(Color.GREEN);
							rect24.setStrokeWidth(3);
							break;
						}
						break;
					case 3:
						switch(y) {
						case 0:
							rect30.setStroke(Color.GREEN);
							rect30.setStrokeWidth(3);
							break;
						case 4:
							rect34.setStroke(Color.GREEN);
							rect34.setStrokeWidth(3);
							break;
						}
						break;
					}
				}
			}
		}
	}
}