package ifba.multimetro.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
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
import ifba.multimetro.bluetooth.ConectaBluetooth;
import ifba.multimetro.bluetooth.ConnectedThread;

public class FluxoBluetooth extends AppCompatActivity {

    private String address = null;
    private ProgressDialog progress;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private TextView leitura;
    private ImageView ohm;
    private ImageView corrente;
    private ImageView tensao;
    //private Button atualizar;
    //private EditText textoLog;
    private ConectaBluetooth conectaBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        address = intent.getStringExtra(DeviceList.EXTRA_ADDRESS);

        setContentView(R.layout.activity_fluxo_bluetooth);

        leitura = (TextView) findViewById(R.id.leitura);
        ohm = (ImageView) findViewById(R.id.ohm);
        corrente = (ImageView) findViewById(R.id.corrente);
        tensao = (ImageView) findViewById(R.id.tensao);

        //atualizar = (Button) findViewById(R.id.button2);
        //textoLog =(EditText) findViewById(R.id.editText);

        conectaBluetooth = new ConectaBluetooth(this);
        conectaBluetooth.setAddress(address);
        conectaBluetooth.execute();

        ohm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conectaBluetooth.getConnectedThreade().write("R");
                Log.v("FluxoBluetooth", "Escrevendo Bytes");
            }
        });

        tensao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conectaBluetooth.getConnectedThreade().write("T");
            }
        });

        corrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conectaBluetooth.getConnectedThreade().write("C");

            }
        });

        /*
        if(conectaBluetooth.isBtConnected()){
            connectedThreade = new ConnectedThread(conectaBluetooth.getBtSocket(),this);
            connectedThreade.start();
        }
        */

        /*
        atualizar.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             //atualizarLed();
                                             connectedThreade.start();
                                         }
                                     }


        );
        */

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


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }



}
