package ifba.multimetro.activity;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ifba.multimetro.R;
import ifba.multimetro.adapter.ConexaoBluetoothAdapter;

public class DeviceList extends AppCompatActivity {

    private Button btConecta;
    private Button btBuscar;
    private Spinner comboBox;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_device_list);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        btConecta = (Button) findViewById(R.id.conectar);
        btBuscar = (Button) findViewById(R.id.buscar);
        comboBox = (Spinner) findViewById(R.id.spinner);


        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null){

            Toast.makeText(getApplicationContext(), "Não existe suporte para bluetooth", Toast.LENGTH_LONG).show();
            finish();
        }else if(!myBluetooth.isEnabled()){

            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }

        btConecta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = (Spinner) findViewById(R.id.spinner);
                BluetoothDevice bluetoothDevice = (BluetoothDevice) spinner.getSelectedItem();
                if(bluetoothDevice != null){
                    Intent intent = new Intent(DeviceList.this, FluxoBluetooth.class);
                    intent.putExtra(EXTRA_ADDRESS, bluetoothDevice.getAddress());
                    startActivity(intent);
                }else
                    Toast.makeText(getApplicationContext(), "Dispositivo não selecionado", Toast.LENGTH_LONG).show();
            }
        });
        btBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairedDevicesList();
            }
        });
    }

    private void pairedDevicesList(){
        pairedDevices = myBluetooth.getBondedDevices();
        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Nenhum dispositivo encontrado", Toast.LENGTH_LONG).show();
        }

        ConexaoBluetoothAdapter adapter = new ConexaoBluetoothAdapter(this,list);
        comboBox.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
