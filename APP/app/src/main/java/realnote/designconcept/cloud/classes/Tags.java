package realnote.designconcept.cloud.classes;

import android.util.Log;

import com.androidnetworking.error.ANError;

/**
 * Created by ToniApps Studios on 15/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class Tags { //Tags para la linea de comandos (Uso no obligatorio)

    public static final String TAG_ERROR_GET_NOTES = "ErrorGetNotes";

    public static void logError(String tag, String message){
        Log.e(tag, message);
    }

    public static void logError(ANError anError){
        Log.e(TAG_ERROR_GET_NOTES, anError.getErrorCode() + ": " + anError.getMessage());
    }

}
