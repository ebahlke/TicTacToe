import java.util.*;

public class Board {
	private String[][] board;
	private Hashtable<Integer, Integer[]> namedSquares;
	// ^ allows lookup from square-numberings (i.e. top left is square #1) to coordinates in the board double-array
	private final Integer[][] ALL_LINES = {{1, 5, 9}, {3, 5, 7}, {1, 2, 3}, {4, 5, 6}, {7, 8, 9},
			{1, 4, 7}, {2, 5, 8}, {3, 6, 9}};
	// ^ all rows, cols, diags etc. in square-name format
	private int SIZE = 3;

	public Board() {
		board = new String[SIZE][SIZE];
		Integer[] squareNames = new Integer[SIZE*SIZE];
		for (int i = 0; i < SIZE*SIZE; i++)
			squareNames[i] = i+1;
		LinkedList<Integer[]> coordinatePairs = new LinkedList<Integer[]>();

		// attach each "square name" (square 1, 2, etc.) to a coordinate pair
		// that can be used to look the corresponding square up in the board double-array:
		for (int j = 0; j < SIZE; j++) {
			for (int k = 0; k < SIZE; k++) {
				Integer[] toAdd = new Integer[2];
				toAdd[0] = j;
				toAdd[1] = k;
				coordinatePairs.add(toAdd);
			}
		}

		namedSquares = new Hashtable<Integer, Integer[]>(SIZE*SIZE);
		for (int l = 0; l < SIZE*SIZE; l++)
			namedSquares.put(squareNames[l], coordinatePairs.get(l));
	}

	/* Creates a board object where something has already happened on the board. */
	public Board(String[][] board) {
		this();
		this.board = board;
	}

	public void playerTakesTurn(Player p, int plays) {
		Integer[] coordinates = namedSquares.get(plays);
		board[coordinates[0]][coordinates[1]] = p.playerName();
	}

	public boolean gameWon() {
		for (int i = 0; i < ALL_LINES.length; i++) {
			if (allFilled(ALL_LINES[i]))
				return true;
		}
		return false;
	}

	public boolean gameDrawn() {
		return (!gameWon() && (allOpenSquares().size() == 0));
	}

	public LinkedList<Integer> allOpenSquares() {
		LinkedList<Integer> openSquares = new LinkedList<Integer>();

		for (int i = 1; i <= namedSquares.size(); i++) {
			Integer[] coordinates = namedSquares.get(i);
			String currentSquare = board[coordinates[0]][coordinates[1]];
			if (currentSquare == null)
				openSquares.add(i);
		}

		return openSquares;
	}

	/* Checks to see if every square (here, each int represents a square name)
	 * in the given list is filled BY THE SAME PLAYER. */
	private boolean allFilled(Integer[] line) {
		String firstNameFound = "";

		for (int i = 0; i < line.length; i++) {
			Integer[] currentCoordinates = namedSquares.get(line[i]);
			String currentSquare = board[currentCoordinates[0]][currentCoordinates[1]];
			if (currentSquare == null)
				return false;
			else if (firstNameFound.equals(""))
				firstNameFound = currentSquare;
			else if (!currentSquare.equals(firstNameFound))
				return false;
		}

		return true;
	}


	public String toString() {
		String s = "";

		for (int i = 0; i < 3; i++)
			s+=toString(board[0][i]);
		s+="\n";
		for (int j = 0; j < 3; j++)
			s+=toString(board[1][j]);
		s+="\n";
		for (int k = 0; k < 3; k++)
			s+=toString(board[2][k]);
		return s;
	}

	public String toString(String s) {
		if (s == null)
			return "  ";
		else return s+" ";
	}

	public String[][] getBoard() {
		return board;
	}

	public static void main(String[] args) {
		//    System.out.println(t.allOpenSquares());
		//    TicTacToe ttt = new TicTacToe("human", "computer");
		//    //Player p = new Player("X", "human", new TicTacToe("human", "computer"));
		//    t.takeTurn(p, 6);
		//    System.out.println(t.allOpenSquares());
		//    t.takeTurn(p, 5);
		//    t.takeTurn(p, 4);
		//    System.out.println(t.gameWon());
		//    System.out.println(t.toString());
		//    Board s = new Board(0);
		//    Player one = new Player("X", "computer", new TicTacToe("computer", "computer"));
		//    Player two = new Player("O", "computer", new TicTacToe("computer", "computer"));
		//    s.takeTurn(two, 7);
		//    System.out.println(s.allOpenSquares());
		//    LinkedList<Integer> hm = new LinkedList<Integer>();
		//    hm.add(7);
		//    hm.add(3);
		//    hm.add(5);
		//    Strategy x = new Strategy(hm);
		//    System.out.println(x.isValid(s));
	}
}