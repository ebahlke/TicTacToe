import java.util.*;

public class Strategy {
	private LinkedList<Integer> strat;
	private boolean IS_NULL_STRAT;
	private boolean isDrawStrat;
	private int wins;
	private int losses;

	public Strategy(LinkedList<Integer> toPlay, Boolean drawStrat) {
		strat = toPlay;
		isDrawStrat = drawStrat;
		IS_NULL_STRAT = false;
		if (isDrawStrat) {
			wins = 0;
			losses = 0;
		}
		else {
			wins = 1;
			losses = 0;
		}
	}

	/* Quicker constructor for testing purposes: */
	public Strategy(int ... toPlay) {
		strat = new LinkedList<Integer>();
		for (int i = 0; i < toPlay.length; i++)
			strat.add(toPlay[i]);
		IS_NULL_STRAT = false;
	}

	/* Constructor for creating the null strat: */
	public Strategy() {
		IS_NULL_STRAT = true;
	}

	/* Checks to see if a strategy is valid on a given board.  It takes the squares
	 * already played as an argument because of the way computer players take their
	 * turns - sometimes playing the strategy out of order in order to attempt sabotage, etc. -
	 * so we cannot just start from a given index and check the remaining squares in the strat.
	 * Instead we need to clone the strategy and filter out the moves already taken. Otherwise some
	 * squares in the strategy will not be contained in the board's allOpenSquares(), having
	 * already been played by the player calling isValid.
	 */
	public boolean isValid(Board onBoard, LinkedList<Integer> turnsTaken) {
		if (IS_NULL_STRAT || properlyContains(turnsTaken, this.strat))
			return false;
		else {
			Strategy movesRemaining = clone();
			movesRemaining.filterMoves(turnsTaken);
			
			if (properlyContains(onBoard.allOpenSquares(), movesRemaining.getStrat()))
				return true;
			else return false;
		}
	}

	/* Removes any overlapping entries in the alreadyPlayed list from the strats list. */
	public void filterMoves(LinkedList<Integer> alreadyPlayed) {
		for (int i = 0; i < alreadyPlayed.size(); i++) {
			if (strat.contains(alreadyPlayed.get(i)))
				strat.removeFirstOccurrence(alreadyPlayed.get(i));
		}
	}

	/* Returns the next unplayed move in this strategy. */
	public int getNextMove(Board onBoard) {
		if (size() == 0)
			return -1;
		else {
			LinkedList<Integer> openSquares = onBoard.allOpenSquares();
			for (int i = 0; i < openSquares.size(); i++) {
				if (strat.contains(openSquares.get(i)))
					return openSquares.get(i);
			}
			return -1;
		}
	}

	/* Determines whether or not two strategies have any point of overlap/
	 * moves in common: */
	public boolean overlaps(Strategy another) {
		for (int i = 0; i < another.size(); i++) {
			if (strat.contains(another.getMoveAt(i)))
				return true;
		}
		return false;
	}

	/* Returns the overlap between two strategies, or -1 if no overlap exists: */
	public int getOverlap(Strategy another) {
		if (!overlaps(another))
			return -1;
		else {
			int index = 0;
			while (!strat.contains(another.getMoveAt(index)))
				index++;
			return another.getMoveAt(index);
		}
	}
	
	/* =====================================================================================================
	 * VERSION 2 METHODS:
	 * ===================================================================================================== */
	
	public int getMoveAt(int turnNumber) {
		if (IS_NULL_STRAT || turnNumber >= strat.size())
			return -1;
		else return strat.get(turnNumber);
	}
	
	/* =====================================================================================================
	 * GENERIC METHODS:
	 * ===================================================================================================== */
	
	public String toString() {
		if (IS_NULL_STRAT)
			return "NULL STRAT";
		else return (strat.toString() + " (wins " + wins + " losses " + losses +")");
	}
	
	public boolean isNullStrat() {
		return IS_NULL_STRAT;
	}

	public boolean isDrawStrat() {
		return isDrawStrat;
	}
	
	/* Returns whether or not THIS strategy is a pared-down version of the OTHER one,
	 * i.e. the other strategy contains this one. We stipulate that both strategies must
	 * of the same type in terms of winning or draw. */
	public boolean isParedVersionOf(Strategy another) {
		if ((this.isDrawStrat && !another.isDrawStrat()) ||
				(!this.isDrawStrat && another.isDrawStrat()))
			return false;

		if (this.size() >= another.size())
			return false;
		
		else {
			for (int i = 0; i < this.size(); i++) {
				if (!another.getStrat().contains(this.getMoveAt(i)))
					return false;
			}
			return true;
		}
	}
	
	/* Equals method. Equality of two strategies is not effected by the order
	 * of moves within the strategy, so [1, 2, 3] is the same as [3, 1, 2]. */
	public boolean equals(Strategy another) {
		if (another.isNullStrat())
			return IS_NULL_STRAT;

		LinkedList<Integer> otherStrat = another.getStrat();

		if (otherStrat.size() != strat.size())
			return false;

		for (int i = 0; i < strat.size(); i++) {
			if (!otherStrat.contains(strat.get(i)))
				return false;
		}

		return true;
	}

	/* Returns a full clone of this strategy: */
	public Strategy clone() {
		LinkedList<Integer> strat2 = new LinkedList<Integer>();

		for (int i = 0; i < strat.size(); i++) {
			int toCopy = strat.get(i);
			strat2.add(toCopy);
		}

		Strategy clone = new Strategy(strat2, isDrawStrat);
		return clone;
	}

	public LinkedList<Integer> getStrat() {
		return strat;
	}
	
	public int getWins() {
		return wins;
	}

	public void augmentWins() {
		if (!isDrawStrat)
			wins = wins+1;
	}
	
	public int getLosses() {
		return losses;
	}

	public void augmentLosses() {
		if (!isDrawStrat)
			losses = losses+1;
	}

	public int getScore() {
		return (wins-losses);
	}

	public int size() {
		return strat.size();
	}
	
	public boolean properlyContains(LinkedList<Integer> possibleContainer, LinkedList<Integer> possiblyContained) {
		if (possiblyContained.size() > possibleContainer.size())
			return false;
		for (int i = 0; i < possiblyContained.size(); i++)
			if (!possibleContainer.contains(possiblyContained.get(i)))
				return false;
		return true;
	}

	public static void main(String[] args) {
		
		/*
		LinkedList<Integer> one = new LinkedList<Integer>();
		one.add(6);
		one.add(9);
		one.add(2);
		one.add(3);
		LinkedList<Integer> two = new LinkedList<Integer>();
		two.add(5);
		two.add(8);
		two.add(1);
		two.add(4);
		two.add(3);
		Strategy s = new Strategy(6, 9, 2, 7);
		Strategy t = new Strategy(5, 8, 1, 4, 3);
		System.out.println(s.overlaps(t));*/
	}
}