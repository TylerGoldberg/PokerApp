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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    int i;

    boolean playOver = false; // set play over to false
    boolean hostTurn = true;
    int timesRaised = 0;
    boolean p1Fold =false;
    boolean p2Fold =false;
    boolean stayed = false;

    FirebaseDatabase database;
    DatabaseReference messageRef;

    DatabaseReference p1Mon;
    DatabaseReference p2Mon;
    DatabaseReference potMon;
    DatabaseReference turn;
    DatabaseReference Raised;

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
        Raised = database.getReference("/rooms/"+roomName+"/Raised");
        Raised.setValue(0);





        fold = findViewById(R.id.fold);
        raise = findViewById(R.id.raise);
        stay = findViewById(R.id.stay);
        play = findViewById(R.id.play);

        card1 = (ImageView) findViewById(R.id.card1);
        card2 = (ImageView) findViewById(R.id.card2);
        card3 = (ImageView) findViewById(R.id.card3);
        card4 = (ImageView) findViewById(R.id.card4);

        poolcard_1 = (ImageView) findViewById(R.id.poolcard_1);
        poolcard_2 = (ImageView) findViewById(R.id.poolcard_2);
        poolcard_3 = (ImageView) findViewById(R.id.poolcard_3);
        poolcard_4 = (ImageView) findViewById(R.id.poolcard_4);
        poolcard_5 = (ImageView) findViewById(R.id.poolcard_5);
        ImageView[] poolcards = {poolcard_1,poolcard_2,poolcard_3,poolcard_4,poolcard_5};



        p1MoneyView = (TextView) findViewById(R.id.p1MoneyView);
        p2MoneyView = (TextView) findViewById(R.id.p2MoneyView);

        loseText = (TextView) findViewById(R.id.loseText);
        winText = (TextView) findViewById(R.id.winText);

        winText.setVisibility(View.GONE);
        loseText.setVisibility(View.GONE);
        fold.setVisibility(View.GONE);
        raise.setVisibility(View.GONE);
        stay.setVisibility(View.GONE);

        playPoker.Deck deck = new playPoker.Deck();

        p1Money = 2000;
        p2Money = 2000;
        p1MoneyView.setText("P1 Money :" + p1Money);
        p2MoneyView.setText("P2 Money :" + p2Money);


        if(role.equals("guest")) // for now only host can start game
        {
            play.setVisibility(View.GONE);
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                play.setVisibility(View.GONE); // get rid of play button and start play
                winText.setVisibility(View.GONE);
                loseText.setVisibility(View.GONE);
                fold.setEnabled(true);
                raise.setEnabled(true);
                stay.setEnabled(true);

                deck.shuffle();
                i = 0;
                p2card1 = deck.cards.get(i++);
                p1card1 = deck.cards.get(i++);
                p2card2 = deck.cards.get(i++);
                p1card2 = deck.cards.get(i++); // deal cards

                if(role.equals("host"))
                {
                    int cardResource = getResources().getIdentifier((p1card1.toString()).toLowerCase(),"drawable",getPackageName());
                    card1.setImageResource(cardResource);
                    cardResource = getResources().getIdentifier(p1card2.toString().toLowerCase(),"drawable",getPackageName());
                    card2.setImageResource(cardResource);
                }

                if(role.equals("guest"))
                {
                    int cardResource = getResources().getIdentifier(p2card1.toString().toLowerCase(),"drawable",getPackageName());
                    card3.setImageResource(cardResource);
                    cardResource = getResources().getIdentifier(p2card2.toString().toLowerCase(),"drawable",getPackageName());
                    card4.setImageResource(cardResource);
                }


                p1Money -= 20;
                p1Mon.setValue(p1Money);
                p2Money -= 20;
                p2Mon.setValue(p2Money);
                potMoney = 40;
                potMon.setValue(potMoney);

                p1MoneyView.setText("P1 Money: "+ p1Money);
                p2MoneyView.setText("P2 Money: "+ p2Money);


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



            }




        });

        fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hostTurn)
                {
                    p1Fold = true;

                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        play.setVisibility(View.VISIBLE);
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
                    p2Fold = true;

                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        winText.setVisibility(View.VISIBLE);
                        play.setVisibility(View.VISIBLE);
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
                fold.setEnabled(false);
                raise.setEnabled(false);
                stay.setEnabled(false);
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timesRaised == 1)
                {
                    if(hostTurn)
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
                    i++;
                    if(i<9)
                    {
                        playPoker.Card card = deck.cards.get(i);
                        int resource = getResources().getIdentifier(card.toString().toLowerCase(),"drawable",getPackageName());
                        poolcards[3-i].setImageResource(resource);
                    }
                    else
                    {
                        if(i==10)
                        {
                            //find who won
                        }
                    }
                    return;
                }

                if(hostTurn)
                {
                    p1Money -= 100;
                    p1Mon.setValue(p1Money);
                    potMoney += 100;
                    potMon.setValue(potMoney);
                    Raised.setValue(timesRaised + 1);
                    if(role.equals("host"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                    }
                    turn.setValue(0);
                    if(role.equals("guest"))
                    {
                        stay.setVisibility(View.GONE);
                    }
                }
                else
                {
                    p2Money -= 100;
                    p2Mon.setValue(p2Money);
                    potMoney += 100;
                    potMon.setValue(potMoney);
                    Raised.setValue(timesRaised + 1);
                    if(role.equals("guest"))
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                    }
                    turn.setValue(1);
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
                    if(role.equals("host") && hostTurn)
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        turn.setValue(0);
                        stayed = true;
                        return;
                    }
                    if(role.equals("guest") && !hostTurn)
                    {
                        fold.setVisibility(View.GONE);
                        raise.setVisibility(View.GONE);
                        stay.setVisibility(View.GONE);
                        turn.setValue(1);
                        stayed = true;
                        return;
                    }

            }
        });



        addRoomListener();
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

        Raised.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int value = dataSnapshot.getValue(int.class);
                timesRaised = value;
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
                if(hostTurn && role.equals("guest"))
                {
                    fold.setVisibility(View.GONE);
                    raise.setVisibility(View.GONE);
                    stay.setVisibility(View.GONE);
                }
                if(!hostTurn && role.equals("host"))
                {
                    fold.setVisibility(View.GONE);
                    raise.setVisibility(View.GONE);
                    stay.setVisibility(View.GONE);
                }
                if(!hostTurn && role.equals("guest"))
                {
                    fold.setVisibility(View.VISIBLE);
                    raise.setVisibility(View.VISIBLE);
                    stay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });
    };

}