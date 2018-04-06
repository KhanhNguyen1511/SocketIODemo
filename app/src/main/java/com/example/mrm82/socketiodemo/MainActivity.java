package com.example.mrm82.socketiodemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edtUserName;
    Button btnCreate;
    ListView listUsers;
    ArrayList<String> userArr;
    ArrayAdapter userAdapter;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.18:3000");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();

        edtUserName=findViewById(R.id.edtUserName);
        btnCreate=findViewById(R.id.btnCreate);
        listUsers=findViewById(R.id.listUsers);
        btnCreate.setOnClickListener(this);
        userArr =new ArrayList<>();
        getUserList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCreate:
                emitToServer();
                break;
        }
    }

    private void emitToServer() {
        String userName = edtUserName.getText().toString().trim();
        mSocket.emit("client_send_username",userName);
        mSocket.on("user_create_result",onNewMessage_UserCreate);
    }

    private void getUserList(){
        mSocket.on("server_send_usernames",onNewMessage_UserList);

    }
    private Emitter.Listener onNewMessage_UserCreate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content;
                    try {
                        content = data.getString("content");
                        if (content=="true"){
                            Toast.makeText(MainActivity.this, "Đăng ký username thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,ChatActivity.class));
                        }else {
                            Toast.makeText(MainActivity.this, "Username đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onNewMessage_UserList = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONArray content;

                    try {
                        content = data.getJSONArray("listUsers");
                            for (int i=0;i<content.length();i++){
                                userArr.add(content.get(i).toString());
                            }
                        userAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,userArr);
                        listUsers.setAdapter(userAdapter);
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };
}
