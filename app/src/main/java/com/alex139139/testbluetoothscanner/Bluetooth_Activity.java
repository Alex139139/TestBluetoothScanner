package com.alex139139.testbluetoothscanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Set;

public class Bluetooth_Activity extends AppCompatActivity implements View.OnClickListener {

    private static int REQUEST_ENABLE_BT = 1;
    private static int DISCOVERABLE_DURATION_120= 120;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter mArrayAdapter;

    //private ArrayList mArrayList = new ArrayList();


    private Button button_Conect;
    private Button button_Scan;
    private ListView ListView_Device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_);
        button_Conect = (Button) findViewById(R.id.button_Conect_id);
        button_Scan = (Button) findViewById(R.id.button_scan_id);
        ListView_Device = (ListView) findViewById(R.id.ListView_Device_id);
    }

    public void EstadoInicial_Bluetooth(){
        if (mBluetoothAdapter.isEnabled()) {
            ((Button) findViewById(R.id.button_Conect_id)).setText(R.string.DesactivarBluetooth);
            //pairedDevicesList();
        } else if (!mBluetoothAdapter.isEnabled()) {
            ((Button) findViewById(R.id.button_Conect_id)).setText(R.string.ActivarBluetooth);
        }
        if(mBluetoothAdapter.isDiscovering()){
            ((Button) findViewById(R.id.button_scan_id)).setText(R.string.detenerBusqueda);
        }
        if(!mBluetoothAdapter.isDiscovering()){
            ((Button) findViewById(R.id.button_scan_id)).setText(R.string.buscar);
        }
    }
    public void funcion_Bluetooth(){

        if (mBluetoothAdapter == null) {
            Toast.makeText(this,"Dispositivo Sin Bluetooth ",Toast.LENGTH_SHORT).show();

        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);


            }
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }
    public  void scanBluetooth(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }
        if(mBluetoothAdapter.isDiscovering()){

            mBluetoothAdapter.cancelDiscovery();
        }else if(!mBluetoothAdapter.isDiscovering()){

            mBluetoothAdapter.startDiscovery();
        }
    }
    //////////////////////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_Conect_id:
                funcion_Bluetooth();
                break;
            case R.id.button_scan_id:
                scanBluetooth();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"Bluetooth On",Toast.LENGTH_SHORT).show();
                //pairedDevicesList();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Bluetooth Off", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == DISCOVERABLE_DURATION_120){
            if (resultCode == DISCOVERABLE_DURATION_120) {
                //Toast.makeText(this,"Buscando...",Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Busqueda cancelada", Toast.LENGTH_SHORT).show();
            }
        }
    }


    ///////////////////////   BroadcastReceiver  /////////////////////////////////////////////////////////////////

    // Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
    // del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
    private final BroadcastReceiver mReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList mArrayList = new ArrayList();
            final String action = intent.getAction();
            // Filtramos por la accion. Nos interesa detectar BluetoothAdapter.ACTION_STATE_CHANGED

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                //mArrayAdapter.notifyDataSetChanged();
                mArrayAdapter = new ArrayAdapter(Bluetooth_Activity.this,android.R.layout.simple_list_item_1, mArrayList);
                ListView_Device.setAdapter(mArrayAdapter);
            }
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                Toast.makeText(Bluetooth_Activity.this,"Iniciando Busqueda ",Toast.LENGTH_SHORT).show();
                ((Button) findViewById(R.id.button_scan_id)).setText(R.string.detenerBusqueda);

            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){ Toast.makeText(Bluetooth_Activity.this,"Busqueda Detenida ",Toast.LENGTH_SHORT).show();

                ((Button) findViewById(R.id.button_scan_id)).setText(R.string.buscar);
            }

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                // Solicitamos la informacion extra del intent etiquetada como BluetoothAdapter.EXTRA_STATE
                // El segundo parametro indicara el valor por defecto que se obtendra si el dato extra no existe
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (estado) {
                    // Apagado
                    case BluetoothAdapter.STATE_OFF:
                    {
                        ((Button)findViewById(R.id.button_Conect_id)).setText(R.string.ActivarBluetooth);
                        Toast.makeText(Bluetooth_Activity.this, "Bluetooth Apagado", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    // Encendido
                    case BluetoothAdapter.STATE_ON:
                    {
                        //pairedDevicesList();
                        ((Button)findViewById(R.id.button_Conect_id)).setText(R.string.DesactivarBluetooth);

                        break;
                    }
                    default:
                        break;
                }
            }
        }


    };

    private void registrar_EventosBluetooth() {

        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filtro.addAction(BluetoothDevice.ACTION_FOUND);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filtro.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver , filtro);
    }



    // Ademas de realizar la destruccion de la actividad, eliminamos el registro del
    // BroadcastReceiver.
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver );
    }

}
