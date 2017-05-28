package com.bong.smartcart;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bong.smartcart.bluetooth.BTService;
import com.bong.smartcart.bluetooth.ContextUtil;
import com.bong.smartcart.bluetooth.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


public class PurchaseFragment extends Fragment {

    Handler asdasdasd = new Handler();

    //리스트
    String myJSON;
    ListView list;
    JSONArray items = null;
    public static String addId = "";
    ArrayList<HashMap<String, String>> itemList;

    //블루투스
    private BTService _btService;
    private BluetoothAdapter mAdapter;
    private Context _context;
    private BluetoothAdapter mBTAdapter;
    public static String Adress = "";


    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_COUNT = "count";
    private static final String TAG_PLACE = "place";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_INCOUNT = "incount";

    public Handler handler = new Handler() {
        String str = "";
        @Override
        public void handleMessage(Message msg)
        {

            if (msg.what == 1)
            {
                Log.i("What", "1");
            } else if (msg.what == 2) {
                str = msg.obj.toString().replace("\n", "");
                getData("http://27.35.110.82:3000/search?id='"+ str +"'");
            }

            Log.i("msg", msg.obj.toString());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase, null) ;
        list = (ListView)view.findViewById(R.id.listView_purchase);
        itemList = new ArrayList<HashMap<String, String>>();

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        ContextUtil.CONTEXT = getActivity().getApplicationContext();
        _btService = new BTService(getActivity().getApplicationContext(), handler);


        Button btn_search = (Button) view.findViewById(R.id.testbtn);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Address", Adress);
                BluetoothDevice device = mBTAdapter.getRemoteDevice(Adress);
                Log.i("Device asdfasdfasdf::::", device.toString());
                _btService.connect(device);
            }
        });

        return view;
    }

    protected void showList()
    {

        try
        {
            Log.v("?뚮┝", "Show LIst try");
            JSONObject jsonObj = new JSONObject(myJSON);
            items = jsonObj.getJSONArray(TAG_RESULTS);
            Log.v("?뚮┝", "Show LIst jason");
            for (int i = 0; i < items.length(); i++)
            {
                Log.v("?뚮┝", "Im in For");
                JSONObject c = items.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String name = c.getString(TAG_NAME);
                String price = c.getString(TAG_PRICE);
                String count = c.getString(TAG_COUNT);
                String place = c.getString(TAG_PLACE);
                String category = c.getString(TAG_CATEGORY);

                Log.v("?뚮┝", id + " " + name + " " + price + " " + count + " " + place + " " + category);

                HashMap<String, String> item = new HashMap<String, String>();

                item.put(TAG_ID, id);
                item.put(TAG_NAME, name);
                item.put(TAG_PRICE, price);
                item.put(TAG_COUNT, count);
                item.put(TAG_PLACE, place);
                item.put(TAG_CATEGORY, category);

                itemList.add(item);
            }

            ListAdapter adapter = new SimpleAdapter(
                    this.getContext(), itemList, R.layout.list_item,
                    new String[]{TAG_ID, TAG_NAME, TAG_PRICE, TAG_COUNT, TAG_PLACE, TAG_CATEGORY},
                    new int[]{R.id.list_id, R.id.list_name, R.id.list_price, R.id.list_count, R.id.list_place, R.id.list_category}
            );
            list.setAdapter(adapter);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    public void getData(String url)
    {
        class GetDataJSON extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... params)
            {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try
                {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null)
                    {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e)
                {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result)
            {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

//블루투스 코드

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
       // mAdapter.cancelDiscovery();
        ConnectedThread thread = new ConnectedThread($socket);
        thread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket, because mmSocket is final
            BluetoothSocket tmp = null;
//         mmDevice = device;
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Log.i("dddddddddddddddddd", tmp.toString());
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }


        public void run() {
            // Cancel discovery because it will slow down the connection
            //mAdapter.cancelDiscovery();
            try {
                // Connect the device through the socket. This will block until it succeeds or throws an exception
                mmSocket.connect();
            } catch (Exception e1) {
                Log.e("Purchase.java | run", "|==" + "connect fail" + "|");

                e1.printStackTrace();
                // Unable to connect; close the socket and get out
                try {
                    if (mmSocket.isConnected())
                        mmSocket.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;


//      private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
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

            while (true)
            {
                try
                {
                    byte[] buffer = new byte[16]; // buffer store for the stream
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    sleep(1000);

                    // Send the obtained bytes to the UI Activity
//               mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    Log.i("Purchase.java | run", "|==" + bytes2String(buffer, bytes) + "|");

                    String name = bytes2String(buffer, bytes);
                    Log.i("name", name);
                    //list.setText(name);

                    /*Intent intent = new Intent("kr.mint.bluetooth.receive");
                    intent.putExtra("signal", bytes2String(buffer, bytes));
                    _context.sendBroadcast(intent);*/
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }


        private String bytes2String(byte[] b, int count)
        {
            ArrayList<String> result = new ArrayList<String>();
            /*for (int i = 0; i < count; i++)
            {
                String myInt = b.toString();
                result.add(" " + myInt);
            }*/
//016008130124061
            String str = new String(b);
            return str;
            //return TextUtils.join("-", result);
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
