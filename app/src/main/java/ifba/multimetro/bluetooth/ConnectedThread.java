package ifba.multimetro.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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
//import ifba.multimetro.activity.FluxoBluetooth;

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


        // Get the input and output streams, using temp objects because
        // member streams are final
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
        byte[] buffer;  // buffer store for the stream
        int bytes=0; // bytes returned from read()
        //is = new DataInputStream(mmInStream);

        //Charset charset = StandardCharsets.US_ASCII;
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {



                int num = mmInStream.available();
                //Log.i("Dados Leitura", String.valueOf(num));
                buffer = new byte[num];

                // Read from the InputStream
                bytes = mmInStream.read(buffer, 0, num);

                if(bytes>0) {
                    //Log.i("Dados Leitura", leitura = new String(buffer));

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
                                //leitura.concat(String.valueOf(buffer[count]));
                                count += 1;
                                //Log.i("Entrou while", "while");
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
                    //tipoLeitura = ConversaoLeituras.getEscalaLeitura();

                    // Send the obtained bytes to the UI activity
                    //leitura = new String(buffer);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //textoLog.setText(msg.trim());
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
                //Log.v("ERRO: ",e.getMessage());
                //break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String bytes) {
        try {
            mmOutStream.write(bytes.toString().getBytes());
            mmOutStream.flush();
        } catch (IOException e) {

        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {

        }
    }
}
