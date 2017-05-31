package com.bong.smartcart;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bong.smartcart.bluetooth.BTPay;
import com.bong.smartcart.bluetooth.BTService;
import com.bong.smartcart.bluetooth.ContextUtil;
import com.bong.smartcart.bluetooth.PreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;


public class PurchaseFragment extends Fragment {

    Handler asdasdasd = new Handler();
    OutputStream mOutputStream = null;

    //리스트
    String myJSON;
    ListView list;
    JSONArray items = null;
    public static String addId = "";
    ArrayList<HashMap<String, Object>> itemList;
    Button btn_pay;
    TextView text_total;

    //블루투스
    private BTService _btService;
    private BTPay _BTPay;
    private BluetoothAdapter mAdapter;
    private Context _context;
    private BluetoothAdapter mBTAdapter;
    public static String Adress = "20:16:10:10:80:32";
    public static String PayAdress = "20:16:10:10:93:18";

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_OPRICE = "oprice";
    private static final String TAG_PLACE = "place";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_INCOUNT = "incount";
    private static final String TAG_IMAGEID = "imageId";
    private static final String TAG_BUTTON = "btn";

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
        itemList = new ArrayList<HashMap<String, Object>>();
        btn_pay = (Button)view.findViewById(R.id.btn_pay);
        text_total = (TextView)view.findViewById(R.id.text_total);

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        ContextUtil.CONTEXT = getActivity().getApplicationContext();
        _btService = new BTService(getActivity().getApplicationContext(), handler);

        Log.i("Address", Adress);
        BluetoothDevice device = mBTAdapter.getRemoteDevice(Adress);
        Log.i("Device asdfasdfasdf::::", device.toString());
        _btService.connect(device);

