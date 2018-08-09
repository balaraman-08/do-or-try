package com.example.android.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    static SharedPreferences sp;
    static final String BASE_URL = "http://do-or-try.herokuapp.com/";
    LinearLayout login, signin;
    TextInputLayout emailLL, passwordLL, emailLS, passwordLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle(R.string.action_log_in);

        sp = getSharedPreferences("user", MODE_PRIVATE);

        if (!sp.getString("email", "").equals(""))
            goToTodoList(sp.getString("email", ""));

        //Checking internet connectivity
        if (!isInternetConnected()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Internet not connected")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        //Switching login or signin
        login = findViewById(R.id.email_login_form);
        signin = findViewById(R.id.email_signin_form);
        emailLL = findViewById(R.id.emailLL);
        passwordLL = findViewById(R.id.passwordLL);
        emailLS = findViewById(R.id.emailLS);
        passwordLS = findViewById(R.id.passwordLS);

        TextView GoToSignin = findViewById(R.id.toSignin);
        TextView GoToLogin = findViewById(R.id.toLogin);

        GoToSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(R.string.action_sign_in);
                signin.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
            }
        });

        GoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle(R.string.action_log_in);
                login.setVisibility(View.VISIBLE);
                signin.setVisibility(View.GONE);
            }
        });

        //Login process
        final EditText emailViewL, passwordViewL;
        Button login;

        emailViewL = findViewById(R.id.email);
        passwordViewL = findViewById(R.id.password);
        login = findViewById(R.id.button_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailViewL.getText().toString();
                password = passwordViewL.getText().toString();

                if (email.isEmpty()) {
                    emailLL.setError("Fill in the fields");
                } else if (password.isEmpty()){
                    emailLL.setError(null);
                    passwordLL.setError("Fill in the fields");
                } else if (!validateEmail(email)) {
                    emailLL.setError("Enter a valid email");
                    passwordLL.setError(null);
                } else {
                    emailLL.setError(null);
                    passwordLL.setError(null);
                    requestLogin(email, password);
                }
            }
        });

        //Sign in process
        final EditText emailViewS, passwordViewS;
        Button signin;

        emailViewS = findViewById(R.id.emailSignin);
        passwordViewS = findViewById(R.id.passwordSignin);
        signin = findViewById(R.id.button_signin);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = emailViewS.getText().toString();
                password = passwordViewS.getText().toString();

                if (email.isEmpty()) {
                    emailLS.setError("Fill in the fields");
                } else if (password.isEmpty()){
                    emailLS.setError(null);
                    passwordLS.setError("Fill in the fields");
                } else if (!validateEmail(email)) {
                    emailLS.setError("Enter a valid email");
                    passwordLS.setError(null);
                } else {
                    emailLS.setError(null);
                    passwordLS.setError(null);
                    requestSignin(email, password);
                }
            }
        });
    }

    private void requestSignin(final String email, final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        String signin_url = BASE_URL + "signin";

        StringRequest request = new StringRequest(Request.Method.POST, signin_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "success":
                        Toast.makeText(LoginActivity.this, "Sign in Successful", Toast.LENGTH_SHORT).show();
                        sp.edit().putString("email", email).apply();
                        goToTodoList(email);
                        break;

                    case "sign in error":
                        Toast.makeText(LoginActivity.this, "Couldn't sign in. Try again later", Toast.LENGTH_SHORT).show();
                        break;

                    case "user already exists":
                        emailLS.setError("Email already exists");
                        break;

                    case "Something went wrong. Try again later":
                        Toast.makeText(LoginActivity.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
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

    private void requestLogin(final String email, final String password) {

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        String login_url = BASE_URL + "login";

        StringRequest request = new StringRequest(Request.Method.POST, login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                switch (response) {
                    case "success":
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        sp.edit().putString("email", email).apply();
                        goToTodoList(email);
                        break;

                    case "password wrong":
                        emailLL.setError("Credentials mismatch");
                        passwordLL.setError("Credentials mismatch");
                        break;

                    case "user not found":
                        emailLL.setError("User not found");
                        break;

                    case "Something went wrong. Try again later":
                        Toast.makeText(LoginActivity.this, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
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

    private boolean validateEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void goToTodoList(String email) {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        i.putExtra("email", email);
        startActivity(i);
        finish();
    }

    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

}