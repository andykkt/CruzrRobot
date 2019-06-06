package au.com.optus.cruzrrobot;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.ubtechinc.cruzr.sdk.dance.DanceConnectionListener;
import com.ubtechinc.cruzr.sdk.dance.DanceControlApi;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceCallBackImpl;
import com.ubtechinc.cruzr.sdk.face.FaceInfo;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.ISpeechContext;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.sdk.status.SystemStatusApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteStatusListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/4/26.
 */

public class App extends Application {
    public static App app;

    private List<FaceInfo> faces;
    //RefWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();
        //watcher = LeakCanary.install(this);
        app = this;
        RosRobotApi.get().initializ(this, new InitListener() {
            @Override
            public void onInit() {
                super.onInit();
            }
        });
        SpeechRobotApi.get().initializ(getApplicationContext(), 21, new InitListener() {
            @Override
            public void onInit() {
                SpeechRobotApi.get().registerSpeech(new ISpeechContext() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onStop() {

                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onResume() {

                    }

                    @Override
                    public void onResult(String speechTxt) {
                        //Log.i("dan", "text" + speechTxt);
                        //ResultEvent event = new ResultEvent();
                        //event.message = "本地的指令(" + speechTxt + ")已经被主服务识别返回";
                        //RxBus.getDefault().post(event);
                    }
                });
            }
        });
/*        SystemStatusApi.get().registerStatusCallback(new RemoteStatusListener() {
            @Override
            public void onResult(List<Integer> list, boolean b, int i) {

            }

            @Override
            public void onStatusPause(int j, int i) {

            }

            @Override
            public void onStatusFree(int i) {

            }
        });*/

        CruzrFaceApi.initCruzrFace(this);
        CruzrFaceApi.getCruzrFacesList(new CruzrFaceCallBackImpl() {
            @Override
            public void onCruzrFaceListCallBack(ArrayList<FaceInfo> faceList) {
                Log.i("dan", "onCruzrFaceListCallBack: " + faceList);
                faces = faceList;
                deletePowerOffFace(faces);
            }

            @Override
            public void onCruzrFaceSetCallBack(int resultCode) {
                Log.i("dan", "onCruzrFaceSetCallBack: " + resultCode);
            }

            @Override
            public void onCurrentFaceIdCallBack(String currentFaceId) {
                Log.i("dan", "onCurrentFaceIdCallBack: " + currentFaceId);
            }

            @Override
            public void onCruzrFaceNameCallBack(String s) {
                Log.i("dan", "onCruzrFaceNameCallBack: " + s);
            }

            @Override
            public void onCruzrFaceUriCallBack(String s) {
                Log.i("dan", "onCruzrFaceUriCallBack: " + s);
            }
        });

        NavigationApi.get().initializ(this);

        DanceControlApi.getInstance().initialize(this, new DanceConnectionListener() {
            @Override
            public void onConnected() {
                Log.i("dan", "DanceControlApi.onConnected");
            }

            @Override
            public void onDisconnected() {
                Log.i("dan", "DanceControlApi.onDisconnected");
            }

            @Override
            public void onReconnected() {
                Log.i("dan", "DanceControlApi.onReconnected");
            }
        });
    }

    public List<FaceInfo> getFaces() {
        return faces;
    }

    /**
     * 由于关机的表情会无限执行，所以在演示的时候把关机表情去掉
     *
     * @param list
     */
    private void deletePowerOffFace(List<FaceInfo> list) {
        Iterator<FaceInfo> it = list.iterator();
        FaceInfo info = null;
        while (it.hasNext()) {
            info = it.next();
            if ("face_power_off".equals(info.faceId)) {
                list.remove(info);
                break;
            }
        }
    }

/*	public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context
				.getApplicationContext();
		return application.watcher;
	}*/
}

