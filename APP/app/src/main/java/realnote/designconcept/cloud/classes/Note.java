package realnote.designconcept.cloud.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

import realnote.designconcept.cloud.EditorActivity;

/**
 * Created by ToniApps Studios on 11/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class Note {

    //CAMPOS DE LAS NOTAS
    int id = -1;
    String title;
    String note;
    String date;
    int idOnline = -1;

    public Note() {
        this.date = SQLHelper.getDate();
        this.title = "";
        this.note = "";
    }

    public Note(int id, String title, String note, String date, int idOnline) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.date = date;
        this.idOnline = idOnline;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOnline() {
        return idOnline;
    }

    public void setIdOnline(int idOnline) {
        this.idOnline = idOnline;
    }

    public String trimNote() {
        return this.note.replace("\n", " ");
    }

    public void edit(Context context){

        Intent editorIntent = new Intent(context, EditorActivity.class); //Abre el editor
        editorIntent.putExtra(Consts.NOTE_EXTRA_NAME, this.getIdOnline()); //Pone el extra de las notas para el editor con el id online

        context.startActivity(editorIntent); //Comenzamos el activity del editor

    }

    public void delete(Context context, boolean online){

        SQLHelper db = new SQLHelper(context); //cogemos la base de datos

        if (online) { //Si hay conexión a internet

            db.deleteNote(this.id); //Borra la nota de la base de datos

            Log.i("NoteDeleted", "In online mode with ID: "+ getId());

            Server.deleteNote(this, new StringRequestListener() { //Borra la nota del server
                @Override
                public void onResponse(String response) {
                    if (response.equals(Consts.OK_RESPONSE))
                        Log.i("delete", "true");
                    else
                        Log.i("delete", "false");

                }

                @Override
                public void onError(ANError anError) {
                    Tags.logError(anError);
                }
            });

        }else {

            Log.i("NoteDeleted", "In offline mode with ID: "+ getId());

            db.deleteNoteOffline(this); //Borra la nota de la base de datos

        }

    }

    public void save(Context context){

        SQLHelper db = new SQLHelper(context); //Base de datos

        db.updateNote(this); //Actualizamos la nota offline

        Server.updateNote(this, new StringRequestListener() { //Actualizamos la nota Online
            @Override
            public void onResponse(String response) {
                if (response.equals(Consts.OK_RESPONSE))
                    Log.i("updated", "true");
                else
                    Log.i("updated", "false");

            }

            @Override
            public void onError(ANError anError) {
                Tags.logError(anError);
            }
        });

    }

    public boolean isEmpty(){ //Comprueba si la nota está vacia
        if (this.getId() == -1)
            return true;
        else
            return false;
    }

    public boolean notUploaded(){ //Comprueba si la nota no está subida
        if (this.getIdOnline() == -1)
            return true;
        else
            return false;
    }

}
