package com.example.poker;

import java.util.ArrayList;
import java.util.Collections;

public class playPoker{

    enum Suit{Club,Diamond,Heart,Spade}
    enum Value{ Two(2),Three(3),Four(4),Five(5),Six(6),
                Seven(7),Eight(8),Nine(9),Ten(10),Jack(11),
                Queen(12),King(13),Ace(14); //Ace exclusively High

        private int v;
        Value(int i) {
            v = i;
        }

        public int getval(){
        return v;
    }

    }

    private int pot;
    private int player_money;


    public static class Card implements Comparable{
        Suit suit;
        Value value;
        public Card(Suit s, Value v)
        {
            suit = s;
            value = v;
        }

        public Card()
        {
        }

        public Suit getSuit(){
            return suit;
        }

        public void setSuit(Suit suit){
            this.suit = suit;
        }

        public Value getValue(){
            return value;
        }

        public void setValue(Value value){
            this.value = value;
        }

        @Override
        public String toString() {
            return suit.toString() + value.toString();
        }

        @Override
        public int compareTo(Object o) {
            return this.value.compareTo(((Card) o).value);
        }
    }

    public static class Deck{
        ArrayList<Card> cards = new ArrayList<>();
        public Deck()
        {
            int i = 0;
            for(Suit s: Suit.values())
                for(Value v: Value.values()) {
                    Card card = new Card(s, v);
                    cards.add(card);
                }
        }
        public void shuffle(){
            Collections.shuffle(this.cards);
        }
    }

    public void sort(ArrayList<Card> cards)
    {
        Collections.sort(cards);
    }

    public static boolean findRoyal(ArrayList<Card> allcards) //doesnt work currently
    {
        Suit s = null;
        int len = allcards.size();
        for(int i = 0; i < len - 1;i++)
        {
            if(allcards.get(i).value.getval() < Value.Ten.getval())
                continue;
            if(allcards.get(i).value == Value.Ten) {
                s = allcards.get(i).suit;
                if(allcards.get(i).value == allcards.get(i+1).value) {  //checks for a second ten
                    ArrayList<Card> allcards2 = new ArrayList<>(allcards);
                    allcards2.remove(i);
                    if (findRoyal(allcards2))
                        return true;
                }
                if(allcards.get(i).value == allcards.get(i+2).value) { //checks for a third ten and doesnt check for a 4th bc card limit
                    ArrayList<Card> allcards3 = new ArrayList<>(allcards);
                    allcards3.remove(i);
                    if (findRoyal(allcards3))
                        return true;
                }
                continue;
            }
            if(allcards.get(i).suit == s) // checks to see if suit is same
            {
                if(allcards.get(i).value == Value.Ace)
                    return true;
                while(allcards.get(i).value == allcards.get(i+1).value) // ignores cards of same value
                    i++;
                if(allcards.get(i).value.getval() == allcards.get(i+1).value.getval()-1)
                    continue;

            }
            else if(allcards.get(i).value == allcards.get(i+1).value)//sees if theres cards of same value with the suit
            {
                while(allcards.get(i).value == allcards.get(i+1).value)
                {
                    i++;
                    if(allcards.get(i).suit == s)
                        break;
                }
                if(allcards.get(i).suit == s && allcards.get(i).value == Value.Ace)
                    return true;
            }
            else
                return false;
        }

        return false;

    }

    public static int findFlushStraight(ArrayList<Card> allcards)
    {
        int count = 1;
        int highest = 0;
        int len = allcards.size();
        Card c1 = allcards.get(0); // previous card
        Card c2;                   // current card
        for(int i = 1; i < len; i++)
        {
            c2 = allcards.get(i);
            if(c1.value.getval()==c2.value.getval()) // check for same value card, assume not same suit
            {
                ArrayList<Card> allcards2 = new ArrayList<>(allcards); // make another list without c1
                allcards2.remove(c1);
                int ffs = findFlushStraight(allcards2);
                if(ffs > highest)
                    highest = ffs;
                continue; // ignore current card
            }
            if(c2.suit == c1.suit) //check for same suit
            {
                if(c1.value.getval() == c2.value.getval() - 1)
                {
                    count++; // straight continues
                }
            }
            else // run ends
            {
                if(count >= 5 && c2.value.getval() > highest) // see if straight; record highest card
                    highest = c2.value.getval();
                count = 1;
            }
            c1 = c2; // make current card the previous card for next run

        }

        return highest;
    }

