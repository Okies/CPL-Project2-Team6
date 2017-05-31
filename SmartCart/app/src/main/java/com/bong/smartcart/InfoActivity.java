package com.bong.smartcart;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoActivity extends AppCompatActivity {

    String myJSON;
    JSONArray items = null;

    public static String infoId = "";
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_COUNT = "count";
    private static final String TAG_PLACE = "place";
    private static final String TAG_CATEGORY = "category";
    private static final String TAG_CAPTION = "caption";
    private static final String TAG_MAP = "map";

    ImageView info_image;
    TextView info_category;
    TextView info_name;
    TextView info_price;
    TextView info_info;
    ImageView info_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getData("http://27.35.110.82:3000/search?id='" + infoId + "'");

        setContentView(R.layout.activity_info);
    }

    protected void showList()
    {
        info_image = (ImageView)findViewById(R.id.info_Image);
        info_category = (TextView)findViewById(R.id.info_category);
        info_name = (TextView)findViewById(R.id.info_name);
        info_price = (TextView)findViewById(R.id.info_price);
        info_info = (TextView)findViewById(R.id.info_info);
        info_map = (ImageView)findViewById(R.id.info_map);
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
                String caption = c.getString(TAG_CAPTION);
                String map = c.getString(TAG_MAP);

                Log.v("?뚮┝", id + " " + name + " " + price + " " + count + " " + place + " " + category + " " + caption + " " + map);
                int imageId = getResources().getIdentifier("p"+id, "drawable", "com.bong.smartcart");
                int mapId = getResources().getIdentifier(map, "drawable", "com.bong.smartcart");
                info_category.setText(category);
                info_name.setText(name);
                info_price.setText(price + "원");
                info_info.setText(caption);
                info_image.setImageResource(imageId);
                info_map.setImageResource(mapId);

            }
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
