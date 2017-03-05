package realnote.designconcept.cloud.classes;

import android.content.Context;

/**
 * Created by ToniApps Studios on 20/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class Settings {

    //User ID
    public static String getUId(Context context){ //Coge el UID de las PREFS
        SharedPrefs prefs = new SharedPrefs(context);
        return prefs.userID();
    }

}
