package com.example.mrm82.socketiodemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by mrm82 on 01/04/2018.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    ListView listMessage;
    EditText edtMessage;
    Button btnSend;
    ArrayList<String>chatArr;
    ArrayAdapter chatAdapter;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.1.18:3000");
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mSocket.connect();
        listMessage = findViewById(R.id.listChat);
        edtMessage=findViewById(R.id.edtMessage);
        btnSend=findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        chatArr =new ArrayList<>();
        mSocket.on("server_send_chat_message",onNewMessage_ChatMessages);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSend:
                sendMessage();
        }
    }

    private void sendMessage() {
        String message = edtMessage.getText().toString().trim();
        mSocket.emit("client_send_message",message);
    }


    private Emitter.Listener onNewMessage_ChatMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String content;
                    try {
                        content = data.getString("chatMessages");
                        chatArr.add(content);
                        chatAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,chatArr);
                        listMessage.setAdapter(chatAdapter);
                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };
}
