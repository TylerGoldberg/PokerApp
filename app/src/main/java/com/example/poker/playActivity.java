package com.example.poker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class playActivity extends AppCompatActivity {

    Button raise;
    Button fold;
    Button stay;

    ImageView iv_card1;
    ImageView iv_card2;
    ImageView poolcard_1;
    ImageView poolcard_2;
    ImageView poolcard_3;
    ImageView poolcard_4;
    ImageView poolcard_5;

    TextView playerMoney;
    TextView potMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        fold = findViewById(R.id.fold);
        raise = findViewById(R.id.raise);
        stay = findViewById(R.id.stay);

        iv_card1 = (ImageView) findViewById(R.id.iv_card1);
        iv_card2 = (ImageView) findViewById(R.id.iv_card2);
        poolcard_1 = (ImageView) findViewById(R.id.poolcard_1);
        poolcard_2 = (ImageView) findViewById(R.id.poolcard_2);
        poolcard_3 = (ImageView) findViewById(R.id.poolcard_3);
        poolcard_4 = (ImageView) findViewById(R.id.poolcard_4);
        poolcard_5 = (ImageView) findViewById(R.id.poolcard_5);

        playerMoney = (TextView) findViewById(R.id.playerMoney);
        potMoney = (TextView) findViewById(R.id.potMoney);


        fold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });


    }

}
