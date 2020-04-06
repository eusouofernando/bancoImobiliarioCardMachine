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

public class PagamentoActivity extends AppCompatActivity {

    PendingIntent pendingIntent;
    NfcAdapter nfcAdapter;
    NfcHelper nfcHelper;
    EditText nbValorPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        nfcHelper = new NfcHelper();

        nbValorPagamento = findViewById(R.id.nbValorPagamento);
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

        if (nbValorPagamento.getText().toString().isEmpty()) {

            Toast.makeText(this, "O valor do pagamento é obrigatório", Toast.LENGTH_SHORT).show();
        }else if(Integer.parseInt(nbValorPagamento.getText().toString()) == 0){

            Toast.makeText(this, "O valor não pode ser ZERO", Toast.LENGTH_SHORT).show();
        }else{

            Integer valorPagamento = Integer.parseInt(nbValorPagamento.getText().toString());

            Jogador jogador = nfcHelper.readFromNFC(ndef);
            if (jogador.getDinheiro() < valorPagamento){

                Toast.makeText(this, "Saldo insulficiente!!", Toast.LENGTH_SHORT).show();
            }else{



                jogador.setDinheiro(jogador.getDinheiro() - valorPagamento);
                nfcHelper.writeInicialValues(ndef,jogador.getNome(),jogador.getDinheiro().toString());

                Intent novaIntent = new Intent(this, FinalizaPagamentoActivity.class);
                novaIntent.putExtra("valorDoPagamento", valorPagamento);
                startActivity(novaIntent);
            }
        }



    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }
}
