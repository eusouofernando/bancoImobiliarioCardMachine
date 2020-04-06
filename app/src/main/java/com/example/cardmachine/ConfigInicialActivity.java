package com.example.cardmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ConfigInicialActivity extends AppCompatActivity {


    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    NfcHelper nfcHelper;
    EditText txtNomeJogador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_inicial);


        txtNomeJogador = findViewById(R.id.txtNomeJogador);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcHelper = new NfcHelper();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);


        String nome = txtNomeJogador.getText().toString();

        if (nome.isEmpty()){

            Toast.makeText(this, "O nome é obrigatório", Toast.LENGTH_LONG).show();
        }
        else{

            nfcHelper.eraseTag(ndef);
            nfcHelper.writeInicialValues(ndef,nome,"15000");
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
