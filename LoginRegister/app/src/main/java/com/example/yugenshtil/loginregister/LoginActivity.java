package com.example.yugenshtil.loginregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final Button bGet = (Button) findViewById(R.id.bGet);
        final Button bPost = (Button) findViewById(R.id.bPost);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);

            }
        });

        bGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tx = (TextView) findViewById(R.id.jsonRes);
                String url = "https://jsonplaceholder.typicode.com/posts/1";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Oleg","Response is " + response);
                                tx.setText(response.toString());
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                         Log.d("Oleg","error" + error);

                            }
                        });

                MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsObjRequest);

                Log.d("Oleg", "clicked");
            }
        });

        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tx = (TextView) findViewById(R.id.jsonRes);
                String url = "http://omytryniuk.net23.net/test.php";

                Map<String, String> params = new HashMap();
                params.put("first_param", "Seneca");
                params.put("second_param", "2");

                JSONObject parameters = new JSONObject(params);

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("Oleg","Response is " + response);
                                tx.setText(response.toString());
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub

                            }
                        });

                MySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsObjRequest);

                Log.d("Oleg", "clickedPOST");
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                       @Override
                    public void onResponse(String response) {
                          try {
                              JSONObject jsonResponse = new JSONObject(response);
                              boolean success = jsonResponse.getBoolean("success");
                              if(success){

                                  String name = jsonResponse.getString("name");
                                  int age = jsonResponse.getInt("age");

                                  Intent intent = new Intent(LoginActivity.this,UserAreaActivity.class);
                                  intent.putExtra("name", name);
                                  intent.putExtra("username", username);
                                  Log.d("oleg", "name is" + username);
                                  intent.putExtra("age", age);

                                  LoginActivity.this.startActivity(intent);
                              }
                              else{
                                  AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                  builder.setMessage("Login Failed").setNegativeButton("Retry",null).create().show();

                              }

                          } catch (JSONException e) {
                              e.printStackTrace();
                          }
                         }
                     };
                        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                        queue.add(loginRequest);

                }



                });






    }


    public class ActorsAsyncTask extends AsyncTask<String, Void,Boolean>{


        @Override
        protected Boolean doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();



            return null;
        }

        @Override
        protected void onPostExecute(Boolean result){

            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
