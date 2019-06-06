package au.com.optus.cruzrrobot;

import android.content.Context;
import android.util.Log;

import com.ubtechinc.cruzr.sdk.dance.DanceConnectionListener;
import com.ubtechinc.cruzr.sdk.dance.DanceConstant;
import com.ubtechinc.cruzr.sdk.dance.DanceControlApi;
import com.ubtechinc.cruzr.sdk.dance.RemoteDanceListener;
import com.ubtechinc.cruzr.sdk.face.CruzrFaceApi;
import com.ubtechinc.cruzr.sdk.face.FaceInfo;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.sdk.speech.SpeechConstant;
import com.ubtechinc.cruzr.sdk.speech.SpeechRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.RemoteCommonListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechASRListener;
import com.ubtechinc.cruzr.serverlibutil.interfaces.SpeechTtsListener;

import java.util.List;
import java.util.Random;

public class ApiControl {

    private Context context;

    public ApiControl(Context context) {
        this.context = context;
    }

    public void TtsPlay(String text) {
        SpeechRobotApi.get().speechStartTTS(text, new SpeechTtsListener() {
            @Override
            public void onAbort() {

            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void setVolume(int volume) {
        SpeechRobotApi.get().speechSetTtsVolume(volume);
    }

    public void moveForward(float amount) {
        int code = RosRobotApi.get().moveToward(amount, 0, 0, new RemoteCommonListener() {
                    @Override
                    public void onResult(int i, int i1, String s) {
                        Log.i("paul", "moveForward:" + s);
                    }
                });
//        ResultEvent event = new ResultEvent();
//        event.retcode = code;
//        event.message = "前进返回 session_id =";
//        RxBus.getDefault().post(event);
    }

    public void moveBackward(float amount) {
        int code = RosRobotApi.get().moveToward(-amount, 0, 0, new RemoteCommonListener() {

            @Override
            public void onResult(int i, int status, String s) {
                Log.i("paul", "后退状态:" + status);
//                ResultEvent event = new ResultEvent();
//                event.retcode = status;
//                event.message = "后退状态=";
//                RxBus.getDefault().post(event);
            }
        });
//        ResultEvent event = new ResultEvent();
//        event.retcode = code;
//        event.message = "后退返回 session_id =";
//        RxBus.getDefault().post(event);
    }

    public void turnLeft(float amount) {
        int code = RosRobotApi.get().moveToward(0, 0, amount, new RemoteCommonListener() {
            @Override
            public void onResult(int i, int status, String s) {
                Log.i("paul", "leftAround:" + status);
//                ResultEvent event = new ResultEvent();
//                event.retcode = status;
//                event.message = "左转状态=";
//                RxBus.getDefault().post(event);
            }
        });
//        ResultEvent event = new ResultEvent();
//        event.retcode = code;
//        event.message = "左转返回 session_id =";
//        RxBus.getDefault().post(event);
    }

    public void turnRight(float amount) {
        int code = RosRobotApi.get().moveToward(0, 0, -amount, new RemoteCommonListener() {
            @Override
            public void onResult(int i, int status, String s) {
                Log.i("paul", "右转状态:" + status);
//                ResultEvent event = new ResultEvent();
//                event.retcode = status;
//                event.message = "右转状态=";
//                RxBus.getDefault().post(event);
            }
        });
//        ResultEvent event = new ResultEvent();
//        event.retcode = code;
//        event.message = "右转状态=";
//        RxBus.getDefault().post(event);
    }

    public void stopMove() {
        int code = RosRobotApi.get().stopMove();
//        ResultEvent event = new ResultEvent();
//        event.retcode = code;
//        event.message = "停止返回 session_id =";
//        RxBus.getDefault().post(event);

    }

    public void setRandomFace() {
        List<FaceInfo> faces = App.app.getFaces();
        //ResultEvent event = new ResultEvent();
        if (faces != null && faces.size() > 0) {
            int size = faces.size();
            Random ran = new Random();
            int index = ran.nextInt(size);

            CruzrFaceApi.setCruzrFace(null, faces.get(index).faceId, true, true);
            //event.message = faces.get(index).faceId + "设置表情:" + faces.get(index).faceDesc;
        } else {
            //event.message = "没有获取到表情数据";
        }
        //RxBus.getDefault().post(event);
    }

    public void setFace(String faceId) {
        CruzrFaceApi.setCruzrFace(null, faceId, true, true);
    }

    public void disableWakeup() {
        SpeechRobotApi.get().enableWakeup(SpeechConstant.PROHIBIT_WAKE_UP_TYPE_ALL, false);
    }

    public void setAction(String actionId) {
        RosRobotApi.get().run(actionId);
    }

    public void setDance(String danceId) {
        DanceControlApi.getInstance().dance(danceId, new RemoteDanceListener() {
                    @Override
                    public void onResult(int status) {
                        switch(status) {
                            case DanceConstant.STATE_DANCE_START:
                                Log.i("dan", "Dance.STATE_DANCE_START: " + status);
                                break;
                            case DanceConstant.STATE_DANCE_COMPLETE:
                                Log.i("dan", "Dance.STATE_DANCE_COMPLETE: " + status);
                                break;
                            case DanceConstant.STATE_DANCE_FAIL:
                                Log.i("dan", "Dance.STATE_DANCE_FAIL: " + status);
                                break;
                            case DanceConstant.STATE_DANCE_CANCEL:
                                Log.i("dan", "Dance.STATE_DANCE_CANCEL: " + status);
                                break;
                        }
                        Log.i("dan", "Dance.onResult: " + status);
                    }
                }
        );
    }

    public void setNavigateTo(String mapPoint) {
        NavigationApi.get().startNavigationService(mapPoint);
    }

    public void startAsr() {
        SpeechRobotApi.get().startSpeechASR(new SpeechASRListener() {

            @Override
            public void onVolumeChanged(int i) {

            }

            @Override
            public void onBegin() {
//                Log.i("dan", "apirunner开始说话");
//                ResultEvent event = new ResultEvent();
//                event.message = "开始说话";
//                RxBus.getDefault().post(event);
            }

            @Override
            public void onEnd() {
//                Log.i("dan", "apirunner结束说话");
//                ResultEvent event = new ResultEvent();
//                event.message = "说话完毕";
//                RxBus.getDefault().post(event);
            }

            @Override
            public void onResult(String text, boolean isLast) {
//                Log.i("dan", text);
//                ResultEvent event = new ResultEvent();
//                event.message = "识别结果:" + text + "(是否结束 " + isLast + ")";
//                RxBus.getDefault().post(event);
            }

            @Override
            public void onError(int code) {
//                ResultEvent event = new ResultEvent();
//                event.message = "识别异常,返回码:" + code;
//                RxBus.getDefault().post(event);
            }

            @Override
            public void onIllegal() {

            }
        });
    }
}
