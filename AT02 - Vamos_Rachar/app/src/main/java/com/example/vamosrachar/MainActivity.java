package com.example.vamosrachar;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    EditText edtPeople, edtValue;
    TextView tvResult;
    FloatingActionButton share, play;
    TextToSpeech ttsPLayer;
    int people = 2;
    double value = 0.00;
    String formattedResult = "R$ 0,00";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtPeople = (EditText) findViewById(R.id.editPeopleNumber);
        edtValue = (EditText) findViewById(R.id.editValue);
        tvResult = (TextView) findViewById(R.id.textViewResult);

        share = (FloatingActionButton) findViewById(R.id.shareFloatingActionButton);
        play = (FloatingActionButton) findViewById(R.id.playFloatingActionButton);

        EditObserver editObserver = new EditObserver(this);
        edtPeople.addTextChangedListener(editObserver);
        edtValue.addTextChangedListener(editObserver);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttsPLayer != null) {
                    ttsPLayer.speak("O valor por pessoa é de " + formattedResult + " reais.", TextToSpeech.QUEUE_FLUSH, null, "ID1");
                }
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "A conta de R$ " + value + ", dividida para " + people + " pessoas ficou de " + tvResult.getText().toString() + " reais para cada pessoa.");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 1122);

    }

    protected void calculate() {
        DecimalFormat df = new DecimalFormat("#.00");
        try {
            people = Integer.parseInt(edtPeople.getText().toString());
            value = Double.parseDouble(edtValue.getText().toString());
            if (people != 0) {
                formattedResult = df.format(value / people);
                tvResult.setText("R$ "+ formattedResult);
            } else {
                tvResult.setText("R$ 0,00");
            }
        } catch(Exception e) {
            tvResult.setText("R$ 0,00");
            Log.v("PDM", "Valor incorreto");
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS ativado...", Toast.LENGTH_LONG).show();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "TTS não habilitado...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1122) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                ttsPLayer = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
}