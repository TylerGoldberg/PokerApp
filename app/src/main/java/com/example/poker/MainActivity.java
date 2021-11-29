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

    TextView playerMoney;
    TextView potMoney;

    String playerName = "";
    String roomName = "";
    String role = "";

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

        playerMoney = (TextView) findViewById(R.id.playerMoney);
        potMoney = (TextView) findViewById(R.id.potMoney);

        loseText = (TextView) findViewById(R.id.loseText);
        winText = (TextView) findViewById(R.id.winText);

        winText.setVisibility(View.GONE);
        loseText.setVisibility(View.GONE);
        fold.setVisibility(View.GONE);
        raise.setVisibility(View.GONE);
        stay.setVisibility(View.GONE);

        playPoker.Deck deck = new playPoker.Deck();


        if(role.equals("guest"))
        {
            play.setVisibility(View.GONE);
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deck.shuffle();
                int i = 0;


            }
        });

    }



}