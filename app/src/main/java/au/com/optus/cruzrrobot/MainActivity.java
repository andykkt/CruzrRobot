package au.com.optus.cruzrrobot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private TextView output;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    private void start() {
        Request request = new Request.Builder().url("ws://10.0.0.81:1337/").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }

    private void sendRobot(WebSocket webSocket) {
        UUID uuid = UUID.randomUUID();
        Robot robot = new Robot();
        robot.name = uuid.toString();
        robot.type = "robot";
        Command command = new Command();
        command.commandId = Command.ADDROBOT;
        command.parameters.add(robot);
        Gson gson = new Gson();
        String commandJson = gson.toJson(command);
        webSocket.send(commandJson);
    }

    private void receivedCommand(int commandId, Action[] parameters) {

    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            sendRobot(webSocket);
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
            try {
                JSONObject received = new JSONObject(text);
                String type = received.getString("type");
                String data = received.getString("data");

                output("type: " + type + " data: " + data);

                JSONObject dataObject = new JSONObject(data);
                String time = dataObject.getString("time");
                String author = dataObject.getString("author");
                String textString = dataObject.getString("text");

                output("time: " + time + " author: " + author + " textString: " + textString);

                JSONObject commandObject = new JSONObject(textString);
                int commandId = commandObject.getInt("commandId");
                String parametersString = commandObject.getString("parameters");

                Gson gson = new Gson();
                Action[] parameters = gson.fromJson(parametersString, Action[].class);
                output("action.type: " + parameters[0].type + " action.parameter: " + parameters[0].parameter);

                receivedCommand(commandId, parameters);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

}

