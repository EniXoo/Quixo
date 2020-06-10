package application.modele;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ModelFx {
	/** The symbol for nobody's pieces */
	public static final Color neutral = Color.BURLYWOOD;
	/** The symbol for player red's pieces */
	public static final Color playerRed = Color.RED;
	/** The symbol for player blue's pieces */
	public static final Color playerBlue = Color.BLUE;

	/** The border for selectable dice */
	public static final Border selected = new Border(new BorderStroke(Color.MEDIUMSPRINGGREEN,BorderStrokeStyle.SOLID, new CornerRadii(1), BorderStroke.THICK));
	/** The border for unselectable dice */
	public static final Border unselected = new Border(new BorderStroke(null,null, null, null));

	/** Gets a printable String for the given color */
	public final String colorName(Color color) {
		if (neutral.equals(color)) {
			return "personne";
		} else if (playerBlue.equals(color)) {
			return "bleu";
		} else if (playerRed.equals(color)) {
			return "rouge";
		}
		return "inconnu";
	}
	/** Internal storage of current player */
	private final ReadOnlyObjectWrapper<Color> currentPlayer = new ReadOnlyObjectWrapper<>(playerRed);
	/** Internal storage of the other player */
	private final ReadOnlyObjectWrapper<Color> nextPlayer = new ReadOnlyObjectWrapper<>(playerBlue);
	/** Internal storage of chosen piece X-coordinate */
	private final SimpleObjectProperty<Integer> chosenX = new SimpleObjectProperty<>(null);
	/** Internal storage of chosen piece Y-coordinate */
	private final SimpleObjectProperty<Integer> chosenY = new SimpleObjectProperty<>(null);
	/** Internal storage of the board */
	@SuppressWarnings("unchecked")
	private final SimpleObjectProperty<Color>[][] dice = new SimpleObjectProperty[5][5];

	/** Public read-only view of the current player */
	public final ReadOnlyObjectProperty<Color> player = currentPlayer.getReadOnlyProperty();
	/** Public read-only view of the other player */
	public final ReadOnlyObjectProperty<Color> otherPlayer = nextPlayer.getReadOnlyProperty();

	/** Indicates whether inserting the chosen piece to the top of the board is legal or not */
	public final BooleanExpression isTopPossible = chosenX.isNotNull().and(chosenY.isNotEqualTo(0));
	/** Indicates whether inserting the chosen piece to the bottom of the board is legal or not */
	public final BooleanExpression isBottomPossible = chosenX.isNotNull().and(chosenY.isNotEqualTo(4));
	/** Indicates whether inserting the chosen piece to the left of the board is legal or not */
	public final BooleanExpression isLeftPossible = chosenX.isNotEqualTo(0); // if equals 0, then not null, of course
	/** Indicates whether inserting the chosen piece to the right of the board is legal or not */
	public final BooleanExpression isRightPossible = chosenX.isNotEqualTo(4); // if equals 4, then not null, of course

	/** Boolean intermediate expressions to compute the winner */
	private final BooleanExpression[] wonLineRed = new BooleanExpression[5];
	private final BooleanExpression[] wonColRed = new BooleanExpression[5];
	private final BooleanExpression[] wonLineBlue = new BooleanExpression[5];
	private final BooleanExpression[] wonColBlue = new BooleanExpression[5];
	private final BooleanExpression[] wonDiagRed = new BooleanExpression[2];
	private final BooleanExpression[] wonDiagBlue = new BooleanExpression[2];
	/** Indicates whether player X has won or not */
	public final BooleanExpression wonRed;
	/** Indicates whether player O has won or not */
	public final BooleanExpression wonBlue;

	/** Builds a new model for Quixo */
	public ModelFx() {
		// Builds the board with 5*5 neutral dice
		for (int y=0; y<5; ++y)
			for (int x=0; x<5; ++x) {
				dice[y][x] = new SimpleObjectProperty<>(neutral);
			}
		// Prepares the winning conditions
		wonDiagBlue[0] = dice[0][0].isEqualTo(playerBlue);
		wonDiagBlue[1] = dice[4][0].isEqualTo(playerBlue);
		wonDiagRed[0] = dice[0][0].isEqualTo(playerRed);
		wonDiagRed[1] = dice[4][0].isEqualTo(playerRed);
		for (int i=0; i<5; ++i) {
			wonColBlue[i] = dice[0][i].isEqualTo(playerBlue);
			wonColRed[i] = dice[0][i].isEqualTo(playerRed);
			wonLineBlue[i] = dice[i][0].isEqualTo(playerBlue);
			wonLineRed[i] = dice[i][0].isEqualTo(playerRed);
		}
		for (int i=1; i<5; ++i) {
			wonDiagBlue[0] = wonDiagBlue[0].and(dice[i][i].isEqualTo(playerBlue));
			wonDiagBlue[1] = wonDiagBlue[1].and(dice[4-i][i].isEqualTo(playerBlue));
			wonDiagRed[0] = wonDiagRed[0].and(dice[i][i].isEqualTo(playerRed));
			wonDiagRed[1] = wonDiagRed[1].and(dice[4-i][i].isEqualTo(playerRed));
			for (int j=0; j<5; ++j) {
				wonColBlue[j] = wonColBlue[j].and(dice[i][j].isEqualTo(playerBlue));
				wonColRed[j] = wonColRed[j].and(dice[i][j].isEqualTo(playerRed));
				wonLineBlue[j] = wonLineBlue[j].and(dice[j][i].isEqualTo(playerBlue));
				wonLineRed[j] = wonLineRed[j].and(dice[j][i].isEqualTo(playerRed));
			}
		}
		BooleanExpression wonRed = wonDiagRed[0].or(wonDiagRed[1]);
		BooleanExpression wonBlue = wonDiagBlue[0].or(wonDiagBlue[1]);
		for (int i=0; i<5; ++i) {
			wonRed = wonRed.or(wonColRed[i]).or(wonLineRed[i]);
			wonBlue = wonBlue.or(wonColBlue[i]).or(wonLineBlue[i]);
		}
		this.wonRed = wonRed;
		this.wonBlue = wonBlue;
	} // public ModelFx

	/** Copy the specified model */
	public ModelFx(ModelFx other) {
		this();
		for (int y=0; y<5; ++y)
			for (int x=0; x<5; ++x)
				this.dice[y][x].set(other.dice[y][x].get());
		if (this.currentPlayer.get() != other.currentPlayer.get())
			changePlayer();
	}

	/** Gets the current chosen piece X-coordinate. May be null if no piece is chosen. */
	public Integer getChosenX() {
		return chosenX.get();
	}

	/** Gets the current chosen piece Y-coordinate. May be null if no piece is chosen. */
	public Integer getChosenY() {
		return chosenY.get();
	}

	/** Gets a view of a single dice from its coordinates.
	 * @param x an integer from 0 (left) to 4 (right) representing the column index of the desired dice.
	 * @param y an integer from 0 (top) to 4 (bottom) representing the row index of the desired dice
	 */
	public ObjectBinding<Background> getDice(int x, int y) {
		return Bindings.createObjectBinding(()->new Background(new BackgroundFill(dice[y][x].get(),new CornerRadii(5),null)),dice[y][x]);
	}

	/** Switch players */
	private void changePlayer() {
		Color tmp = currentPlayer.get();
		currentPlayer.set(nextPlayer.get());
		nextPlayer.set(tmp);
	}

	/** Starts a new game. Puts all pieces to neutral and draws the starting player randomly */
	public void newGame() {
		for (int y=0; y<5; ++y)
			for (int x=0; x<5; ++x)
				dice[y][x].set(neutral);

		chosenX.set(null);
		chosenY.set(null);
		if (Math.random()>0.5) {
			changePlayer();
		}
	} // public newGame

	/** Indicates whether the specified piece can be chosen to be moved
	 * @param x an integer from 0 (left) to 4 (right) representing the column index of the desired dice.
	 * @param y an integer from 0 (top) to 4 (bottom) representing the row index of the desired dice
	 */
	public boolean isChooseAllowed(int x, int y) {
		return (chosenX.get() == null) // no piece chosen yet
				&& (x == 0 || x == 4 || y == 0 || y == 4) // on the side of the board
				&& (dice[y][x].get() != nextPlayer.get()); // neutral or current player's piece
	}

	/** Chooses the specified piece to be moved
	 * @param x an integer from 0 (left) to 4 (right) representing the column index of the desired dice.
	 * @param y an integer from 0 (top) to 4 (bottom) representing the row index of the desired dice
	 * @throws IllegalArgumentException if specified piece is not legal.
	 */
	public void chooseDice(int x, int y) {
		if (isChooseAllowed(x,y)) {
			chosenX.set(x);
			chosenY.set(y);
		} else
			throw new IllegalArgumentException("This piece is not choosable");
	}

	/** Cancels the choice of piece to move */
	public void cancelChoice() {
		chosenX.set(null);
		chosenY.set(null);
	}

	/** Inserts the chosen piece to the top of the board.
	 * Pieces are shifted down until the hole is filled.
	 * @throws IllegalStateException if no piece is chosen
	 * @throws IllegalArgumentException if the piece would be inserted back at its own place
	 */
	public void insertTop() {
		if (chosenX.get() == null) throw new IllegalStateException("You have first to choose a piece to move!");
		if (chosenY.get() == 0) throw new IllegalArgumentException("You cannot put the piece where it was!");
		int index = chosenX.get();
		// first shift existing values
		for (int y = chosenY.get(); y > 0; --y) {
			dice[y][index].set(dice[y-1][index].get()); //copy up-value into the hole
		}
		// then set the border value to current player
		dice[0][index].set(currentPlayer.get());
		cancelChoice();
		changePlayer();
	}

	/** Inserts the chosen piece to the bottom of the board.
	 * Pieces are shifted up until the hole is filled.
	 * @throws IllegalStateException if no piece is chosen
	 * @throws IllegalArgumentException if the piece would be inserted back at its own place
	 */
	public void insertBottom() {
		if (chosenX.get() == null) throw new IllegalStateException("You have first to choose a piece to move!");
		if (chosenY.get() == 4) throw new IllegalArgumentException("You cannot put the piece where it was!");
		int index = chosenX.get();
		// first shift existing values
		for (int y = chosenY.get(); y < 4; ++y) {
			dice[y][index].set(dice[y+1][index].get()); //copy down-value into the hole
		}
		// then set the border value to current player
		dice[4][index].set(currentPlayer.get());
		cancelChoice();
		changePlayer();
	}

	/** Inserts the chosen piece to the left of the board.
	 * Pieces are shifted right until the hole is filled.
	 * @throws IllegalStateException if no piece is chosen
	 * @throws IllegalArgumentException if the piece would be inserted back at its own place
	 */
	public void insertLeft() {
		if (chosenX.get() == null) throw new IllegalStateException("You have first to choose a piece to move!");
		if (chosenX.get() == 0) throw new IllegalArgumentException("You cannot put the piece where it was!");
		int index = chosenY.get();
		// first shift existing values
		for (int x = chosenX.get(); x > 0; --x) {
			dice[index][x].set(dice[index][x-1].get());//copy left-value into the hole
		}
		// then set the border value to current player
		dice[index][0].set(currentPlayer.get());
		cancelChoice();
		changePlayer();
	}

	/** Inserts the chosen piece to the right of the board.
	 * Pieces are shifted left until the hole is filled.
	 * @throws IllegalStateException if no piece is chosen
	 * @throws IllegalArgumentException if the piece would be inserted back at its own place
	 */
	public void insertRight() {
		if (chosenX.get() == null) throw new IllegalStateException("You have first to choose a piece to move!");
		if (chosenX.get() == 4) throw new IllegalArgumentException("You cannot put the piece where it was!");
		int index = chosenY.get();
		// first shift existing values
		for (int x = chosenX.get(); x < 4; ++x) {
			dice[index][x].set(dice[index][x+1].get());//copy right-value into the hole
		}
		// then set the border value to current player
		dice[index][4].set(currentPlayer.get());
		cancelChoice();
		changePlayer();
	}

	/** Inserts the chosen piece to specified coordinates.
	 * Pieces are shifted below until the hole is filled.
	 * @throws IllegalStateException if no piece is chosen
	 * @throws IllegalArgumentException if the insertion is not legal
	 */
	public void insertAt(int x, int y) {
		if (chosenX.get() == null) throw new IllegalStateException("You have first to choose a piece to move!");
		if (chosenX.get() == x)
			if (chosenY.get() == y) throw new IllegalArgumentException("You cannot put the piece where it was!");
			else if (y == 0) insertTop();
			else if (y == 4) insertBottom();
			else throw new IllegalArgumentException("You have to place your piece at one end of the column!");
		else if (chosenY.get() == y)
			if (x == 0) insertLeft();
			else if (x == 4) insertRight();
			else throw new IllegalArgumentException("You have to place your piece at one end of the row!");
		else throw new IllegalArgumentException("You have to play on the same row/column as the piece you chose!");
	}
} // public class ModelFx

