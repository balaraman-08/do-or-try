package com.example.android.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ClickListener {

    RecyclerView recyclerView;
    String email;
    ArrayList<TodoList> todoListArrayList;
    ArrayList<String> ids;
    TodoAdapter todoAdapter;
    static final String TODO_BASE_URL = LoginActivity.BASE_URL + "todo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Daily Tasks");

        email = getIntent().getStringExtra("email");
        recyclerView = findViewById(R.id.todolist);

        todoListArrayList = new ArrayList<>();
        ids = new ArrayList<>();
        todoAdapter = new TodoAdapter(MainActivity.this, todoListArrayList);
        todoAdapter.setClickListener(this);

        recyclerView.setAdapter(todoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        getList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_add){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = getLayoutInflater().inflate(R.layout.custom_dialog, null);
            final EditText mTitleView = view.findViewById(R.id.newTitle);
            final EditText mDescView = view.findViewById(R.id.newDesc);
            Button ok = view.findViewById(R.id.ok);
            Button cancel = view.findViewById(R.id.cancel);

            builder.setView(view);
            final AlertDialog dialog = builder.create();
            dialog.show();

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(mTitleView.getText().toString().isEmpty() || mDescView.getText().toString().isEmpty())){
                        dialog.dismiss();
                        addToList(mTitleView.getText().toString(), mDescView.getText().toString());
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Enter text", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }

        if (item.getItemId() == R.id.action_sign_out){
            LoginActivity.sp.edit().putString("email", "").apply();
            goToLogin();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToList(final String title, final String desc) {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Adding to your list...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String add_url = TODO_BASE_URL + "add";
        StringRequest request = new StringRequest(Request.Method.POST, add_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("title", title);
                params.put("desc", desc);
                return params;
            }
        };

        queue.add(request);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                progressDialog.dismiss();
            }
        });
    }

    private void goToLogin() {
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(i);
    }

    public void getList(){

        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Getting your list...");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String list_url = TODO_BASE_URL + "list";
        StringRequest request = new StringRequest(Request.Method.POST, list_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray list = new JSONArray(response);
                    for (int i = 0; i < list.length(); ++i){
                        JSONObject item = (JSONObject) list.get(i);
                        String id = item.getString("id");
                        String title = item.getString("title");
                        String desc = item.getString("description");
                        int up = item.getInt("upvote");
                        int down =  item.getInt("downvote");

                        TodoList List = new TodoList(id, title, desc, up, down);
                        if (!ids.contains(id)){
                            ids.add(id);
                            todoListArrayList.add(List);
                        }
                    }
                    todoAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        queue.add(request);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void upVoting(int position) {

        TodoList item = todoListArrayList.get(position);
        int up = item.getUpvotes();
        final int down = item.getDownvotes();
        final String id = item.getId();

        up++;
        todoListArrayList.get(position).setUpvotes(up);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String add_url = TODO_BASE_URL + "edit";
        final int finalUp = up;
        StringRequest request = new StringRequest(Request.Method.PUT, add_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                todoAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("upvote", String.valueOf(finalUp));
                params.put("downvote", String.valueOf(down));
                return params;
            }
        };

        queue.add(request);
    }

    @Override
    public void downVoting(int position) {

        TodoList item = todoListArrayList.get(position);
        final int up = item.getUpvotes();
        int down = item.getDownvotes();
        final String id = item.getId();

        down++;
        todoListArrayList.get(position).setDownvotes(down);

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String add_url = TODO_BASE_URL + "edit";

        final int finalDown = down;
        StringRequest request = new StringRequest(Request.Method.PUT, add_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                todoAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("upvote", String.valueOf(up));
                params.put("downvote", String.valueOf(finalDown));
                return params;
            }
        };

        queue.add(request);
    }

    @Override
    public void deleting(final int position) {

        TodoList item = todoListArrayList.get(position);
        final String id = item.getId();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        String delete_url = TODO_BASE_URL + "delete";

        StringRequest request = new StringRequest(Request.Method.POST, delete_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    todoListArrayList.remove(position);
                    todoAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Check internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        queue.add(request);
    }
}
