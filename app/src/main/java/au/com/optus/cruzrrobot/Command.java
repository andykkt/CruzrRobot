package au.com.optus.cruzrrobot;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

public class Command {
    public static final int ADDUSER = 1;
    public static final int ADDROBOT = 2;
    public static final int ROBOTREADU = 3;
    public static final int GETROBOT = 10;
    public static final int ROBOTSTATUS = 11;
    public static final int EXECUTESIMPLE = 100;
    public static final int EXECUTEMACRO = 101;

    int commandId;
    List<Object> parameters = new ArrayList<Object>();
}
