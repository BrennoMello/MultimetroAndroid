package ifba.multimetro.activity;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import ifba.multimetro.R;
import ifba.multimetro.bluetooth.ConectaBluetooth;


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
                Log.v("FluxoBluetooth", "Escrevendo Bytes");
            }
        });

        corrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                conectaBluetooth.getConnectedThreade().write("C");
                Log.v("FluxoBluetooth", "Escrevendo Bytes");
            }
        });

   }

    private void Disconnect() {

        if (btSocket!=null) {

            try {
                btSocket.close();
            }
            catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }



}
