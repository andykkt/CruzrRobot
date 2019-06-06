package au.com.optus.cruzrrobot;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.ISpeechContext;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //private TextView output;
    private OkHttpClient client;
    private Button showbar;
    private ApiControl controller;
    private Boolean isHideBar = false;
    private void showHideBar() {
        if (isHideBar == true) {
            sendBroadcast(new Intent("com.ubt.cruzr.showbar"));
            isHideBar = false;
        } else {
            sendBroadcast(new Intent("com.ubt.cruzr.hidebar"));
            isHideBar = true;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.showbar:
                showHideBar();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //output = (TextView) findViewById(R.id.output);
        this.setContentView(R.layout.activity_main);
        showbar = (Button) findViewById(R.id.showbar);
        showbar.setOnClickListener(this);
        controller = new ApiControl(this);
        client = new OkHttpClient();
        start();
        showHideBar();

        VideoView videoView = (VideoView) findViewById(R.id.video_view);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.eyes));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        //videoView.setZOrderOnTop(true);
        videoView.start();

        controller.disableWakeup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpeechRobotApi.get().destory();
        RosRobotApi.get().destory();
        sendBroadcast(new Intent("com.ubt.cruzr.showbar"));
    }

    private void start() {
        Request request = new Request.Builder().url("ws://54.252.186.106:1337/").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void prepareRobot() {

        //controller.setVolume(300);
        controller.TtsPlay("I am ready");
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //output.setText(output.getText().toString() + "\n\n" + txt);
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
        switch (commandId) {
            case Command.EXECUTESIMPLE:
                executeSimple(parameters[0].type, parameters[0].parameter);
                break;
            case Command.EXECUTEMACRO:
                break;
            default:
                break;
        }
    }

    private void executeSimple(int actionId, String parameter) {
        switch (actionId) {
            case Action.MOVEFORWARD:
                controller.moveForward(Float.parseFloat(parameter));
                break;
            case Action.MOVEBACKWARD:
                controller.moveBackward(Float.parseFloat(parameter));
                break;
            case Action.TURNLEFT:
                controller.turnLeft(Float.parseFloat(parameter));
                break;
            case Action.TURNRIGHT:
                controller.turnRight(Float.parseFloat(parameter));
                break;
            case Action.STOPMOVE:
                controller.stopMove();
                break;
            case Action.SPEECH:
                controller.TtsPlay(parameter);
                break;
            case Action.SPEECHVOLUME:
                controller.setVolume(Integer.parseInt(parameter));
                break;
            case Action.NAVIGATETO:
                controller.setNavigateTo(parameter);
                break;

            case Action.FACE:
                controller.setFace(parameter);
                break;

            case Action.ACTION:
                controller.setAction(parameter);
                break;

            case Action.DANCE:
                controller.setDance(parameter);
                break;
        }
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, Response response) {

            sendRobot(webSocket);
            prepareRobot();
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
