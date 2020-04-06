package com.example.cardmachine;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcel;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;


public class NfcHelper {

    public void writeInicialValues(Ndef ndef, String nomeJogador, String valorInicial) {
        try {

                NdefRecord[] records = new NdefRecord[]{
                         NdefRecord.createMime("text/plain", nomeJogador.getBytes(Charset.forName("UTF-8"))),
                         NdefRecord.createMime("text/plain", valorInicial.getBytes(Charset.forName("UTF-8")))
                };

                writeToNfc(ndef, records);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToNfc(Ndef ndef, NdefRecord[] records)  {

        //mTvMessage.setText(getString(R.string.message_write_progress));

        if (ndef != null) {

            try {
                ndef.connect();
                ndef.writeNdefMessage(new NdefMessage(records));
                ndef.close();

            } catch (IOException | FormatException e) {
                e.printStackTrace();
                //mTvMessage.setText(getString(R.string.message_write_error));

            } finally {
                //mProgress.setVisibility(View.GONE);
            }
        }
    }

    public Jogador readFromNFC(Ndef ndef) {

        Jogador jogador = new Jogador();

        try {

            ndef.connect();

            NdefMessage ndefMessage = ndef.getNdefMessage();
            jogador.setNome(new String(ndefMessage.getRecords()[0].getPayload()));
            jogador.setDinheiro(Integer.parseInt(new String(ndefMessage.getRecords()[1].getPayload())));

            ndef.close();

        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }

        return jogador;
    }

    public void eraseTag(Ndef ndef){

        try {
            //NdefRecord[] records = {
             //       new NdefRecord(NdefRecord.TNF_EMPTY, new byte[0], new byte[0], new byte[0])
            //};
            ndef.connect();
            ndef.writeNdefMessage(new NdefMessage(new NdefRecord(NdefRecord.TNF_EMPTY, null, null, null)));
            ndef.close();
        }
        catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
