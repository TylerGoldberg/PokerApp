package com.example.poker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
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

    FirebaseDatabase database;
    DatabaseReference messageRef;

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


        final boolean[] p1Fold = {false};
        final boolean[] p2Fold = {false};



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

        p1Money = 3000;
        p2Money = 3000;
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

                deck.shuffle();
                int i = 0;
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
                p2Money -= 20;
                potMoney = 40;

                p1MoneyView.setText("P1 Money: "+ p1Money);
                p2MoneyView.setText("P2 Money: "+ p2Money);


                boolean playOver = false; // set play over to false
                boolean hostTurn = true;
                boolean guestTurn = false;
                int timesRaised = 0;

                /*
                while(!playOver) // alternate turns until play is over
                {
                    if(hostTurn && role.equals("host"))
                    {
                        fold.setVisibility(View.VISIBLE);
                        raise.setVisibility(View.VISIBLE);
                        stay.setVisibility(View.VISIBLE);

                        if(p1Fold[0])
                        {
                            p2Money += potMoney;
                            if(role.equals("host"))
                            {
                                loseText.setVisibility(View.VISIBLE);
                                play.setVisibility(View.VISIBLE);
                            }
                            if(role.equals("guest"))
                                winText.setVisibility(View.VISIBLE);
                        }
                    }
                    if(guestTurn && role.equals("guest"))
                    {
                        fold.setVisibility(View.VISIBLE);
                        raise.setVisibility(View.VISIBLE);
                        stay.setVisibility(View.VISIBLE);
                    }

                }*/



            }




        });

        /*fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role.equals("host"))
                    p1Fold[0] = true;
                if(role.equals("guest"))
                {
                    p2Fold[0] = true;
                }
            }
        });

        raise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/




    }



}