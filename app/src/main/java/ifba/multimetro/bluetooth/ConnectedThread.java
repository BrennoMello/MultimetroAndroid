package ifba.multimetro.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Arrays;

import ifba.multimetro.R;
import ifba.multimetro.util.ConversaoLeituras;


/**
 * Created by Brenno-Mello on 16/12/2015.
 */
public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private String leitura;
    private String tipoLeitura;
    private Activity activity;
    private DataInputStream is;
    private DataOutputStream os;
    private String string;
    Charset charset = Charset.forName("US-ASCII");



    public ConnectedThread(BluetoothSocket socket, Activity activity) {

        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        this.activity = activity;


        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.v("ERRO ConnectedThread", e.getMessage());
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run() {


        byte[] buffer;
        int bytes=0;

        while (true) {
            try {


                int num = mmInStream.available();

                buffer = new byte[num];

                bytes = mmInStream.read(buffer, 0, num);

                if(bytes>0) {

                    int tipoDado = 0;
                    for (int i = 0; i < bytes; i++) {
                        if (buffer[i] == '&' && buffer[i + 2] == '#') {
                            tipoDado = i;
                            break;
                        }
                    }


                    int iniDado = 0;
                    int fimDado = 0;
                    for (int i = 0; i < bytes; i++) {

                        if (buffer[i] == '*') {
                            int count = i + 1;
                            while (buffer[count] != '%') {
                                count += 1;
                            }
                            iniDado = i + 1;
                            fimDado = count;
                            break;
                        }
                    }
                    leitura = new String(Arrays.copyOfRange(buffer, iniDado, fimDado));
                    Log.v("Todos os dados", new String(Arrays.copyOfRange(buffer, 0, bytes)));
                    Log.v("Tipo leitura", new String(Arrays.copyOfRange(buffer, tipoDado + 1, tipoDado + 2)));
                    Log.v("Início da transmissão", String.valueOf(tipoDado));
                    Log.v("Tamanho", String.valueOf(buffer.length));


                    tipoLeitura = new String(Arrays.copyOfRange(buffer, tipoDado + 1, tipoDado + 2));
                    float leituraConvertida = ConversaoLeituras.converterLeitura(Float.parseFloat(leitura),tipoLeitura);
                    leitura = String.valueOf(new DecimalFormat("0.0000").format(leituraConvertida));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (leitura.length() > 0) {
                                TextView textLeitura = (TextView) activity.findViewById(R.id.leitura);
                                textLeitura.setText(leitura.trim());

                                TextView textEscala = (TextView) activity.findViewById(R.id.escala);
                                textEscala.setText(ConversaoLeituras.getEscalaLeitura());

                                ImageView imageTensao = (ImageView) activity.findViewById(R.id.tensao);
                                ImageView imageCorrente = (ImageView) activity.findViewById(R.id.corrente);
                                ImageView imageResistencia = (ImageView) activity.findViewById(R.id.ohm);

                                switch (tipoLeitura){
                                    case "T":
                                        imageTensao.setBackgroundColor(activity.getResources().getColor(R.color.vermelho));
                                        imageCorrente.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
                                        imageResistencia.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));

                                    break;
                                    case "C":
                                        imageCorrente.setBackgroundColor(activity.getResources().getColor(R.color.vermelho));
                                        imageResistencia.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
                                        imageTensao.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));

                                    break;
                                    case "R":
                                        imageResistencia.setBackgroundColor(activity.getResources().getColor(R.color.vermelho));
                                        imageCorrente.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));
                                        imageTensao.setBackgroundColor(activity.getResources().getColor(android.R.color.transparent));

                                    break;
                                }

                            }

                        }
                    });

                }
                Thread.sleep(1400);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String bytes) {
        try {
            mmOutStream.write(bytes.toString().getBytes());
            mmOutStream.flush();
        } catch (IOException e) {

        }
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {

        }
    }
}
