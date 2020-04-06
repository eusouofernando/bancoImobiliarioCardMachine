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

public class OperDebitoActivity extends AppCompatActivity {


    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    NfcHelper nfcHelper;
    EditText valorDebito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oper_debito);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcHelper = new NfcHelper();

        valorDebito  = findViewById(R.id.valorDebito);
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

        String vlrDebito = valorDebito.getText().toString();
        if (vlrDebito.isEmpty()){

            Toast.makeText(this, "O valor do debito é obrigatório", Toast.LENGTH_SHORT).show();
        }
        else {

            Jogador jogador = nfcHelper.readFromNFC(ndef);
            if (jogador.getDinheiro() < Integer.parseInt(vlrDebito)){

                Toast.makeText(this, "Saldo insulficiente!!", Toast.LENGTH_SHORT).show();
            }else{

                jogador.setDinheiro(jogador.getDinheiro() - Integer.parseInt(vlrDebito));
                nfcHelper.writeInicialValues(ndef,jogador.getNome(),jogador.getDinheiro().toString());
                startActivity(new Intent(getBaseContext(), MainActivity.class));
            }
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
