

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.LazySeq;
import clojure.lang.PersistentArrayMap;
import clojure.lang.Symbol;

public class WhistGame
{

	public static class PlayerObject
	{
		private IFn discard;
		private IFn peek;
		private IFn mode;
		private IFn choice;
		private IFn lead;
		private IFn dolead;
		private IFn respond;

		private IFn isdiscard = Clojure.var( "test-test.game", "isdiscard?" );
		private IFn iscard = Clojure.var( "test-test.game", "iscard?" );
		private IFn isboole = Clojure.var( "test-test.game", "isboole?" );
		private IFn ismode = Clojure.var( "test-test.game", "ismode?" );
		private IFn ischoice = Clojure.var( "test-test.game", "ischoice?" );

		private PersistentArrayMap situation;

		int trick_win_counter;
		int hand_win_counter;

		LazySeq hand;
		LazySeq discarded;

		PlayerObject( PersistentArrayMap player )
		{
			discard = ( IFn ) player.get( Keyword.find( "test-test.core", "discard" ) );
			peek = ( IFn ) player.get( Keyword.find( "test-test.core", "peek" ) );
			mode = ( IFn ) player.get( Keyword.find( "test-test.core", "mode" ) );
			choice = ( IFn ) player.get( Keyword.find( "test-test.core", "choice" ) );
			lead = ( IFn ) player.get( Keyword.find( "test-test.core", "lead" ) );
			dolead = ( IFn ) player.get( Keyword.find( "test-test.core", "dolead" ) );
			respond = ( IFn ) player.get( Keyword.find( "test-test.core", "respond" ) );
		}

		void updateSituation( PersistentArrayMap myplay, boolean mymisere, Symbol mytrump )
		{
			LazySeq mydiscard2 = ( LazySeq ) Clojure.var( "clojure.core", "concat" ).invoke( discarded, played );
			situation = ( PersistentArrayMap ) Clojure.var( "test-test.core", "create-situation" ).invoke( myplay, hand,
					mydiscard2, mymisere, mytrump );
		}

