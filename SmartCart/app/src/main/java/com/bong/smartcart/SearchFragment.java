package com.bong.smartcart;

import android.app.ListFragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchFragment extends Fragment {

    String myJSON;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_COUNT = "count";
    private static final String TAG_PLACE = "place";
    private static final String TAG_CATEGORY = "category";

    JSONArray items = null;

    ArrayList<HashMap<String, String>> itemList;

    ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null) ;
        final ListViewAdapter adapter = new ListViewAdapter(this.getContext());
        list = (ListView) view.findViewById(R.id.listView_search) ;
        itemList = new ArrayList<HashMap<String, String>>();
        getData("http://27.35.110.82:3000/items");
        //getData("http://222.104.202.90:3000/items");
        //list.setAdapter(adapter) ;

        /*listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItem mItem = adapter.mListItem.get(i);
            }
        });*/

        Button btn_search = (Button) view.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1, text2="";
                EditText text_search = (EditText) getView().findViewById(R.id.text_search);
                text1 = text_search.getText().toString();
                try {
                    text2 = URLEncoder.encode(text1, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("text", text2);
                getData("http://27.35.110.82:3000/search?name='" + text2 + "'");
            }
        });

        return view;

    }

    protected void showList()
    {
        itemList = new ArrayList<HashMap<String, String>>();
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


}
