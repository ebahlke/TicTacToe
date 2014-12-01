import java.util.*;

public class Player {
	private String playerName; // "X" or "O"
	private String playerType; // "human" or "computer"
	private TicTacToe context;
	private LinkedList<Integer> turnsTaken;

	// instance variables for computer players:
	private LinkedList<Strategy> allWinningStrats;
	private Strategy currentStrat;
	private boolean stratsAvailable;

	private static Random gen = new Random();
	private final Strategy NULL_STRAT = new Strategy();

	public Player(String name, String type, TicTacToe context) {
		if (!validPlayer(name, type))
			System.out.println("ERROR: Please enter one of X or O for the player name, and human or computer for player type.");
		else {
			this.context = context;
			playerName = name;
			playerType = type;
			turnsTaken = new LinkedList<Integer>();

			if (playerType.equalsIgnoreCase("computer"))
				computerInit();
		}
	}

	/* Sets/resets instance variables for the computer player: */
	public void computerInit() {
		allWinningStrats = context.winningStrats();
		if (allWinningStrats.size() > 0) {
			currentStrat = allWinningStrats.get(0);
			stratsAvailable = true;
		}
		else {
			currentStrat = NULL_STRAT;
			stratsAvailable = false;
		}
	}

	public void reset() {
		turnsTaken.clear();
		if (playerType.equalsIgnoreCase("computer"))
			computerInit();
	}
	
	/* Takes a move on the given board. */
	public Board takeTurn(Board onBoard) {
		int toPlay = chooseTurn(onBoard);
		onBoard.playerTakesTurn(this, toPlay);
		return onBoard;
	}
	
	/* Returns the square the player wants to play on the given board. */
	public int chooseTurn(Board onBoard) {
		int toPlay;
		
		if (playerType.equalsIgnoreCase("human"))
			toPlay = humanChooseMove(onBoard);
		else toPlay = computerChooseMove(onBoard);
		
		turnsTaken.add(toPlay);
		return toPlay;
	}
	
	/* Lets a human player choose the open square they want to play on the given board. */
	public int humanChooseMove(Board onBoard) {
		Scanner getInput = new Scanner(System.in);
		System.out.print("What square would you like to play?  Choose one of: " + onBoard.allOpenSquares());
		try {
			int toPlay = Integer.parseInt(getInput.next());
			if (!onBoard.allOpenSquares().contains(toPlay)) {
				System.out.println("ERROR: Please choose an open square.");
				return humanChooseMove(onBoard);
			}
			else return toPlay;
		} catch (NumberFormatException ex) {
			System.out.println("ERROR: Please enter an integer contained in the open squares.");
			return humanChooseMove(onBoard);
		}
	}
	
	/* The computer chooses the open square it wants to play on the given board by:
	 * 1) trying to play a known winning strategy;
	 * 1b) trying to sabotage its opponent's strategy while doing so;
	 * 2) failing that, trying to sabotage its opponent's strategy regardless of the success of its own. */
	public int computerChooseMove(Board onBoard) {
		if (currentStrat.isValid(onBoard, turnsTaken)) {
			return attemptTwoBirds(onBoard);
		}
		
		else if (validStratExists(onBoard)) { // note that validStratExists automatically sets currentStrat
			return attemptTwoBirds(onBoard);
		}
		
		else return playSabotage(onBoard);
	}
	
	/* The computer tries to see if there's any overlap between the known winning strategy it's playing,
	 * and the strategy it guesses that its opponent is playing.  If there is overlap, it selects this
	 * combined victory-furthering/sabotaging move. */
	private int attemptTwoBirds(Board onBoard) {
		Strategy twoBirds = isPlayingWinningStrat(onBoard, otherPlayer());
		if (twoBirds.isNullStrat()) {
			return currentStrat.getNextMove(onBoard);
		}
		else if (!currentStrat.overlaps(twoBirds)) {
			return currentStrat.getNextMove(onBoard);
		}
		else {
			return currentStrat.getOverlap(twoBirds);
		}
	}
	
	/* The computer cannot find a valid winning strategy that it can play itself, so it tries
	 * to sabotage its opponent's (apparent) strategy, if possible. */
	private int playSabotage(Board onBoard) {
		Strategy toSabotage = isPlayingWinningStrat(onBoard, otherPlayer());
		if (toSabotage.isNullStrat()) {
			return playStupid(onBoard);
		}
		else {
			return toSabotage.getNextMove(onBoard);
		}
	}
	
	/* Plays a "stupid" computer move, i.e. selects at random from the given list of open squares. */
	private int playStupid(Board onBoard) {
		LinkedList<Integer> validMoves = onBoard.allOpenSquares();
	    int index = gen.nextInt(validMoves.size());
	    return validMoves.get(index);
	}

	/* Determines whether or not any of the known winning strategies can be completed on the given board,
	 * taking into account the turns the player has already played. */
	private boolean validStratExists(Board onBoard) {
		if (!stratsAvailable)
			return false;

		else {
			for (int i = 0; i < allWinningStrats.size(); i++) {
				if (allWinningStrats.get(i).isValid(onBoard, turnsTaken)) {
					currentStrat = allWinningStrats.get(i);
					return true;
				}
			}
			
			stratsAvailable = false;
			return false;
		}
	}
	
	/* Determines whether or not a player seems to be playing (i.e. has partially completed) any
	 * of the known winning strategies on the given board. */
	private Strategy isPlayingWinningStrat(Board onBoard, Player p) {
		LinkedList<Integer> theirMoves = p.getMoves();

		if (theirMoves.size() == 0)
			return NULL_STRAT;

		for (int i = 0; i < allWinningStrats.size(); i++) {
			if (allWinningStrats.get(i).isValid(onBoard, otherPlayer().getMoves()))
				return allWinningStrats.get(i);
		}

		return NULL_STRAT;
	}
	
	/* *
	 * =============================================
	 * GETTERS, SETTERS, ETC
	 * =============================================
	 * */

	public Strategy getCurrentStrat() {
		return currentStrat;
	}

	public int turnsTaken() {
		return turnsTaken.size();
	}
	
	public String playerName() {
		return playerName;
	}

	public String playerType() {
		return playerType;
	}

	public LinkedList<Integer> getMoves() {
		return turnsTaken;
	}

	public String toString() {
		return "Player "+playerName;
	}

	public boolean equals(Player another) {
		return playerName.equals(another.playerName());
	}

	public Player otherPlayer() {
		LinkedList<Player> players = context.getPlayers();
		int myIndex = players.indexOf(this);
		return players.get((myIndex+1)%2);
	}

	private boolean validPlayer(String name, String type) {
		if ((!name.equals("X") && !name.equals("O")) ||
				(!type.equalsIgnoreCase("human") && !type.equalsIgnoreCase("computer")))
			return false;
		return true;
	}
}