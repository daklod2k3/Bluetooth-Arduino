package com.locdactran.bluetootharduino;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    Button btTrai,btPhai, btCheck, btClose;
    JoystickView jt;
    TextView t1;
    String address = null, name = null;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{setw();} catch (Exception e) {}
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setw()
    {
        btCheck = findViewById(R.id.btCheck);
        btTrai = findViewById(R.id.btTrai);
        btPhai = findViewById(R.id.btPhai);
        btClose = findViewById(R.id.btClose);
        btClose.setOnTouchListener(this);
        t1 = findViewById(R.id.tl);
        jt = findViewById(R.id.joystick);
        btCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    try {
                        bluetooth_connect_device();
                        btCheck.setText("Close");
                    } catch (IOException e) {
                        t1.setText("Kết nối không thành công");
                        btCheck.setText("Check");
                    }

            }
        });
        btTrai.setOnTouchListener(this);
        btPhai.setOnTouchListener(this);
        jt.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (strength >50) {
                    if (angle>70 && angle<110) {
                        gui((byte) 1);
                    }
                    if (angle>250 && angle<290) {
                        gui((byte) 2);
                    }

                    if (angle>=110 && angle<=180) { //Tien Trai
                        gui((byte) 3);
                    }
                    if (angle>180 && angle<=250) { //Lui Trai
                        gui((byte) 7);
                    }

                    if (angle>=0 && angle<=70){ //Tien Phai
                        gui((byte) 4);
                    }
                    if (angle>=290 && angle<=360){ //Lui Phai
                        gui((byte) 8);
                    }
                }
                if (strength == 0) {
                    gui((byte) 0);
                }
            }
        });

    }

    private void bluetooth_connect_device() throws IOException
    {
        try
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (!myBluetooth.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size()>0)
            {
                for(BluetoothDevice bt : pairedDevices)
                {
                    address=bt.getAddress();name = bt.getName();
                }
            }

        }
        catch(Exception we){}
        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        btSocket.connect();
        try { t1.setText("BT Name: "+name+"\nBT Address: "+address); }
        catch(Exception e){}
    }

    private void gui(Byte i)
    {
        try
        {
            if (btSocket!=null)
            {
                btSocket.getOutputStream().write(i);
            }
        }
        catch (Exception e){}
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP){
            gui((byte) 0);
        } else {
                switch (view.getId()) {
                    case R.id.btTrai:
                        gui((byte) 5);
                        break;
                    case R.id.btPhai:
                        gui((byte) 6);
                        break;
                    case R.id.btClose:
                        System.exit(0);
                        break;
                }
        }
        return false;
    }
}

