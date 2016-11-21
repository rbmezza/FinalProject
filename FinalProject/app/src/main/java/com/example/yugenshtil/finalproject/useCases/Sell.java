package com.example.yugenshtil.finalproject.useCases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.yugenshtil.finalproject.AboutApp;
import com.example.yugenshtil.finalproject.LoginPage;
import com.example.yugenshtil.finalproject.MainMenu;
import com.example.yugenshtil.finalproject.MySingleton;
import com.example.yugenshtil.finalproject.R;
import com.example.yugenshtil.finalproject.RegistrationPage;
import com.example.yugenshtil.finalproject.UserMenu;
import com.example.yugenshtil.finalproject.adapter.DerpAdapter;
import com.example.yugenshtil.finalproject.item.AddItem;
import com.example.yugenshtil.finalproject.item.EditItem;
import com.example.yugenshtil.finalproject.model.DerpData;
import com.example.yugenshtil.finalproject.model.ItemDisplayActivity;
import com.example.yugenshtil.finalproject.model.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Sell extends Activity  implements DerpAdapter.ItemClickCallback{

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private String GETITEMSURL="http://senecaflea.azurewebsites.net/api/Item/filter/user/";
    private String DELETEITEMSURL="http://senecaflea.azurewebsites.net/api/Item/";
    TextView tvItemsList;
    String myItems = "";

    JSONArray jsonArray=null;
    private String id = "";
    private String fullName="";


    // New
    private RecyclerView recView;
    private DerpAdapter adapter;
 //   private ArrayList listData;

    public ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);

      //  listData = (ArrayList) DerpData.getListData();
   //   Bundle extras = getIntent().getExtras();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    //    Log.d("Oleg", "Preferences " + sharedpreferences);
        id = sharedpreferences.getString("id", "");
        fullName = sharedpreferences.getString("fullName", "");
        Log.d("Oleg","we are here again");

      //  final TextView tvCongratulation = (TextView) findViewById(R.id.sellTVCongratulations);
      //  tvItemsList = (TextView) findViewById(R.id.sellTVitemsList);
        final Button btAddItem = (Button) findViewById(R.id.sellBTaddItem);
        
     //   tvCongratulation.setText(fullName + ", here is the list pf products you sell");
     //   tvItemsList.setText(myItems);
        getMyItems();


            Log.d("Oleg","oool");





        btAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("oleg", "Add new item");
                Intent addItemIntent = new Intent(Sell.this, AddItem.class);
                addItemIntent.putExtra("userId", id);


                startActivity(addItemIntent);

            }
        });


    }

    public void getMyItems(){
        pd = ProgressDialog.show(this, "", "Loading. Please wait...", true);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(Request.Method.GET, GETITEMSURL+id, null, new Response.Listener<JSONArray>() {


            String myItemsList="";

            @Override
            public void onResponse(JSONArray response) {

                pd.cancel();

                if(response!=null){

                    JSONArray items = response;
                    jsonArray = response;
                    if(items!=null) {
                        Log.d("Oleg", "size " + items.length());
                        for (int i = 0; i < items.length(); i++) {
                            try {
                                JSONObject item = (JSONObject) items.get(i);
                                myItemsList+="Title: "+ item.getString("Title")+" Status:"+item.getString("Status")+" Price: " + item.getString("Price")+"\n";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                       // tvItemsList.setText(myItemsList);
                    }


                    recView = (RecyclerView)findViewById(R.id.rec_list);
                    //LayoutManager: GridLayoutManager or StaggeredGridLayoutManager

                    recView.setLayoutManager(new LinearLayoutManager(Sell.this));
                    //Send list here
                //WAS   adapter = new DerpAdapter(DerpData.getListData(),Sell.this,jsonArray);

                       adapter = new DerpAdapter(Sell.this,jsonArray);

                    Log.d("Oleg","Setting adapter");
                    recView.setAdapter(adapter);
                    adapter.setItemClickCallback(Sell.this);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
                    itemTouchHelper.attachToRecyclerView(recView);

                    Button addItem = (Button) findViewById(R.id.sellBTaddItem);
                    addItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("Oleg","Add new clicked");
                            Intent loginIntent = new Intent(Sell.this,AddItem.class);
                            startActivity(loginIntent);
                        }
                    });



                }else{
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "JSON RETURNED NULL", duration);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pd.cancel();
                Log.d("Oleg","error" + error);

            }
        });

        MySingleton.getInstance(Sell.this).addToRequestQueue(jsObjRequest);

    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };
        return simpleItemTouchCallback;
    }

    private void addItemToList() {
       // ListItem item = DerpData.getRandomListItem();
      //  listData.add(item);
        //adapter.notifyItemInserted(listData.indexOf(item));
    }

    private void moveItem(int oldPos, int newPos) {

      //  ListItem item = (ListItem) listData.get(oldPos);
      //  listData.remove(oldPos);
      //  listData.add(newPos, item);
        adapter.notifyItemMoved(oldPos, newPos);
    }

    private void deleteItem(final int position) {
      //  listData.remove(position);
      //  adapter.notifyItemRemoved(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sell, menu);
        Log.d("Oleg","onCreate hrer");
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
        else if(id==R.id.action_logout){
            SharedPreferences preferences = getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            Intent mainMenuIntent = new Intent(Sell.this,MainMenu.class);
            startActivity(mainMenuIntent);



        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int p) {

        try {
            JSONObject item = (JSONObject) jsonArray.get(p);
            Intent i  = new Intent(this, ItemDisplayActivity.class);
            Bundle extras = new Bundle();
            extras.putString("ItemId",item.get("ItemId").toString());
            extras.putString("Title",item.get("Title").toString());
            extras.putString("SellerId",item.get("SellerId").toString());
            extras.putString("Description",item.get("Description").toString());
            extras.putString("Price",item.get("Price").toString());
            i.putExtras(extras);
            startActivity(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpdateIconClick(int p) {
        try {
            JSONObject item = (JSONObject)jsonArray.get(p);
           // String updateItemId = item.getString("ItemId");
            Log.d("Oleg","you would like to update id " + p);
            updateAnItem(p);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    //    adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }

    public void onDeleteIconClick(int p) {

        try {
            JSONObject item = (JSONObject)jsonArray.get(p);
            String deleteItemId = item.getString("ItemId");
            Log.d("Oleg","you would like to delete it id " + deleteItemId);
            deleteAnItem(deleteItemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


      //  ListItem item = (ListItem) listData.get(p);
        //update our Data

    //    if(item.isFavourite()){
     //       item.setFavourite(false);

    //    }else
       //     item.setFavourite(true);

        Log.d("Oleg","onSecondaryIconClick");

        //    adapter.setListData(listData);
        adapter.notifyDataSetChanged();
    }


    public void deleteAnItem(String id){
        pd = ProgressDialog.show(this, "", "Loading. Please wait...", true);
        Log.d("Oleg","Gonna delete");
        StringRequest dr = new StringRequest(Request.Method.DELETE, DELETEITEMSURL+id,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                       pd.cancel();
                       Log.d("Oleg","Response is "+response);
                    //    adapter.notifyDataSetChanged();
                        Intent aboutAppIntent = new Intent(Sell.this,Sell.class);
                        startActivity(aboutAppIntent);


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.cancel();
                        Log.d("Oleg","Response error is "+error);

                    }
                }
        );
        MySingleton.getInstance(Sell.this).addToRequestQueue(dr);

    }


    public void updateAnItem(int id) {
       // pd = ProgressDialog.show(this, "", "Loading. Please wait...", true);

        try {
            JSONObject item = (JSONObject) jsonArray.get(id);
            Intent i = new Intent(Sell.this, EditItem.class);
            i.putExtra("ItemId",item.get("ItemId").toString());
            i.putExtra("Title",item.get("Title").toString());
            i.putExtra("SellerId",item.get("SellerId").toString());
            i.putExtra("Description",item.get("Description").toString());
            i.putExtra("Price",item.get("Price").toString());
            startActivity(i);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(Sell.this, UserMenu.class));
        finish();

    }




}
