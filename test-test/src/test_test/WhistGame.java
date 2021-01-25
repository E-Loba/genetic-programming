package test_test;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.LazySeq;
import clojure.lang.PersistentArrayMap;

public class WhistGame
{
	public static int dummy()
	{
		return 42;
	}

	public static class PlayerObject
	{
		// these fields store the function objects for each stage of the game
		// use the do_action method in order to perform a move
		private IFn discard;
		private IFn peek;
		private IFn mode;
		private IFn choice;
		private IFn lead;
		private IFn dolead;
		private IFn respond;

		// support functions used for error checking
		// private IFn isdiscard = Clojure.var( "test-test.core", "isdiscard?"
		// );
		// private IFn iscard = Clojure.var( "test-test.core", "iscard?" );
		// private IFn isboole = Clojure.var( "test-test.core", "isboole?" );
		// private IFn ismode = Clojure.var( "test-test.core", "ismode?" );
		// private IFn ischoice = Clojure.var( "test-test.core", "ischoice?" );

		// the context of the game
		private Object situation;

		Object modes_stack;
		int trick_win_counter;
		int hand_win_counter;

		Object hand;
		Object discarded;

		/**
		 * creates a java object from the clojure data structure
		 * 
		 * @param player
		 *            the clojure map containing functions for each stage of th
		 *            game
		 */
		PlayerObject( PersistentArrayMap player )
		{
			discard = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "discard" ) );
			peek = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "peek" ) );
			mode = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "mode" ) );
			choice = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "choice" ) );
			lead = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "lead" ) );
			dolead = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "dolead" ) );
			respond = ( IFn ) Clojure.var( "clojure.core", "get" ).invoke( player, Keyword.find( "respond" ) );
		}

		/**
		 * updates the context of the game before making a move
		 * 
		 * @param myplay
		 *            the card played by the opponent
		 * @param mymisere
		 *            boolean is the game mode misere?
		 * @param mytrump
		 *            the currently trumping suit
		 */
		void updateSituation( Object myplay, boolean mymisere, Object mytrump )
		{
			LazySeq mydiscard2 = ( LazySeq ) Clojure.var( "clojure.core", "concat" ).invoke( discarded, played );
			situation = ( PersistentArrayMap ) Clojure.var( "test-test.core", "create-situation" ).invoke( myplay, hand,
					mydiscard2, mymisere, mytrump );
		}

		/**
		 * initialise the modes stack
		 */
		void set_modes()
		{
			modes_stack = Clojure.var( "clojure.core", "eval" ).invoke( Clojure.read( "allmodes" ) );
		}

		/**
		 * update the modes stack by removing used modes
		 * 
		 * @param current_mode
		 *            the mode that was already used
		 */
		void update_modes( Object current_mode )
		{
			modes_stack = Clojure.var( "test-test.core", "remove-mode" ).invoke( current_mode, modes_stack );
		}

		/**
		 * makes the player perform a move for the indicated stage of the game
		 * 
		 * @param action
		 *            the current stage of the game
		 * @param myplay
		 *            the card played by the opponent
		 * @param mymisere
		 *            whether misere is the current mode
		 * @param mytrump
		 *            the currently trumping suit
		 * @return an object appropriate for the current stage of the game
		 */
		Object do_action( String action, Object myplay, boolean mymisere, Object mytrump )
		{
			updateSituation( myplay, mymisere, mytrump );
			if ( action.equals( "discard" ) )
				return discard.invoke( situation );
			else if ( action.equals( "peek" ) )
				return peek.invoke( situation );
			else if ( action.equals( "choice" ) )
				return choice.invoke( situation );
			else if ( action.equals( "lead" ) )
				return lead.invoke( situation );
			else if ( action.equals( "dolead" ) )
				return dolead.invoke( situation );
			else if ( action.equals( "respond" ) )
				return respond.invoke( situation );
			else if ( action.equals( "mode" ) )
				return mode.invoke( situation, modes_stack );
			else
				return null;
		}
	}

	// some objects used in the game
	static Object deck, played;
	static boolean is_misere;
	static Object current_trump;
	static Object choices;
	static Object nullCard;
	static boolean verbose;

	/**
	 * calls on several helper methods to host a game of Whist between two
	 * players
	 * 
	 * @param Player1
	 * @param Player2
	 * @param is_verbose
	 *            whether or not to print information to console
	 * @return 1 indicating palyer 1's victory, or 2 indicating player 2's
	 *         victory. 0 means draw
	 */
	public static int playWhist( PersistentArrayMap Player1, PersistentArrayMap Player2, boolean is_verbose )
	{
		// load the clojure namespaces
		IFn require = Clojure.var( "clojure.core", "require" );
		require.invoke( Clojure.read( "test-test.core" ) );
		require.invoke( Clojure.read( "test-test.funcs" ) );
		verbose = is_verbose;

		// initialise game variables
		choices = Clojure.var( "test-test.core", "choices" );
		nullCard = Clojure.var( "clojure.core", "eval" ).invoke( Clojure.read( "null-card" ) );

		// create players from maps
		PlayerObject player1 = new PlayerObject( Player1 );
		PlayerObject player2 = new PlayerObject( Player2 );

		player1.set_modes();
		player2.set_modes();

		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ninitialised\n" );

		int result = 0;
		// randomly chooses one player to be the first dealer
		// alternates dealers eight times
		// the if-statements catch whether a player is unable to play properly
		// and declare his opponent the winner
		if ( Math.random() > 0.5 )
		{
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
		}
		else
		{
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player1, player2 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
			result = play_hand( player2, player1 );
			if ( result == -1 )
				return 2;
			else if ( result == -2 )
				return 1;
		}
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ncompleted game\n" );
		// counts the victories achieved by each player and selects a winner
		if ( player1.hand_win_counter > player2.hand_win_counter )
			return 1;
		else if ( player1.hand_win_counter < player2.hand_win_counter )
			return 2;
		else
			return 0;
	}

	/**
	 * modifies players' hands and the deck accordingly
	 * 
	 * @param player1
	 * @param player2
	 */
	static void deal_hands( PlayerObject player1, PlayerObject player2 )
	{
		deck = Clojure.var( "test-test.core", "produce-deck" ).invoke();
		// shuffle deck
		Object temp = Clojure.var( "test-test.core", "deal-hand" ).invoke( deck, 12 );
		// take twelve cards
		deck = Clojure.var( "clojure.core", "last" ).invoke( temp );
		// remove dealt cards from deck
		player1.hand = Clojure.var( "clojure.core", "butlast" ).invoke( temp );
		// give cards to player
		temp = Clojure.var( "test-test.core", "deal-hand" ).invoke( deck, 12 );
		// ditto
		player2.hand = Clojure.var( "clojure.core", "butlast" ).invoke( temp );
		// ditto
		deck = Clojure.var( "clojure.core", "last" ).invoke( temp );
		// ditto
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ndealt hands\n" );
	}

	/**
	 * side effects: modifies "deck", "player.hand", and "player.discarded"
	 * accordingly
	 * 
	 * @param player
	 * @param situation
	 */
	static int peek_procedure( PlayerObject player, boolean mymisere, Object mytrump )
	{
		Object card = Clojure.var( "clojure.core", "first" ).invoke( deck );
		boolean do_peek;
		try
		{
			// checks whether the player wants to keep or discard the card
			// the peeked card is passed to player for inspection
			do_peek = ( boolean ) player.do_action( "peek", card, mymisere, mytrump );
		}
		catch ( Exception E )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
			return -1;
		}
		if ( do_peek )
		{
			player.hand = Clojure.var( "clojure.core", "conj" ).invoke( player.hand, card );
			// take card
			deck = Clojure.var( "clojure.core", "rest" ).invoke( deck );
			// remove card from deck
			card = Clojure.var( "clojure.core", "first" ).invoke( deck );
			// take next card
			player.discarded = Clojure.var( "clojure.core", "conj" ).invoke( player.discarded, card );
			// discard it
			deck = Clojure.var( "clojure.core", "rest" ).invoke( deck );
			// remove it from deck
		}
		else
		{
			player.discarded = Clojure.var( "clojure.core", "conj" ).invoke( player.discarded, card );
			// discard card
			deck = Clojure.var( "clojure.core", "rest" ).invoke( deck );
			// remove it from deck
			card = Clojure.var( "clojure.core", "first" ).invoke( deck );
			// take next card
			player.hand = Clojure.var( "clojure.core", "conj" ).invoke( player.hand, card );
			// keep it
			deck = Clojure.var( "clojure.core", "rest" ).invoke( deck );
			// remove it from deck
		}
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\npeeked\n" );
		return 0;
	}

	/**
	 * side effects: mutates players' hands and the pile of played cards
	 * accordingly
	 * 
	 * @param dealer
	 * @param non_dealer
	 * @param non_dealer_leads
	 * @return the winner of the trick 1 = dealer won 0 = non-dealer won
	 */
	static int trick_procedure( PlayerObject dealer, PlayerObject non_dealer, boolean non_dealer_leads )
	{
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\nplaying trick\n" );
		PlayerObject leader, responder;
		if ( non_dealer_leads ) // assign leader and responder roles
		{
			leader = non_dealer;
			responder = dealer;
		}
		else
		{
			leader = dealer;
			responder = non_dealer;
		}

		Object played_card = nullCard;
		try
		{
			// let the leader select
			played_card = leader.do_action( "dolead", nullCard, is_misere, current_trump );
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( "player leads with card!" + played_card.toString() );
		}
		catch ( Exception E )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
			return -1;
		}
		leader.hand = Clojure.var( "test-test.core", "remove-card" ).invoke( played_card, leader.hand );
		// remove card from leader's hand
		played = Clojure.var( "clojure.core", "conj" ).invoke( played, played_card );
		// add card to pile of played cards

		Object respond_card = nullCard;
		try
		{
			// let player respond to played card
			respond_card = ( PersistentArrayMap ) responder.do_action( "respond", played_card, is_misere,
					current_trump );
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( "player responds with card!" + respond_card.toString() );
		}
		catch ( Exception E )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
			return -2;
		}
		responder.hand = Clojure.var( "test-test.core", "remove-card" ).invoke( played_card, responder.hand );
		// remove played card from hand
		played = Clojure.var( "clojure.core", "conj" ).invoke( played, respond_card );
		// add to pile of played cards

		// if the responder failed to produce a card, the leader wins
		if ( !( boolean ) Clojure.var( "test-test.core", "iscard?" ).invoke( respond_card ) )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( "\nresponder messed up!\n" );
			if ( non_dealer_leads ) // report whether the dealer or non-dealer
									 // won
				return -1;
			else
				return -2;
		}
		// if the leader failed to produce a card, the responder wins
		else if ( !( boolean ) Clojure.var( "test-test.core", "iscard?" ).invoke( played_card ) )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( "\nleader messed up!\n" );
			if ( non_dealer_leads ) // report whether the dealer or non-dealer
									 // won
				return -1;
			else
				return -2;
		}

		boolean leader_wins = false;
		// determine the winner of the trick using a clojure function
		try
		{
			IFn check_winner = Clojure.var( "test-test.funcs", "lead-wins-against?" );
			leader_wins = ( boolean ) check_winner.invoke( played_card, respond_card, current_trump, is_misere );
		}
		catch ( Exception E )
		{
			if ( verbose )
			{
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
				Clojure.var( "clojure.core", "print" ).invoke( played_card );
				Clojure.var( "clojure.core", "print" ).invoke( respond_card );
				Clojure.var( "clojure.core", "print" ).invoke( current_trump );
				Clojure.var( "clojure.core", "print" ).invoke( is_misere );
			}
			return -1;
		}
		if ( leader_wins ) // adjust trick win counters
			leader.trick_win_counter++;
		else
			responder.trick_win_counter++;
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ncompleted trick\n" );

		if ( non_dealer_leads ) // report whether the dealer or non-dealer won
			return leader_wins ? 0 : 1;
		else
			return leader_wins ? 1 : 0;
	}

	static int play_hand( PlayerObject dealer, PlayerObject non_dealer )
	{

		// initialise hand-variables
		dealer.trick_win_counter = 0;
		non_dealer.trick_win_counter = 0;
		played = Clojure.var( "clojure.core", "list" ).invoke();
		deal_hands( dealer, non_dealer );

		Object current_mode = null;
		try
		{
			// select mode
			current_mode = non_dealer.do_action( "mode", nullCard, is_misere, current_trump );
		}
		catch ( Exception E )
		{
			if ( verbose )
			{
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() + "\n" );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.situation );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.modes_stack );
				Clojure.var( "clojure.core", "print" ).invoke( current_mode );
				Clojure.var( "clojure.core", "print" ).invoke( "\n" );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.mode );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.dolead );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.respond );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.peek );
				Clojure.var( "clojure.core", "print" ).invoke( non_dealer.discard );
			}
			return -2;
		}
		is_misere = ( boolean ) Clojure.var( "clojure.core", "=" ).invoke( current_mode,
				Clojure.var( "test-test.core", "misere" ) );
		// check whether mode is misere
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\nchose mode\n" );

		if ( ( boolean ) Clojure.var( "clojure.core", "=" ).invoke( current_mode,
				Clojure.var( "test-test.core", "choice" ) ) )
		{
			try
			{
				// allow player to choose whether to lead or not
				current_mode = non_dealer.do_action( "choice", nullCard, is_misere, current_trump );
			}
			catch ( Exception E )
			{
				if ( verbose )
					Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
				return -2;
			}
		}
		current_trump = Clojure.var( "test-test.core", "get-trump-from-mode" ).invoke( current_mode );
		// set trumps according to selected mode
		non_dealer.update_modes( current_mode );
		// remove mode from stack
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\nchose choice\n" );

		Object to_discard = null;
		try
		{
			// allow player to discard
			to_discard = dealer.do_action( "discard", nullCard, is_misere, current_trump );
			dealer.hand = Clojure.var( "test-test.core", "remove-group" ).invoke( to_discard, dealer.hand );
		}
		catch ( Exception E )
		{
			if ( verbose )
			{
				Clojure.var( "clojure.core", "print" ).invoke( "\ndiscard 1 " + E.getMessage() + "\n" );
				Clojure.var( "clojure.core", "print" ).invoke( dealer.situation );
				Clojure.var( "clojure.core", "print" ).invoke( to_discard );
				Clojure.var( "clojure.core", "print" ).invoke( current_mode );
				Clojure.var( "clojure.core", "print" ).invoke( current_trump );
				Clojure.var( "clojure.core", "print" ).invoke( dealer.modes_stack );
			}
			return -1;
		}
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ndiscarded 1\n" );
		try
		{
			// allow player to discard
			to_discard = non_dealer.do_action( "discard", nullCard, is_misere, current_trump );
			non_dealer.hand = Clojure.var( "test-test.core", "remove-group" ).invoke( to_discard, non_dealer.hand );
		}
		catch ( Exception E )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
			return -2;
		}
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ndiscarded 2\n" );

		int error_check = 0;

		for ( int i = 0; i < 7; i++ ) // initiate peek procedure for each player
										 // alternatingly
		{
			error_check = peek_procedure( non_dealer, is_misere, current_trump );
			if ( error_check == -1 )
				return -2;
			error_check = peek_procedure( dealer, is_misere, current_trump );
			if ( error_check == -1 )
				return -1;
		}

		// this variable determines who will lead the next trick.
		// the non-dealer usually starts, so it's initialised to false
		int dealer_won_trick = 0;
		boolean lead;
		try
		{
			lead = ( boolean ) non_dealer.do_action( "lead", nullCard, is_misere, current_trump );
		}
		catch ( Exception E )
		{
			if ( verbose )
				Clojure.var( "clojure.core", "print" ).invoke( E.getMessage() );
			return -2;
		}

		if ( is_misere ) // in misere mode, player has the chance to go second
			if ( lead )
				dealer_won_trick = 1; // in misere, the non-dealer may choose
										 // to go second
		boolean dealer_leads = false;
		for ( int i = 0; i < 13; i++ )
		{
			dealer_leads = ( dealer_won_trick == 0 ) ? false : true;
			dealer_won_trick = trick_procedure( dealer, non_dealer, !dealer_leads );
			// if either player is unable to make a move, terminate game
			if ( dealer_won_trick == -1 )
			{
				if ( dealer_leads )
					return -1;
				else
					return -2;
			}
			else if ( dealer_won_trick == -2 )
			{
				if ( dealer_leads )
					return -2;
				else
					return -1;
			}
		}

		// determine the winner of a hand by counting trick wins
		if ( is_misere )
		{
			if ( non_dealer.trick_win_counter > dealer.trick_win_counter )
				dealer.hand_win_counter++;
			else
				non_dealer.hand_win_counter++;
		}
		else
		{
			if ( non_dealer.trick_win_counter < dealer.trick_win_counter )
				dealer.hand_win_counter++;
			else
				non_dealer.hand_win_counter++;
		}
		if ( verbose )
			Clojure.var( "clojure.core", "print" ).invoke( "\ncompleted hand\n" );
		return 1000;
	}

}
