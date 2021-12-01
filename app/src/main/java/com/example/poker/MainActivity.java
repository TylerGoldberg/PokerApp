package com.example.poker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button raise;
    Button fold;
    Button stay;
    Button play;

    ImageView card1;
    ImageView card2;
    ImageView card3;
    ImageView card4;
    ImageView poolcard_1;
    ImageView poolcard_2;
    ImageView poolcard_3;
    ImageView poolcard_4;
    ImageView poolcard_5;

    playPoker.Card p1card1;
    playPoker.Card p1card2;
    playPoker.Card p2card1;
    playPoker.Card p2card2;
    List<playPoker.Card> cardarray = new ArrayList<>();


    TextView winText;
    TextView loseText;

    TextView p1MoneyView;
    TextView p2MoneyView;

    String playerName = "";
    String roomName = "";
    String role = "";

    int p1Money;
    int p2Money;
    int potMoney;
    int i = 4;

    boolean playOver = false; // set play over to false
    boolean hostTurn = true;
    int timesRaised = 0;
    boolean p1Lose =false;
    boolean p2Lose =false;
    boolean stayed = false;

    FirebaseDatabase database;
    DatabaseReference messageRef;

    DatabaseReference p1Mon;
    DatabaseReference p2Mon;
    DatabaseReference potMon;
    DatabaseReference p1Lost;
    DatabaseReference p2Lost;
    DatabaseReference turn;
    DatabaseReference Raised;
    DatabaseReference I;
    DatabaseReference allcards;
    DatabaseReference STAY;
    playPoker.Deck deck = new playPoker.Deck();

    ImageView[] poolcards = {poolcard_1,poolcard_2,poolcard_3,poolcard_4,poolcard_5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            roomName = extras.getString("roomName");
            if (roomName.equals(playerName)) {
                role = "host";
            } else {
                role = "guest";
            }
        }
        p1Mon = database.getReference("/rooms/"+roomName+"/p1Mon");
        p1Mon.setValue(2000);
        p2Mon = database.getReference("/rooms/"+roomName+"/p2Mon");
        p2Mon.setValue(2000);
        potMon = database.getReference("/rooms/"+roomName+"/potMon");
        potMon.setValue(0);
        turn = database.getReference("/rooms/"+roomName+"/turn");
        turn.setValue(1);
        p1Lost = database.getReference("/rooms/"+roomName+"/p1Lost");
        p1Lost.setValue(0);
        p2Lost = database.getReference("/rooms/"+roomName+"/p2Lost");
        p2Lost.setValue(0);
        Raised = database.getReference("/rooms/"+roomName+"/Raised");
        Raised.setValue(0);
        STAY = database.getReference("/rooms/"+roomName+"/STAY");
        STAY.setValue(0);
        I = database.getReference("/rooms/"+roomName+"/I");
        allcards = database.getReference("/rooms/"+roomName+"/allcards");

        fold = findViewById(R.id.fold);
        raise = findViewById(R.id.raise);
        stay = findViewById(R.id.stay);
        play = findViewById(R.id.play);

        card1 = (ImageView) findViewById(R.id.card1);
        card2 = (ImageView) findViewById(R.id.card2);
        card3 = (ImageView) findViewById(R.id.card3);
        card4 = (ImageView) findViewById(R.id.card4);

        poolcards[0] = (ImageView) findViewById(R.id.poolcard_1);
        poolcards[1] = (ImageView) findViewById(R.id.poolcard_2);
        poolcards[2]= (ImageView) findViewById(R.id.poolcard_3);
        poolcards[3] = (ImageView) findViewById(R.id.poolcard_4);
        poolcards[4] = (ImageView) findViewById(R.id.poolcard_5);



        p1MoneyView = (TextView) findViewById(R.id.p1MoneyView);
        p2MoneyView = (TextView) findViewById(R.id.p2MoneyView);

        loseText = (TextView) findViewById(R.id.loseText);
        winText = (TextView) findViewById(R.id.winText);

        winText.setVisibility(View.GONE);
        loseText.setVisibility(View.GONE);
        fold.setVisibility(View.GONE);
        raise.setVisibility(View.GONE);
        stay.setVisibility(View.GONE);



        p1Money = 2000;
        p2Money = 2000;
        p1MoneyView.setText("P1 Money :" + p1Money);
        p2MoneyView.setText("P2 Money :" + p2Money);



        for(int j = 0; j<10;j++)
        {
            cardarray.add(null);
        }

        play.setVisibility(View.GONE); // get rid of play button and start play
        winText.setVisibility(View.GONE);
        loseText.setVisibility(View.GONE);
        fold.setEnabled(true);
        raise.setEnabled(true);
        stay.setEnabled(true);

        deal();

        if(hostTurn && role.equals("host"))
        {
            fold.setVisibility(View.VISIBLE);
            raise.setVisibility(View.VISIBLE);
            stay.setVisibility(View.VISIBLE);
        }
        if(!hostTurn && role.equals("guest"))
        {
            fold.setVisibility(View.VISIBLE);
            raise.setVisibility(View.VISIBLE);
            stay.setVisibility(View.VISIBLE);
        }

        addRoomListener();

        fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fold.setVisibility(View.GONE);
                raise.setVisibility(View.GONE);
                stay.setVisibility(View.GONE);
                winText.setVisibility(View.GONE);
                loseText.setVisibility(View.GONE);
                if(hostTurn)
                {
                    p1Lose = true;
                    p1Lost.setValue(1);

                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        loseText.setVisibility(View.VISIBLE);
                    }
                    if(role.equals("guest"))
                    {
                        winText.setVisibility(View.VISIBLE);
                    }

                    p2Money += potMoney;
                    p2Mon.setValue(p2Money);
                    turn.setValue(0);



                }
                else
                {
                    p2Lose = true;
                    p2Lost.setValue(1);

                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        winText.setVisibility(View.VISIBLE);

                    }
                    if(role.equals("guest"))
                    {
                        loseText.setVisibility(View.VISIBLE);
                    }
                    turn.setValue(1);

                    p1Money += potMoney;
                    potMon.setValue(0);
                    p2Mon.setValue(p2Money);

                }

                deal();
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fold.setVisibility(View.GONE);
                raise.setVisibility(View.GONE);
                stay.setVisibility(View.GONE);
                winText.setVisibility(View.GONE);
                loseText.setVisibility(View.GONE);
                if(timesRaised == 1)
                {
                    dealpoolcard();
                    Raised.setValue(0);
                    turn.setValue(hostTurn ? 0 : 1);
                    return;


                    /*if(hostTurn)
                    {

                        p1Money -= 100;
                        p1Mon.setValue(p1Mon);
                        potMoney += 100;
                        potMon.setValue(potMoney);
                        Raised.setValue(0);
                        turn.setValue(0);

                    }
                    else
                    {
                        p2Money -= 100;
                        p2Mon.setValue(p2Mon);
                        potMoney += 100;
                        potMon.setValue(potMoney);
                        Raised.setValue(0);
                        turn.setValue(1);
                    }

                    if(i<9)
                    {
                        playPoker.Card card = deck.cards.get(i);
                        int resource = getResources().getIdentifier(card.toString().toLowerCase(),"drawable",getPackageName());
                        poolcards[i-4].setImageResource(resource);
                        I.setValue(i+1);
                    }
                    else
                    {
                        if(i==10)
                        {
                            //find who won
                            deal();
                        }
                    }
                    return;*/
                }
                if(hostTurn)
                {
                    p1Money -= 20;
                    p1Mon.setValue(p1Money);
                    potMoney += 20;
                    potMon.setValue(potMoney);
                    turn.setValue(0);
                    Raised.setValue(timesRaised + 1);
                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                    }
                    if(role.equals("guest"))
                    {
                        stay.setVisibility(View.GONE);
                    }
                }
                else
                {
                    p2Money -= 20;
                    p2Mon.setValue(p2Money);
                    potMoney += 20;
                    potMon.setValue(potMoney);
                    turn.setValue(1);
                    Raised.setValue(timesRaised + 1);
                    if(role.equals("guest"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                    }
                    if(role.equals("host"))
                    {
                        stay.setVisibility(View.GONE);
                    }
                }






            }

        });


        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fold.setVisibility(View.GONE);
                raise.setVisibility(View.GONE);
                stay.setVisibility(View.GONE);
                winText.setVisibility(View.GONE);
                loseText.setVisibility(View.GONE);
                if(stayed)
                {
                    stayed = false;
                    turn.setValue(hostTurn ? 0 : 1);
                    dealpoolcard();
                    return;
                }

                STAY.setValue(1);
                    if(role.equals("host") && hostTurn)
                    {
                        turn.setValue(0);
                        return;
                    }
                    if(role.equals("guest") && !hostTurn)
                    {
                        turn.setValue(1);
                        return;
                    }

            }
        });


    }

    public void deal(){
        Raised.setValue(0);
        deck.shuffle();

        for(int j = 1; j<10;j++)
        {
            cardarray.set(j,null);
        }
        System.out.println("Before deal:" + cardarray);

        for(int j = 1; j < 5; j++)
        {
            cardarray.set(j,deck.cards.get(j));
        }
        System.out.println("After deal: " + cardarray);
        allcards.setValue(null);
        allcards.setValue(cardarray);
        System.out.println("After set: " + cardarray);

        I.setValue(4);

        System.out.println("After I: " + cardarray);

        if(role.equals("host"))
        {
            int cardResource = getResources().getIdentifier(cardarray.get(1).toString().toLowerCase(),"drawable",getPackageName());
            card1.setImageResource(cardResource);
            cardResource = getResources().getIdentifier(cardarray.get(2).toString().toLowerCase(),"drawable",getPackageName());
            card2.setImageResource(cardResource);
        }

        if(role.equals("guest"))
        {
            int cardResource = getResources().getIdentifier(cardarray.get(3).toString().toLowerCase(),"drawable",getPackageName());
            card3.setImageResource(cardResource);
            cardResource = getResources().getIdentifier(cardarray.get(4).toString().toLowerCase(),"drawable",getPackageName());
            card4.setImageResource(cardResource);
        }

        System.out.println("After deal: " + cardarray);


        p1Money -= 20;
        p1Mon.setValue(p1Money);
        p2Money -= 20;
        p2Mon.setValue(p2Money);
        potMoney = 40;
        potMon.setValue(potMoney);
    }

    public void dealpoolcard()
    {

        if(i>=9)
        {
            finisher();
            deal();
            return;
        }
        I.setValue(i+1);
        allcards.setValue(null);
        allcards.setValue(cardarray);

    }

    public void finisher()
    {
        ArrayList<playPoker.Card> p1 = new ArrayList<>(cardarray);
        p1.remove(4);
        p1.remove(3);
        p1.remove(0);
        ArrayList<playPoker.Card> p2 = new ArrayList<>(cardarray);
        p2.remove(2);
        p2.remove(1);
        p2.remove(0);
        if(playPoker.findRoyal(p1))
        {
            p2Lost.setValue(1);
            return;
        }
        if(playPoker.findRoyal(p2))
        {
            p1Lost.setValue(1);
            return;
        }
        int ffs1 = playPoker.findFlushStraight(p1);
        int ffs2 = playPoker.findFlushStraight(p2);
        if(ffs1>ffs2)
        {
            p2Lost.setValue(1);
            return;
        }
        if(ffs2>ffs1)
        {
            p1Lost.setValue(1);
            return;
        }
        int p1Kind = playPoker.findKind(p1);
        int p2Kind = playPoker.findKind(p2);
        if(p1Kind==4)
        {
            p2Lost.setValue(1);
            return;
        }
        if(p2Kind==4)
        {
            p1Lost.setValue(1);
            return;
        }
        int fh1 = playPoker.findHouse(p1);
        int fh2 = playPoker.findHouse(p2);
        if(fh1>fh2)
        {
            p2Lost.setValue(1);
            return;
        }
        if(fh2>fh1)
        {
            p1Lost.setValue(1);
            return;
        }
        if(playPoker.findFlush(p1))
        {
            p2Lost.setValue(1);
            return;
        }
        if(playPoker.findFlush(p2))
        {
            p1Lost.setValue(1);
            return;
        }
        if(playPoker.findFlush(p1))
        {
            p2Lost.setValue(1);
            return;
        }
        if(playPoker.findFlush(p2))
        {
            p1Lost.setValue(1);
            return;
        }
        int s1 = playPoker.findStraight(p2);
        int s2 = playPoker.findStraight(p2);
        if(s1>s2)
        {
            p2Lost.setValue(1);
            return;
        }
        if(s2>s1)
        {
            p1Lost.setValue(1);
            return;
        }
        if(p1Kind==3)
        {
            p2Lost.setValue(1);
            return;
        }
        if(p2Kind==3)
        {
            p1Lost.setValue(1);
            return;
        }
        int[] tp1 = playPoker.findTwoPair(p1);
        int[] tp2 = playPoker.findTwoPair(p2);
        if(tp1!=null)
        {
            if(tp2==null ||(tp1[0] > tp2[0] || (tp1[0] == tp2[0] && tp1[1] >= tp1[1])) )
            {
                p2Lost.setValue(1);
                return;
            }

        }
        if(tp2!=null)
        {
            if(tp1==null ||(tp2[0] > tp1[0] || (tp2[0] == tp1[0] && tp2[1] >= tp1[1])) )
            {
                p1Lost.setValue(1);
                return;
            }

        }
        if(playPoker.findHigh(p1)>=playPoker.findHigh(p2))
        {
            p2Lost.setValue(1);
            return;
        }
        else{
            p1Lost.setValue(1);
            return;
        }
    }

    public void addRoomListener(){
        p1Mon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                p1Money = value;
                p1MoneyView.setText("P1 money:" + p1Money);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        p2Mon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                p2Money = value;
                p2MoneyView.setText("P2 money:" + p2Money);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        potMon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                potMoney = value;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        I.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                i = value;
                playPoker.Card card = deck.cards.get(i);
                int resource = getResources().getIdentifier(card.toString().toLowerCase(),"drawable",getPackageName());
                if(i>=5)
                    poolcards[i-5].setImageResource(resource);
                cardarray.set(i,card);
                cardarray.set(0,card);
                System.out.println(cardarray);
                allcards.setValue(null);
                allcards.setValue(cardarray);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        Raised.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                timesRaised = value;
                if(timesRaised>=1)
                {
                    stay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        turn.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                hostTurn = (value==1);
                if(hostTurn && role.equals("host"))
                {
                    fold.setVisibility(View.VISIBLE);
                    raise.setVisibility(View.VISIBLE);
                    stay.setVisibility(View.VISIBLE);
                }
                if(!hostTurn && role.equals("guest"))
                {
                    fold.setVisibility(View.VISIBLE);
                    raise.setVisibility(View.VISIBLE);
                    stay.setVisibility(View.VISIBLE);
                }
                if(!hostTurn && role.equals("host"))
                {
                    fold.setVisibility(View.GONE);
                    raise.setVisibility(View.GONE);
                    stay.setVisibility(View.GONE);
                }
                if(hostTurn && role.equals("guest"))
                {
                    fold.setVisibility(View.GONE);
                    raise.setVisibility(View.GONE);
                    stay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        p1Lost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot!=null)
                {
                    int value = dataSnapshot.getValue(int.class);
                    p1Lose = (value==1);
                    p2Mon.setValue(p2Money+ potMoney);
                    potMon.setValue(0);
                    if(p1Lose && role.equals("guest"))
                    {
                        loseText.setVisibility(View.GONE);
                        winText.setVisibility(View.VISIBLE);
                    }
                    if(p1Lose && role.equals("host"))
                    {
                        loseText.setVisibility(View.VISIBLE);
                        winText.setVisibility(View.GONE);
                    }
                    p1Lost.setValue(0);


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        p2Lost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                p2Lose = (value==1);
                if(p2Lose && role.equals("host"))
                {
                    loseText.setVisibility(View.GONE);
                    winText.setVisibility(View.VISIBLE);
                }
                if(p2Lose && role.equals("guest"))
                {
                    loseText.setVisibility(View.VISIBLE);
                    winText.setVisibility(View.GONE);
                }
                p2Lost.setValue(0);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });

        STAY.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                stayed = (value==1);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });




        allcards.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot==null)
                    return;
                GenericTypeIndicator<List<playPoker.Card>> t = new GenericTypeIndicator<List<playPoker.Card>>() {
                };
                 cardarray = dataSnapshot.getValue(t);
                 if(cardarray == null)
                     return;
                for(int k = 0; k<10;k++)
                {
                    if(k >= cardarray.size())
                    {
                        cardarray.add(null);
                    }
                }
                /*for(DataSnapshot snapshot : dataSnapshot.getChildren() )
                {
                    playPoker.Suit suit = snapshot.child("suit").getValue(playPoker.Suit.class);
                    playPoker.Value value = snapshot.child("value").getValue(playPoker.Value.class);
                    playPoker.Card card = new playPoker.Card(suit,value);
                    cardarray.set(k,card);
                }*/
                for(int j = 1; j<cardarray.size();j++)
                {
                    System.out.println("yeah im here");
                    if(role.equals("host")&&j==0)
                    {
                        int cardResource = getResources().getIdentifier((cardarray.get(1).toString()).toLowerCase(),"drawable",getPackageName());
                        card1.setImageResource(cardResource);
                        cardResource = getResources().getIdentifier(cardarray.get(2).toString().toLowerCase(),"drawable",getPackageName());
                        card2.setImageResource(cardResource);
                    }

                    if(role.equals("guest")&&j==2)
                    {
                        int cardResource = getResources().getIdentifier(cardarray.get(3).toString().toLowerCase(),"drawable",getPackageName());
                        card3.setImageResource(cardResource);
                        cardResource = getResources().getIdentifier(cardarray.get(4).toString().toLowerCase(),"drawable",getPackageName());
                        card4.setImageResource(cardResource);
                    }
                    if(j >= 5 && cardarray.get(j)==null)
                    {
                        poolcards[j-5].setImageResource(R.drawable.red_joker);
                        continue;
                    }
                    if(j>=5 && cardarray.get(j) != null)
                    {
                        int cardResource = getResources().getIdentifier((cardarray.get(j).toString()).toLowerCase(),"drawable",getPackageName());
                        poolcards[j-5].setImageResource(cardResource);
                    }
                }
                System.out.println(cardarray);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    };

}