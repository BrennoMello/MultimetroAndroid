package ifba.multimetro.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import ifba.multimetro.R;
import ifba.multimetro.bluetooth.ConnectedThread;

public class FluxoBluetooth extends AppCompatActivity {

    String address = null;
    private ProgressDialog progress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private Button atualizar;
    private EditText textoLog;
    private ConnectedThread connectedThreade;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_fluxo_bluetooth);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(address);

        atualizar = (Button) findViewById(R.id.button2);
        textoLog =(EditText) findViewById(R.id.editText);

        new ConectaBluetooth().execute();



        atualizar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             //atualizarLed();
                                             connectedThreade.start();
                                         }
                                     }


        );


    }

    private void atualizarLed(){

        if(btSocket!=null) {
            try {

                btSocket.getOutputStream().write("L".toString().getBytes());
                //InputStream saida = btSocket.getInputStream();
                //byte [] buffer = new byte[2024];
                //saida.read(buffer);

                //String sequencia = new String(buffer);

                //textoLog.setText(sequencia);


            } catch (IOException e) {
                msg("Erro: " + e.getMessage());
            }
        }

    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout

    }


    private class ConectaBluetooth extends AsyncTask<Void,Void,Void>{

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        protected void onPreExecute()
        {
            progress = ProgressDialog.show(FluxoBluetooth.this, "Conectando...", "Aguarde!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                //msg("Erro ao conectar "+e.getMessage());
                Log.v("ERRO conecta bluetooth", e.getMessage());
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Conexão falhou. Tente novamente");
                finish();
            }
            else
            {
                msg("Conectado");
                isBtConnected = true;
                new ConnectedThread(btSocket).start();
            }
            progress.dismiss();
        }




    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }



    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private String msg;
        Charset charset = Charset.forName("US-ASCII");



        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

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
            Charset charset = Charset.forName("US-ASCII");
            //Charset charset = StandardCharsets.US_ASCII;
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    int num = mmInStream.available();
                    Log.i("Dados", String.valueOf(num));
                    buffer = new byte[num];

                    // Read from the InputStream
                    bytes = mmInStream.read(buffer, 0, num);

                    // Send the obtained bytes to the UI activity

                    msg = new String(buffer);
                    FluxoBluetooth.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textoLog.setText(msg.trim());
                        }
                    });

                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.i("ERRO: ",e.getMessage());
                    //break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



}
