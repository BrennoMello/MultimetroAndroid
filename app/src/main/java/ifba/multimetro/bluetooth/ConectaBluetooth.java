package ifba.multimetro.bluetooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by brenno on 22/07/16.
 */

public class ConectaBluetooth extends AsyncTask<Void,Void,Void> {

    private boolean ConnectSuccess = true; //if it's here, it's almost connected
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private BluetoothAdapter myBluetooth = null;
    private ProgressDialog progress;
    private Activity activity;
    private String address = null;
    private ConnectedThread connectedThreade;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConectaBluetooth(Activity activity){
        this.activity = activity;
    }

    protected void onPreExecute() {
      progress = ProgressDialog.show(activity, "Conectando...", "Aguarde!!!");  //show a progress dialog
    }

    @Override
    protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
    {
        try
        {
            if (getBtSocket() == null || !isBtConnected())
            {
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(getAddress());//connects to the device's address and checks if it's available
                setBtSocket(dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID));//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                getBtSocket().connect();//start connection
            }
        }
        catch (IOException e)
        {
            //msg("Erro ao conectar "+e.getMessage());
            Log.v("ERRO conecta bluetooth", e.getMessage());
            ConnectSuccess = false;//if the try failed, you can check the exception here
            progress.dismiss();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
    {
        //super.onPostExecute(result);

        if (!ConnectSuccess)
        {
            this.activity.finish();
            msg("Conexão falhou. Tente novamente");

        }
        else
        {
            msg("Conectado");
            setBtConnected(true);
            setConnectedThreade(new ConnectedThread(this.getBtSocket(),this.activity));
            getConnectedThreade().start();
        }
        progress.dismiss();

    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(this.activity.getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isBtConnected() {
        return isBtConnected;
    }

    public void setBtConnected(boolean btConnected) {
        isBtConnected = btConnected;
    }

    public BluetoothSocket getBtSocket() {
        return btSocket;
    }

    public void setBtSocket(BluetoothSocket btSocket) {
        this.btSocket = btSocket;
    }

    public ConnectedThread getConnectedThreade() {
        return connectedThreade;
    }

    public void setConnectedThreade(ConnectedThread connectedThreade) {
        this.connectedThreade = connectedThreade;
    }
}