    public static int findKind(ArrayList<Card> allcards)
    {
        int count = 1;
        int highest = 1;
        int len = allcards.size();
        for(int i = 1; i < len;i++)
        {
            if(allcards.get(i).value == allcards.get(i-1).value)
                count++;
            else {
                if (count > highest)
                    highest = count;
                count = 1;
            }
        }
        if (count > highest)
            highest = count;

        return highest;
    }

    public static int findHouse(ArrayList<Card> allcards) //return highest card of the triple
    {
        boolean haveTriple = false;
        int count = 0;
        int len = allcards.size();
        Card c1;                    // current card
        Card c2;                   // next card
        Value triple = null;            // value for the triple
        int numpairs = 0;             // number of pairs
        for(int i = 1; i < len -1; i++)
        {
            c1 = allcards.get(0);
            c2 = allcards.get(i+1);

            if(c2.value == c1.value) // next card matches this card
            {
                count++; // these two cards are a pair or triple

                if(count == 2) // is a pair
                {
                        numpairs++;
                }
                else if(count >= 3 && !haveTriple) // is triple and no current triple
                {
                        numpairs--; // remove from num pairs because it was previously counted as a pair
                        haveTriple = true;
                        triple = c1.value;
                }
                else // is triple and currently has triple
                {
                    return c2.value.getval(); //
                }
            }
            else //didnt match
            {
                count = 1;
            }

        }

        if(triple != null && numpairs > 0)
            return triple.getval(); // if there was a triple and a pair return

        return 0;
    }

    public static boolean findFlush(ArrayList<Card> allcards)
    {
        int c =0;
        int s =0;
        int h =0;
        int d = 0;

        for(Card card : allcards)
        {
            if(card.suit == Suit.Club)
                c++;
            if(card.suit == Suit.Diamond)
                d++;
            if(card.suit == Suit.Spade)
                s++;
            if(card.suit == Suit.Heart)
                h++;
        }

        return c >= 5 || s >= 5 || h >= 5 || d >= 5;
    }

    public static int findStraight(ArrayList<Card> allcards) // assumes already sorted
    {
        int count = 1;
        int len = allcards.size();
        for(int i = 0; i < len - 1;i++)
        {
            if(allcards.get(i).value.getval() == allcards.get(i+1).value.getval() - 1)
                count++;
            else if(allcards.get(i).value == allcards.get(i+1).value)
                continue;
            else
                count = 1;
            if(count >=5)
                return 1;
        }
        return 0;
    }

    public static int[] findTwoPair(ArrayList<Card> allcards)
    {
        int count = 0;
        int len = allcards.size();
        Card c1;                    // current card
        Card c2;                   // next card
        int numpairs = 0;             // number of pairs
        int heighest = 0;
        int second = 0;
        for(int i = 1; i < len -1; i++)
        {
            c1 = allcards.get(0);
            c2 = allcards.get(i+1);

            if(c2.value == c1.value) // next card matches this card, and is a pair
            {
                    numpairs++;

                    if(heighest != 0) // if theres already a pair previously heighest pair becomes second
                        second = heighest;

                    heighest = c1.value.getval();


            }

        }

        if(numpairs >= 2)
        {
            return new int[]{heighest,second};
        }

        return null;
    }

    public static int findHigh(ArrayList<Card> allcards)
    {
        int len = allcards.size();
        return allcards.get(len-1).value.getval();
    }




}
