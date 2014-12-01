import java.util.*;

public class TicTacToe {
	private LinkedList<Player> players;
	private LinkedList<Game> pastGames;
	private LinkedList<Strategy> allWinningStrats;
	private LinkedList<Strategy> allLosingStrats;
	private static int testSize = 20;

	public TicTacToe(String playerType1, String playerType2) {
		allWinningStrats = new LinkedList<Strategy>();
		players = new LinkedList<Player>();
		pastGames = new LinkedList<Game>();
		Player one = new Player("X", playerType1, this);
		Player two = new Player("O", playerType2, this);

		players.add(one);
		players.add(two);
	}
	
	public void playGame() {
		Game g = new Game(players);
		g.playRound();
		pastGames.add(g);
		if (g.wasDraw()) {
			Strategy draw1 = new Strategy(g.getDrawTurns1(), true);
			Strategy draw2 = new Strategy(g.getDrawTurns2(), true);
			addOrAugment(draw1, draw2);
		}
		else {
			Strategy nextWinner = new Strategy(g.getWinningMoves(), false);
			addOrAugment(nextWinner);
		}
		allWinningStrats = reorganizeStrats(allWinningStrats);
	}

	/* Adds only unique/newly-discovered winning strategies to allWinningStrats, whereas
	 * if a strategy is already contained, it augments that strategy's "wins" instance variable
	 * (note that if the strategy is a draw strategy, the call to augmentWins() does nothing). */
	public void addOrAugment(Strategy ... strats) {
		for (int i = 0; i < strats.length; i++) {
			if (!contains(allWinningStrats, strats[i]))
				allWinningStrats.add(strats[i]);
			else {
				int index = findIndex(allWinningStrats, strats[i]);
				allWinningStrats.get(index).augmentWins();
			}
		}
	}

	/* Prunes a list of strategies so that it does not contain longer/overgrown copies
	 * (i.e. does not contain both [1 5 3 7] and [3 5 7])
	 * of any strategy and calls a helper to store them in order of their overall wins-losses score. */
	private LinkedList<Strategy> reorganizeStrats(LinkedList<Strategy> strats) {
		int index = 0;

		while (index < strats.size()) {
			if (containsParedVersion(strats, strats.get(index)))
				strats.remove(index);
			else
				index++;
		}
		strats = rankByScore(strats);
		return strats;
	}

	/* Sorts a list of strategies into descending order by their overall wins-losses scores. */
	private LinkedList<Strategy> rankByScore(LinkedList<Strategy> list) {
		if ((list.size() == 1) || (list.size() == 0))
			return list;
		else {
			int pivot = list.get(0).getScore();
			LinkedList<Strategy> smaller = new LinkedList<Strategy>();
			LinkedList<Strategy> larger = new LinkedList<Strategy>();

			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).getScore() >= pivot)
					larger.add(list.get(i));
				else smaller.add(list.get(i));
			}

			smaller = rankByScore(smaller);
			larger = rankByScore(larger);

			larger.add(list.get(0));
			larger.addAll(smaller);
			return larger;
		}
	}

	public LinkedList<Strategy> winningStrats() {
		return allWinningStrats;
	}

	/* Returns a true copy of allWinningStrats. */
	public LinkedList<Strategy> winningStratsCopy() {
		LinkedList<Strategy> toReturn = new LinkedList<Strategy>();

		for (int i = 0; i < allWinningStrats.size(); i++)
			toReturn.add(allWinningStrats.get(i).clone());

		return toReturn;
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

	/* Checks to see if a list of strategies contains a shorter version of a given strategy s.
	 * Note: In the Strategy method isParedVersion, there's a check in place to distinguish between
	 * draw and non-draw strategies. Note that someone may cause a draw by playing [5 1 3 8] and win by
	 * playing [5 1 3 8 2].  We shouldn't remove the winning strategy [5 1 3 8 2] as an "overgrown" version
	 * of [5 1 3 8], since it's a distinct strategy in its own right. */
	private boolean containsParedVersion(LinkedList<Strategy> list, Strategy s) {
		for (int i = 0; i < list.size(); i++) {
			if (s.isParedVersionOf(list.get(i)))
				return true;
		}
		return false;
	}
	
	/* =============
	 * Helpers
	 * ============= */

	/* Alternate findIndex and contains methods to ensure usage of Strategy's equals method
	 * (and to recognize [2, 1, 3] and [1, 2, 3] as the same strategy, for instance): */
	private int findIndex(LinkedList<Strategy> list, Strategy s) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).equals(s)) return i;
		return -1;
	}

	private boolean contains(LinkedList<Strategy> list, Strategy s) {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).equals(s)) return true;
		return false;
	}

	public static void main(String[] args) {
	}
}
