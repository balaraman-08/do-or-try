//package com.example.android.login;
//
///**
// * Created by BALARAMAN on 28-07-2018 with love.
// */
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by ADITHYA AN on 26-02-2018.
// */
//
//public class CheckUserTask extends AsyncTask
//{
//    Context context;
//    CheckUser checkUser;
//    ProgressDialog progressDialog;
//
//
//    public void setCheckUser(CheckUser checkUser)
//    {
//        this.checkUser = checkUser;
//    }
//
//    public CheckUserTask(Context context)
//    {
//        this.context=context;
//    }
//
//
//
//    public void checkUserExists()
//    {
//        StringRequest stringRequest=new StringRequest(Request.Method.POST, context.getResources().getString(R.string.checkUser_ep), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("response",response);
//
//                try {
//                    JSONObject jsonObject=new JSONObject(response);
//                    Toast.makeText(context, "res"+jsonObject, Toast.LENGTH_SHORT).show();
//                    JSONObject jsonObject1=jsonObject.getJSONObject("message");
//                    String status=jsonObject.get("error").toString();
//                    if (status=="false")
//                    {
//                        if (checkUser!=null){
//                            checkUser.isUserExists(jsonObject1);
//                        }
//                    }else{
//                        checkUser.userNotExists();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                progressDialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            /**
//             * Callback method that an error has been occurred with the
//             * provided error code and optional user-readable message.
//             *
//             * @param error
//             */
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "err"+error, Toast.LENGTH_SHORT).show();
//                if (error.getMessage()!=null)
//                    Log.e("error","hai"+error.getMessage());
//
//                progressDialog.dismiss();
//            }
//        }){
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                String str_ph=context.getSharedPreferences("ph_num_pref",Context.MODE_PRIVATE).getString("ph","");
//
//                Map<String,String> map=new HashMap<String,String>();
//                map.put("mobile",str_ph);
//                return map;
//            }
//        };
//
//        RequestQueue requestQueue= Volley.newRequestQueue(context);
//        requestQueue.add(stringRequest);
//
//    }
//
//
//    @Override
//    protected Object doInBackground(Object[] objects) {
//        checkUserExists();
//        return null;
//    }
//
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progressDialog=new ProgressDialog(context);
//        progressDialog.setMessage("creating account please wait");
//        progressDialog.show();
//
//    }
//
//
//    @Override
//    protected void onPostExecute(Object o) {
//        super.onPostExecute(o);
//    }
//}