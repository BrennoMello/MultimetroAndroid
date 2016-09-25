package ifba.multimetro.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.List;

import ifba.multimetro.R;


/**
 * Created by brenno on 21/07/16.
 */
public class ConexaoBluetoothAdapter extends BaseAdapter {

    Context context;
    List<BluetoothDevice> devicesPareid;

    public ConexaoBluetoothAdapter(Context context, List<BluetoothDevice> devicesPareid){
        this.context = context;
        this.devicesPareid = devicesPareid;

    }

    public int getCount(){
        return devicesPareid.size();
    }

    public Object getItem(int position){
        return devicesPareid.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View ConvertView, ViewGroup parent){
        BluetoothDevice bluetoothDevice = devicesPareid.get(position);

        View linha = LayoutInflater.from(context).inflate(R.layout.spinner_custom,null);

        TextView textView = (TextView) linha.findViewById(R.id.textView);

        textView.setText(bluetoothDevice.getName());


        return linha;
    }
}
