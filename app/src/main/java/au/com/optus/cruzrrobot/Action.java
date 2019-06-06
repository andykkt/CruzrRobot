package au.com.optus.cruzrrobot;

public class Action {
    public static final int MOVEFORWARD = 1;
    public static final int MOVEBACKWARD = 2;
    public static final int TURNLEFT = 3;
    public static final int TURNRIGHT = 4;
    public static final int STOPMOVE = 5;
    public static final int SPEECH = 6;
    public static final int SPEECHVOLUME = 7;
    public static final int NAVIGATETO = 8;
    public static final int FACE = 9;
    public static final int ACTION = 10;
    public static final int DANCE = 11;

    int type;
    String parameter;
}