		Object do_action( String action, PersistentArrayMap myplay, boolean mymisere, Symbol mytrump )
		{
			updateSituation( myplay, mymisere, mytrump );
			if ( action.equals( "discard" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( discard, situation, isdiscard );
			else if ( action.equals( "peek" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( peek, situation, isboole );
			else if ( action.equals( "choice" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( choice, situation, ischoice );
			else if ( action.equals( "lead" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( lead, situation, isboole );
			else if ( action.equals( "dolead" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( dolead, situation, iscard );
			else if ( action.equals( "respond" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( respond, situation, iscard );
			else
				return null;
		}

		Object do_action( String action, PersistentArrayMap myplay, boolean mymisere, Symbol mytrump, Object modes )
		{

			updateSituation( myplay, mymisere, mytrump );
			if ( action.equals( "mode" ) )
				return Clojure.var( "test-test.game", "try-catch" ).invoke( mode, situation, ismode );
			else
				return null;
		}
	}

	static LazySeq deck, played;
	static boolean is_misere;
	static Symbol current_trump;
	static Object modes_stack;
	static Object choices;
	static PersistentArrayMap nullCard;

	public static int playWhist( PersistentArrayMap Player1, PersistentArrayMap Player2 )
	{
		IFn require = Clojure.var( "clojure.core", "require" );
		require.invoke( Clojure.read( "test-test.core" ) );
		require.invoke( Clojure.read( "test-test.funcs" ) );

		modes_stack = Clojure.var( "test-test.core", "modes" );
		choices = Clojure.var( "test-test.core", "choices" );
		nullCard = ( PersistentArrayMap ) Clojure.var( "test-test.core", "null-card" );

		PlayerObject player1 = new PlayerObject( Player1 );
		PlayerObject player2 = new PlayerObject( Player2 );

		int result = 0;
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
		deck = ( LazySeq ) Clojure.var( "test-test.core", "produce-deck" ).invoke();
		LazySeq temp = ( LazySeq ) Clojure.var( "test-test.core", "deal-hand" ).invoke( deck, 12 );
		deck = ( LazySeq ) Clojure.var( "clojure.core", "last" ).invoke( temp );
		player1.hand = ( LazySeq ) Clojure.var( "clojure.core", "butlast" ).invoke( temp );
		temp = ( LazySeq ) Clojure.var( "test-test.core", "deal-hand" ).invoke( deck, 12 );
		player2.hand = ( LazySeq ) Clojure.var( "clojure.core", "butlast" ).invoke( temp );
		deck = ( LazySeq ) Clojure.var( "clojure.core", "last" ).invoke( temp );
	}

	/**
	 * side effects: modifies "deck", "player.hand", and "player.discarded"
	 * accordingly
	 * 
	 * @param player
	 * @param situation
	 */
	static int peek_procedure( PlayerObject player, boolean mymisere, Symbol mytrump )
	{
		PersistentArrayMap card = ( PersistentArrayMap ) Clojure.var( "clojure.core", "first" ).invoke( deck );
		boolean do_peek;
		try
		{
			do_peek = ( boolean ) player.do_action( "peek", nullCard, mymisere, mytrump );
		}
		catch ( Exception E )
		{
			return -1;
		}
		if ( do_peek )
		{
			player.hand = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( player.hand, card );
			deck = ( LazySeq ) Clojure.var( "clojure.core", "rest" ).invoke( deck );
			card = ( PersistentArrayMap ) Clojure.var( "clojure.core", "first" ).invoke( deck );
			player.discarded = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( player.discarded, card );
			deck = ( LazySeq ) Clojure.var( "clojure.core", "rest" ).invoke( deck );
		}
		else
		{
			player.discarded = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( player.discarded, card );
			deck = ( LazySeq ) Clojure.var( "clojure.core", "rest" ).invoke( deck );
			card = ( PersistentArrayMap ) Clojure.var( "clojure.core", "first" ).invoke( deck );
			player.hand = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( player.hand, card );
			deck = ( LazySeq ) Clojure.var( "clojure.core", "rest" ).invoke( deck );
		}
		return 0;
	}

	/**
	 * side effects: mutates players' hands and the pile of played cards
	 * accordingly
	 * 
	 * @param dealer
	 * @param non_dealer
	 * @param non_dealer_leads
	 * @return
	 */
	static int trick_procedure( PlayerObject dealer, PlayerObject non_dealer, boolean non_dealer_leads )
	{
		PlayerObject leader, responder;
		if ( non_dealer_leads )
		{
			leader = non_dealer;
			responder = dealer;
		}
		else
		{
			leader = dealer;
			responder = non_dealer;
		}

		PersistentArrayMap played_card = nullCard;
		try
		{
			played_card = ( PersistentArrayMap ) leader.do_action( "dolead", nullCard, is_misere, current_trump );
		}
		catch ( Exception E )
		{
			return -1;
		}
		leader.hand = ( LazySeq ) Clojure.var( "test-test.core", "remove-card" ).invoke( played_card, leader.hand );
		played = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( played, played_card );

		PersistentArrayMap respond_card = nullCard;
		try
		{
			respond_card = ( PersistentArrayMap ) responder.do_action( "respond", played_card, is_misere,
					current_trump );
		}
		catch ( Exception E )
		{
			return -2;
		}
		responder.hand = ( LazySeq ) Clojure.var( "test-test.core", "remove-card" ).invoke( played_card,
				responder.hand );
		played = ( LazySeq ) Clojure.var( "clojure.core", "conj" ).invoke( played, respond_card );

		boolean leader_wins = ( boolean ) Clojure.var( "test-test.funcs", "leader-wins-against?" ).invoke( played_card,
				respond_card, current_trump, is_misere );
		if ( leader_wins )
			leader.trick_win_counter++;
		else
			responder.trick_win_counter++;

		if ( non_dealer_leads )
			return leader_wins ? 0 : 1;
		else
			return leader_wins ? 1 : 0;
	}

	static int play_hand( PlayerObject dealer, PlayerObject non_dealer )
	{

		dealer.trick_win_counter = 0;
		non_dealer.trick_win_counter = 0;
		deal_hands( dealer, non_dealer );

		Symbol current_mode;
		try
		{
			current_mode = ( Symbol ) non_dealer.do_action( "mode", nullCard, is_misere, current_trump, modes_stack );
		}
		catch ( Exception E )
		{
			return -2;
		}
		is_misere = ( boolean ) Clojure.var( "clojure.core", "=" ).invoke( current_mode,
				Clojure.var( "test-test.core", "misere" ) );

		if ( ( boolean ) Clojure.var( "clojure.core", "=" ).invoke( current_mode,
				Clojure.var( "test-test.core", "choice" ) ) )
		{
			try
			{
				current_mode = ( Symbol ) non_dealer.do_action( "choice", nullCard, is_misere, current_trump );
			}
			catch ( Exception E )
			{
				return -2;
			}
		}
		current_trump = ( Symbol ) Clojure.var( "test-test.core", "get-trump-from-mode" ).invoke( current_mode );

		modes_stack = Clojure.var( "test-test.core", "remove-mode" ).invoke( current_mode, modes_stack );

		LazySeq to_discard;
		try
		{
			to_discard = ( LazySeq ) dealer.do_action( "discard", nullCard, is_misere, current_trump );
			dealer.hand = ( LazySeq ) Clojure.var( "test-test.core", "remove-group" ).invoke( to_discard, dealer.hand );
		}
		catch ( Exception E )
		{
			return -1;
		}
		try
		{
			to_discard = ( LazySeq ) non_dealer.do_action( "discard", nullCard, is_misere, current_trump );
			non_dealer.hand = ( LazySeq ) Clojure.var( "test-test.core", "remove-group" ).invoke( to_discard,
					non_dealer.hand );
		}
		catch ( Exception E )
		{
			return -2;
		}

		int error_check = 0;

		for ( int i = 0; i < 7; i++ )
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
			return -2;
		}

		if ( is_misere )
			if ( lead )
				dealer_won_trick = 1; // in misere, the non-dealer may choose
										 // to go second
		boolean dealer_leads = false;
		for ( int i = 0; i < 13; i++ )
		{
			dealer_leads = ( dealer_won_trick == 0 ) ? false : true;
			dealer_won_trick = trick_procedure( dealer, non_dealer, !dealer_leads );
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
		return 1000;
	}

}
