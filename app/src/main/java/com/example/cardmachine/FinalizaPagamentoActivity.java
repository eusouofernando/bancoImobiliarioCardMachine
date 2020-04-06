package com.example.cardmachine;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class FinalizaPagamentoActivity extends AppCompatActivity {


    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    NfcHelper nfcHelper;
    Integer valorPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finaliza_pagamento);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcHelper = new NfcHelper();

        Intent intent = getIntent();
        valorPagamento =   intent.getIntExtra("valorDoPagamento",0);
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

        if(valorPagamento == 0){

            Toast.makeText(this, "Ocorreu um erro ao salvar o valor do credito", Toast.LENGTH_SHORT).show();
        }
        else{

            Jogador jogador = nfcHelper.readFromNFC(ndef);
            jogador.setDinheiro(jogador.getDinheiro() + valorPagamento);
            nfcHelper.writeInicialValues(ndef,jogador.getNome(),jogador.getDinheiro().toString());
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