        if(LoginActivity.islogin == 1) {
            btn_pay.setVisibility(View.VISIBLE);
        }
        else {
            btn_pay.setVisibility(View.INVISIBLE);
        }
        btn_pay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivity(serverIntent);
                BluetoothDevice device = mBTAdapter.getRemoteDevice(PayAdress);
                _BTPay = new BTPay(device);
                new AlertDialog.Builder(getActivity())
                        .setTitle("결제하시겠습니까?")
                        .setMessage("총금액 : " + Integer.toString(getTotal()) + "원")
                        .setPositiveButton("결제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                _BTPay.sendPay("open");
                                sendData("http://27.35.110.82:3000/pay");
                                Intent result = new Intent(getActivity(), MemberActivity.class);
                                startActivity(result);
                                SystemClock.sleep(5000);
                                _BTPay.sendPay("close");
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface arg0, int arg1){
                                _BTPay.sendPay("close");
                            }
                        })
                        .show();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView parent, final View view,
                                    int position, long id) {
                itemList.get(position).put(TAG_INCOUNT, Integer.toString(Integer.parseInt((String)itemList.get(position).get(TAG_INCOUNT))-1));
                itemList.get(position).put(TAG_PRICE, Integer.toString(Integer.parseInt((String)itemList.get(position).get(TAG_OPRICE))
                        * Integer.parseInt((String) itemList.get(position).get(TAG_INCOUNT))) + "원");
                if(Integer.parseInt((String)itemList.get(position).get(TAG_INCOUNT)) == 0) {
                    itemList.remove(itemList.get(position));
                }
                Log.v("111111111111111111", "11111111111111111111");
                //final String item = (String) parent.getItemAtPosition(position);
                Log.v("purchase.java", Integer.toString(position));
                showList(0);
                text_total.setText(Integer.toString(getTotal()) + "원");
                return false;
            }
        });

        return view;
    }


    protected void showList(int wtf)
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
                String oprice = c.getString(TAG_PRICE);
                String place = c.getString(TAG_PLACE);
                String category = c.getString(TAG_CATEGORY);
                int incount = 1;
                int imageId = getResources().getIdentifier("p"+id, "drawable", "com.bong.smartcart");


                Log.v("?뚮┝", id + " " + name + " " + price + " " + place + " " + category + " " + incount);

                HashMap<String, Object> item = new HashMap<String, Object>();

                item.put(TAG_ID, id);
                item.put(TAG_NAME, name);
                item.put(TAG_PRICE, price + "원");
                item.put(TAG_OPRICE, oprice);
                item.put(TAG_PLACE, category + "/" + place);
                item.put(TAG_INCOUNT, Integer.toString(incount));
                item.put(TAG_IMAGEID, getResources().getDrawable(imageId));

                int size = itemList.size();
                int flag = 1;
                if(wtf == 1) {
                    for (int n = 0; n < size; n++) {
                        String s = (String) itemList.get(n).get(TAG_ID);
                        if (s.equals(id)) {
                            itemList.get(n).put(TAG_INCOUNT, Integer.toString(Integer.parseInt((String) itemList.get(n).get(TAG_INCOUNT)) + 1));
                            itemList.get(n).put(TAG_PRICE, Integer.toString(Integer.parseInt((String)itemList.get(n).get(TAG_OPRICE))
                                    * Integer.parseInt((String) itemList.get(n).get(TAG_INCOUNT))) + "원");
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 1) itemList.add(item);
                }
            }

            ListAdapter adapter = new SimpleAdapter(
                    this.getContext(), itemList, R.layout.list_pitem,
                    new String[]{TAG_NAME, TAG_PRICE, TAG_PLACE, TAG_INCOUNT, TAG_IMAGEID},
                    new int[]{R.id.list_name, R.id.list_price, R.id.list_place, R.id.list_incount, R.id.list_Image}
            );
            ((SimpleAdapter) adapter).setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if(view.getId() == R.id.list_Image) {
                        Log.d("BBBBiiiiiiiiiii", "BVVVVVVVVVVVVV");
                        ImageView imageView = (ImageView) view;
                        Drawable drawable = (Drawable) data;
                        imageView.setImageDrawable(drawable);
                        return true;
                    }
                    return false;
                }
            });


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
                showList(1);
                text_total.setText(Integer.toString(getTotal()) + "원");
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public int getTotal() {
        int total = 0;
        int size = itemList.size();
            for (int n = 0; n < size; n++) {
                total += (Integer.parseInt((String) itemList.get(n).get(TAG_OPRICE)) * Integer.parseInt((String) itemList.get(n).get(TAG_INCOUNT)));
            }
        return total;
    }

    public void sendData(String url)
    {
        class sendDataJSON extends AsyncTask<String, Void, String>
        {
            @Override // URL 연결이 구현될 부분
            protected String doInBackground(String... params)
            {
                String uri = params[0];
                String response = null;
                HttpURLConnection conn = null;
                try
                {
                    URL url = new URL(uri);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    JSONObject obj = new JSONObject();
                    JSONArray ids = new JSONArray();
                    try {
                        for (int i = 0; i < itemList.size(); i++) {
                            JSONObject item = new JSONObject();
                            item.put("id", itemList.get(i).get(TAG_ID));
                            item.put("name", itemList.get(i).get(TAG_NAME));
                            item.put("count", itemList.get(i).get(TAG_INCOUNT));
                            ids.put(item);
                        }
                        obj.put("items", ids);
                        obj.put("userID", LoginActivity.logined_id);

                        Log.i("LLLLLLLLLLL:::::::", obj.toString());
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    bw.write(obj.toString());
                    bw.flush();
                    bw.close();
                    os.close();

                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if(sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line);
                    }
                    String req = sb.toString();

                    //if(req.equals("OK"))
                    //    islogin = 1;

                    return req;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(conn != null)
                        conn.disconnect();
                }
                return response;
            }

            @Override // UI 업데이트가 구현될 부분
            protected void onPostExecute(String result)
            {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        }
        sendDataJSON g = new sendDataJSON();
        g.execute(url);
    }

    void sendPay(String msg) {
        try{
            mOutputStream.write(msg.getBytes());  // 문자열 전송.
        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우

        }
    }
}
