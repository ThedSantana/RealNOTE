package realnote.designconcept.cloud.classes;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ToniApps Studios on 16/12/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class SharedPrefs {

    public static final String SHARED_PREFS = "settings";

    public static final String INTRO = "intro";
    public static final String LAST_ONLINE = "last_online";
    public static final String USER_ID = "user_id";

    public static final String RV_LAYOUT = "rv_layout";
    public static final int RV_LAYOUT_LINEAR = 1;
    public static final int RV_LAYOUT_GRID = 2;

    SharedPreferences sharedPrefs;

    public SharedPrefs(Context context){
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_APPEND);
    }

    public boolean showIntro() { return sharedPrefs.getBoolean(INTRO, true); }

    public void setIntro(boolean bool) { sharedPrefs.edit().putBoolean(INTRO, bool).apply(); }

    public boolean lastOnline(){
        return sharedPrefs.getBoolean(LAST_ONLINE , true);
    }

    public void setLastOnline(boolean bool){ sharedPrefs.edit().putBoolean(LAST_ONLINE, bool).apply(); }

    public String userID(){
        return sharedPrefs.getString(USER_ID , "-1");
    }

    public void setUserID(String id){
        sharedPrefs.edit().putString(USER_ID, id).apply();
    }

    public int rvLayout() { return sharedPrefs.getInt(RV_LAYOUT , RV_LAYOUT_LINEAR); }

    public void setRvLayout(int mode){
        sharedPrefs.edit().putInt(RV_LAYOUT, mode).apply();
    }

    public void clear(){
        sharedPrefs.edit().clear().apply();
    }
}
