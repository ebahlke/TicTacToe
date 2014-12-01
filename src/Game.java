import java.util.*;

public class Game {
	private LinkedList<Player> players;
	private LinkedList<Integer> allWinnersMoves, allLosersMoves, drawTurns1, drawTurns2;
	private Player whoWon;
	private Player whoLost;
	private Player whoseTurn;
	private Board currentBoard;
	private boolean wasDraw;
	private final Strategy NULL_STRAT = new Strategy();

	public Game(LinkedList<Player> players) {
		this.players = players;
		currentBoard = new Board();
	}

	public void playRound() {
		players.get(0).reset();
		players.get(1).reset();

		while (!currentBoard.gameWon() && !currentBoard.gameDrawn()) {
			whoseTurn = players.removeFirst();
			players.addLast(whoseTurn);
			System.out.println(whoseTurn.toString() + " plays:");
			currentBoard = whoseTurn.takeTurn(currentBoard);
			System.out.println(currentBoard.toString());
		}

		if (currentBoard.gameWon()) {
			whoWon = whoseTurn;
			whoLost = players.get(0);
			allWinnersMoves = new LinkedList<Integer>();
			allWinnersMoves.addAll(whoWon.getMoves());
			allLosersMoves = new LinkedList<Integer>();
			allLosersMoves.addAll(whoLost.getMoves());

			// if the loser was attempting to play some known winning strat at the end of the game
			// and still got beaten, we mark this as an unfavourable strategy:
			if (whoLost.playerType().equalsIgnoreCase("computer") &&
					!whoLost.getCurrentStrat().equals(NULL_STRAT))
				whoLost.getCurrentStrat().augmentLosses();
		}
		else {
			wasDraw = true;
			drawTurns1 = new LinkedList<Integer>();
			drawTurns2 = new LinkedList<Integer>();
			drawTurns1.addAll(players.get(0).getMoves());
			drawTurns2.addAll(players.get(1).getMoves());
		}
	}
	
	public Player getLoser() {
		return whoLost;
	}

	public LinkedList<Integer> getWinningMoves() {
		return allWinnersMoves;
	}

	public LinkedList<Integer> getLosingMoves() {
		return allLosersMoves;
	}

	public LinkedList<Integer> getDrawTurns1() {
		return drawTurns1;
	}

	public LinkedList<Integer> getDrawTurns2() {
		return drawTurns2;
	}

	public boolean wasDraw() {
		return wasDraw;
	}
}