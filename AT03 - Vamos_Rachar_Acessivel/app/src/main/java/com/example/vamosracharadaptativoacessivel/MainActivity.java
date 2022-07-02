package com.example.vamosracharadaptativoacessivel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, TextToSpeech.OnInitListener {

    EditText edtValor, edtGrupo;
    TextView resultado;
    FloatingActionButton share, tts;
    TextToSpeech ttsPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        edtValor = (EditText) findViewById(R.id.valor);
        edtGrupo = (EditText) findViewById(R.id.qtdPessoas);
        resultado = (TextView) findViewById(R.id.resultado);
        share = (FloatingActionButton) findViewById(R.id.share);
        tts = (FloatingActionButton) findViewById(R.id.tts);

        edtValor.addTextChangedListener(this);
        edtGrupo.addTextChangedListener(this);

        share.setOnClickListener(this);
        tts.setOnClickListener(this);

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, 1122);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        NumberFormat nf = NumberFormat.getCurrencyInstance();
        try {
            double valor = Double.parseDouble(edtValor.getText().toString());
            int numGrupo = Integer.parseInt(edtGrupo.getText().toString());
            resultado.setText( nf.format(valor / numGrupo) );
        } catch (Exception e){
            resultado.setText(nf.format(0));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1122) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                ttsPlayer = new TextToSpeech(this, this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == share) {
            Intent intent = new Intent (Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Cada pessoa pagará " + resultado.getText().toString());
            startActivity(intent);
        } else if (v == tts) {
            if (ttsPlayer != null) {
                ttsPlayer.speak("Cada pessoa pagará " + resultado.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "ID1");
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(this, "TTS ativado...", Toast.LENGTH_LONG).show();
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sem TTS ativado...", Toast.LENGTH_LONG).show();
        }
    }
}