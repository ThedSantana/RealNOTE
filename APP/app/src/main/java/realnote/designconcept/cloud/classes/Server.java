package realnote.designconcept.cloud.classes;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import static com.androidnetworking.AndroidNetworking.post;

/**
 * Created by ToniApps Studios on 20/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class Server {

    public static void getAllNotes(JSONArrayRequestListener jsonArrayRequestListener, Context context){
        post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.GET_ALL_ACTION)
                .addBodyParameter(Consts.UID_VAR, Settings.getUId(context))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(jsonArrayRequestListener);
    }

    public static void newNote(Note note, StringRequestListener stringRequestListener, Context context){
        post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.CREATE_ACTION)
                .addBodyParameter(Consts.TITLE_VAR, note.getTitle())
                .addBodyParameter(Consts.NOTE_VAR, note.getNote())
                .addBodyParameter(Consts.UID_VAR, Settings.getUId(context))
                .addBodyParameter(Consts.DATE_VAR, note.getDate())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(stringRequestListener);
    }

    public static void updateNote(Note note, StringRequestListener stringRequestListener){
        post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.UPDATE_ACTION)
                .addBodyParameter(Consts.TITLE_VAR, note.getTitle())
                .addBodyParameter(Consts.NOTE_VAR, note.getNote())
                .addBodyParameter(Consts.DATE_VAR, note.getDate())
                .addBodyParameter(Consts.ID_VAR, String.valueOf(note.getIdOnline()))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(stringRequestListener);
    }

    public static void updateOrInsertNote(Note note, Context context, StringRequestListener stringRequestListener){
        ANRequest.PostRequestBuilder post = AndroidNetworking.post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.UPDATE_OR_INSERT_ACTION)
                .addBodyParameter(Consts.TITLE_VAR, note.getTitle())
                .addBodyParameter(Consts.NOTE_VAR, note.getNote())
                .addBodyParameter(Consts.DATE_VAR, note.getDate())
                .addBodyParameter(Consts.UID_VAR, Settings.getUId(context))
                .setPriority(Priority.MEDIUM);

        if (!note.notUploaded())
            post.addBodyParameter(Consts.ID_VAR, String.valueOf(note.getIdOnline()));

        post.build().getAsString(stringRequestListener);
    }

    public static void deleteNote(Note note, StringRequestListener stringRequestListener){
        post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.DELETE_ACTION)
                .addBodyParameter(Consts.ID_VAR, String.valueOf(note.getIdOnline()))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(stringRequestListener);
    }

    public static void getNote(Note note, StringRequestListener stringRequestListener){
        post(Consts.URL)
                .addBodyParameter(Consts.ACTION_VAR, Consts.GET_BY_ID_ACTION)
                .addBodyParameter(Consts.ID_VAR, String.valueOf(note.getIdOnline()))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(stringRequestListener);
    }

}
