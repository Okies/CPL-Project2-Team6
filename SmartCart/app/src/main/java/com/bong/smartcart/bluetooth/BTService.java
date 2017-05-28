package com.bong.smartcart.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BTService extends Service
{
    private BluetoothAdapter mAdapter;
    private Context _context;
    Handler handler;


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public BTService(Context $context, Handler handler)
    {
        super();
        this.handler = handler;
        _context = $context;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 주소로 연결하기
     *
     * @param $address
     *           mac address
     */
    public void connect(String $address)
    {
        BluetoothDevice device = mAdapter.getRemoteDevice($address);
        connect(device);
    }


  public void connect(BluetoothDevice $device)
    {
        ConnectThread thread = new ConnectThread($device);
        thread.start();
    }


    private void manageConnectedSocket(BluetoothSocket $socket)
    {
       // Log.i("BTService.java | manageConnectedSocket", "|==" + $socket.getRemoteDevice().getName() + "|" + $socket.getRemoteDevice().getAddress());
        PreferenceUtil.putLastRequestDeviceAddress($socket.getRemoteDevice().getAddress());
        mAdapter.cancelDiscovery();
        ConnectedThread thread = new ConnectedThread($socket, handler);
        thread.start();
    }

    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;


        public ConnectThread(BluetoothDevice device)
        {
            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            BluetoothSocket tmp = null;
//         mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try
            {
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }
            catch (IOException e)
            {
            }
            mmSocket = tmp;
        }


        public void run()
        {
            // Cancel discovery because it will slow down the connection
            mAdapter.cancelDiscovery();
            try
            {
                // Connect the device through the socket. This will block until it succeeds or throws an exception
                mmSocket.connect();
            }
            catch (Exception e1)
            {
                Log.e("BTService.java | run", "|==" + "connect fail" + "|");

                e1.printStackTrace();
                // Unable to connect; close the socket and get out
                try
                {
                    if (mmSocket.isConnected())
                        mmSocket.close();
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }


        /** Will cancel an in-progress connection, and close the socket */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        Handler handler;


//      private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, Handler handler)
        {
            this.handler = handler;
            mmSocket = socket;
            InputStream tmpIn = null;
//         OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because member streams are final
            try
            {
                tmpIn = socket.getInputStream();
//            tmpOut = socket.getOutputStream();
            }
            catch (IOException e)
            {
            }
            mmInStream = tmpIn;
//         mmOutStream = tmpOut;
        }


        public void run()
        {
            //byte[] buffer = new byte[20]; // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            int flag = 1;
            while (true)
            {
                try
                {
                    byte[] buffer = new byte[16]; // buffer store for the stream
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    sleep(1000);

                    String id = new String(buffer);
                    Log.i("BTService.java | run", "|" + id + "|" + Integer.toString(id.length()));

                    if(id.charAt(15) == '\n')
                        flag = 2;

                    Message msg = handler.obtainMessage(flag, id);
                    handler.sendMessage(msg);

                    flag = 1;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }


      /* Call this from the main Activity to send data to the remote device */
//      public void write(byte[] bytes)
//      {
//         try
//         {
//            mmOutStream.write(bytes);
//         }
//         catch (IOException e)
//         {
//         }
//      }

        /* Call this from the main Activity to shutdown the connection */
        public void cancel()
        {
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
            }
        }
    }
}
