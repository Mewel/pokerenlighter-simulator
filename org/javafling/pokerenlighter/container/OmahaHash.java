package org.javafling.pokerenlighter.container;

import org.javafling.pokerenlighter.combination.Card;
import org.javafling.pokerenlighter.combination.Deck;
import java.io.Serializable;

/** This is a hash table designed to store OmahaHand objects.
*/
public class OmahaHash extends PokerHash implements Serializable	//TODO: to implement
{
	// a deck of cards. normally it wouldn't be necessary, but it was introduced as to avoid creating
	// 270725 * 4 = 1,082,900 instances of the class Card (this way only 52 instances are made)
	private Deck mydeck;
	
	private OmahaHand[] hands;

	/** Constructor for this hash table.
	*
	* @param size the size of this hash table.
	*
	* @param nrelem the maximum number of elements accepted by this hash table.
	*/
	public OmahaHash (int size, int nrelem)
	{
		super (size, nrelem);
		
		hands = new OmahaHand[size];

		//create the deck
		mydeck = new Deck ();

		//this will create all the possible combinations
		CombinationGenerator cg = new CombinationGenerator (52, 4);
		//storage spaces for the ints generated by the variable cg (see above)
		int[] indices;
		//temporary storage for the current hand beeing created
		OmahaHand tmp_hc;
		//additional vars
		int i, j;
		
		//go through every possible combination
		while (cg.hasMore ())	
		{
			//store combination of numbers in a temporary var
			indices = cg.getNext ();	

			//get cards from deck and create the hand
			tmp_hc = new OmahaHand (mydeck.getCard (indices[0]), mydeck.getCard (indices[1]), mydeck.getCard (indices[2]), mydeck.getCard (indices[3]));
	
			//determine the location where to store the hand
			i = hfunc (tmp_hc.toString ());

			//linear probing implementation. if the determined position is occupied, go to the next one until a free slot is found
			//if the end of the table is reached, go back to beginning
			while (hands[i] != null)
			{
				i = (i == HSIZE - 1) ? 0 : i + 1;
			}

			//store the hand
			hands[i] = tmp_hc;
		}
	}
	
	/** Finds a OmahaHand object in the hashtable that contains the cards given as parameters.
	* You may provide the cards in any order you wish.
	*
	* @param c1 The first card of the OmahaHand.
	* @param c2 The second card of the OmahaHand.
	* @param c3 The third card of the OmahaHand.
	* @param c4 The fourth card of the OmahaHand.
	*
	* @return A reference to the OmahaHand in the hash table which contains the 4 cards given as parameters.
	* If no such OmahaHand object exists, null is returned.
	*/
	public OmahaHand getOmahaHand (Card c1, Card c2, Card c3, Card c4)
	{
		OmahaHand tmp_hc = new OmahaHand (c1, c2, c3, c4);

		int i = hfunc (tmp_hc.toString ());

		boolean gone_over = false;

		//keep going until we find what we want (or until it's pointless to keep searching)
		while (hands[i] == null || ! hands[i].compare (tmp_hc))	//TODO: fix this
		{
			++i;
			
			//if we're at the end of the table, skip to the beginning
			if (i == HSIZE)
			{
				//if we already skipped to the beginning once, we won't do it again.
				//the OmahaHand is clearly not in the table.
				if (gone_over)
				{
					return null;
				}

				i = 0;
				gone_over = true;
			}
		}

		return hands[i];
	}

	/** Prints the contents of this hash table to a file. Empty locations are ignored.
	*
	* @param filename The name of the output file.
	*/
	public void dumpToFile (String filename)
	{
		dumpToFile (hands, filename);
	}
}