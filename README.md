This is an attempt to solve RubyQuiz problem #4, which asks you to write an AI that learns how to play "a perfect game" of Tic-Tac-Toe, starting from a naive state (with no knowledge of the rules - it only knows how to make a valid moe and recognize when it has won or lost).

It was a fun, albeit not wildly successful, exercise for me, and I like to think I created a good infrastructure to model Tic-Tac-Toe gameplay at least (be it between two humans, a human and a computer, or two computers).

However, my "learning" algorithm isn't particularly sophisticated at all - I was trying more to model the psychology of a human player placed in this situation - and, in fact, falls right down to some degree.  I ran a trial where I created 40 computer vs. computer games, each with 500 rounds.  In over half of them, the computer AIs still hadn't reached a "standoff" equilibrium - i.e. couldn't play the desired perfect game of tic-tac-toe.

The algorithm I use in the current version involves the computer storing all the sequences of winning moves discovered by itself or its opponent, attempting to replicate any winning strategy that's feasible on the current board, and/or attempting to block its opponent if it detects that s/he/it is playing a known winning strategy.  Crude, as I said, and not hugely successful (for instance, there's no mechanism in place to analyze which possible winning strategy is the "best" to play in any given circumstance).

If I were to do a v2.0 of this program, which I would like to, I think my approach would look more like this:

1) First, use the reflectional/rotational symmetry of the tic-tac-toe board to reduce the number of possible game states, then initialize a tree of those states
2) Design the computer AI to explore as much of that tree as possible, keeping track of which player won or if it was a draw at each leaf
3) Use this to associate probabilities/payoffs for each player at each game state
4) Always choose the highest-payoff node when making a move