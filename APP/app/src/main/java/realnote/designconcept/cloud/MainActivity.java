package realnote.designconcept.cloud;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import realnote.designconcept.cloud.classes.Consts;
import realnote.designconcept.cloud.classes.Note;
import realnote.designconcept.cloud.classes.NotesAdapter;
import realnote.designconcept.cloud.classes.SQLHelper;
import realnote.designconcept.cloud.classes.Server;
import realnote.designconcept.cloud.classes.SharedPrefs;
import realnote.designconcept.cloud.classes.Tags;

public class MainActivity extends AppCompatActivity {

    RecyclerView rv;

    public static List<Note> notes;
    public static NotesAdapter adapter;
    SQLHelper db;

    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabClickListener);

        defineVariables(); //Definimos las variables

        //Iniciamos el proceso de internet
        AndroidNetworking.initialize(getApplicationContext());


        rv.setAdapter(adapter); //Establecemos el adaptador


    }

    private void defineVariables(){
        rv = (RecyclerView) findViewById(R.id.notes_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        db = new SQLHelper(this);
        notes = new ArrayList<>();
        adapter = new NotesAdapter(notes);
        sharedPrefs = new SharedPrefs(MainActivity.this);

    }

    private void loadSettings(){ //Leemos los ajustes del deseño
        if (sharedPrefs.rvLayout() == SharedPrefs.RV_LAYOUT_GRID)
            rv.setLayoutManager(new GridLayoutManager(this, 2));
        else
            rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) { //Se crea una nueva nota

            final Note newNote = new Note();
            Server.newNote(newNote, new StringRequestListener() {
                @Override
                public void onResponse(String response) {
                    newNote.setIdOnline(Integer.valueOf(response));
                    insertAndEditNote(newNote);
                }

                @Override
                public void onError(ANError error) {
                    Tags.logError(error);

                    insertAndEditNote(newNote);
                }
            }, MainActivity.this);

        }
    };

    private void insertAndEditNote(Note note){ //Insertar y editar la nota
        db.insertNote(note);
        note.edit(MainActivity.this);

        note.setTitle(getText(R.string.default_title_note).toString());
        note.setNote(getText(R.string.default_text_note).toString());

        notes.add(0, note);
        adapter.notifyDataSetChanged();
    }

    private void compareNotes(JSONArray response) { //Comparamos las notas
        try {


            for (int i = 0; i< response.length(); i++){

                JSONObject jsonNote = response.getJSONObject(i);


                /*
                Log.i("note",
                        "id: " + jsonNote.getInt("id") +
                        " | title: " + jsonNote.getString("title") +
                        " | note: " + jsonNote.getString("note") +
                        " | date: " + jsonNote.getString("timestamp"));
                */


                Note noteInDB = db.getNoteByIdOnline(jsonNote.getInt(Consts.ID_JSON_RESPONSE)); //Cogemos la nota de la base de datos


                Note noteOnline = new Note( //Cogemos la nota del JSON
                        jsonNote.getInt(Consts.ID_JSON_RESPONSE),
                        jsonNote.getString(Consts.TITLE_JSON_RESPONSE),
                        jsonNote.getString(Consts.NOTE_JSON_RESPONSE),
                        jsonNote.getString(Consts.TIMESTAMP_JSON_RESPONSE),
                        noteInDB.getId()
                );

                if  (noteInDB.isEmpty()){ //Si la nota en la base de datos está vacía
                    boolean noteInserted = db.insertNote(noteOnline);
                    Log.i("noteInserted", "note: "+ noteOnline.getIdOnline()+" | inserted: "+ noteInserted);
                    noteOnline.setNote(noteOnline.trimNote());
                    notes.add(noteOnline);
                    continue;
                }

                DateFormat format = new SimpleDateFormat(Consts.DATE_FORMAT, Locale.ENGLISH);

                Date dateOnline = format.parse(noteOnline.getDate());
                Date dateOffline = format.parse(noteInDB.getDate());

                if (dateOnline.equals(dateOffline)){ //Comparamos las fechas

                }else if (dateOnline.after(dateOffline)){
                    //Online date after offline

                    db.updateNote(noteOnline);
                    noteOnline.setNote(noteOnline.trimNote());
                    notes.add(noteOnline);

                    Log.i("noteBoA", "Online After: "+ noteOnline.getIdOnline() + " | OFF: " + noteInDB.getDate() + " ON: " + noteOnline.getDate());

                    continue;

                }else if (dateOffline.after(dateOnline)){
                    //Offline date after online

                    Server.updateNote(noteInDB, new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals(Consts.OK_RESPONSE))
                                Log.i("update", "true");
                            else
                                Log.e("update", "false");

                        }

                        @Override
                        public void onError(ANError anError) {
                            Tags.logError(anError);

                            setOfflineNotes();

                        }
                    });

                    Log.i("noteBoA", "Offline After: "+ noteInDB.getIdOnline() + " | OFF: " + noteInDB.getDate() + " ON: " + noteOnline.getDate());

                }

                noteInDB.setNote(noteInDB.trimNote());
                notes.add(noteInDB);

                adapter.notifyDataSetChanged(); //añadimos la nota y notificamos


            }
        } catch (JSONException e) {
            Tags.logError(Tags.TAG_ERROR_GET_NOTES, " " + e.getMessage());
        } catch (ParseException e) {
            Tags.logError(Tags.TAG_ERROR_GET_NOTES, " " + e.getMessage());
        }
    }

    private void resetOnlineNotes() { //Reseteamos las notas online

        List<Note> notesToDelete = db.getNotesToDelete();

        for (Note note :
                notesToDelete) {
            note.delete(this, isNetworkAvailable());
        } //Borramos las notas que nos sobren

        notes = db.cursorToNotes(db.getAllNotes()); //Cogemos las notas

        for (final Note note:
                notes) {
            Server.updateOrInsertNote(note, this, new StringRequestListener() { //Las actualizamos
                @Override
                public void onResponse(String response) {

                    if (note.notUploaded()) {
                        Log.i("reupload", "noteID: "+ response + " | uploaded");
                        int id = Integer.valueOf(response);
                        note.setIdOnline(id);
                        db.updateNote(note);
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Tags.logError(anError);
                }
            });

        }
        Server.getAllNotes(getOnlineNotes, MainActivity.this); //Obtenemos todas las notas de internet y las ponemos en el RV
    }

    private JSONArrayRequestListener getOnlineNotes = new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
            if (response.length()<1){
                return;
            }

            notes.clear();

            Log.i("OnlineNotes", "loaded");

            try {

                for (int i = 0; i < response.length(); i++) {

                    JSONObject jsonNote = response.getJSONObject(i);

                    Note note = new Note(
                            jsonNote.getInt(Consts.ID_JSON_RESPONSE),
                            jsonNote.getString(Consts.TITLE_JSON_RESPONSE),
                            jsonNote.getString(Consts.NOTE_JSON_RESPONSE),
                            jsonNote.getString(Consts.TIMESTAMP_JSON_RESPONSE),
                            jsonNote.getInt(Consts.ID_JSON_RESPONSE)
                    );

                    if (!db.shouldBeDeleted(note.getId())){
                        notes.add(note); //Si no debería borrarse la nota la añadimos
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            checkIfEmptyNotes();

            rv.removeAllViews();

            adapter = new NotesAdapter(notes);

            rv.setAdapter(adapter);

            Log.i("OnlineNotes", "Total notes: "+ notes.size());

            adapter.notifyDataSetChanged(); //Notificamos el adaptador y ponemos las notas.

            Log.i("OnlineNotes", "Notes in adapter: "+ adapter.getItemCount());


        }

        @Override
        public void onError(ANError anError) {
            Tags.logError(anError);

            setOfflineNotes(); //Si hay algun error leemos las notas offline
        }
    };

    private JSONArrayRequestListener checkIfNotesOutdated = new JSONArrayRequestListener() { //Comprobramos si las notas están desactualizadas
        @Override
        public void onResponse(JSONArray response) {
            if (response.length()<1){
                return;
            }

            compareNotes(response);

            checkIfEmptyNotes();

            adapter.notifyDataSetChanged();


                /*Cursor cursor = db.getAllNotes();
                Log.i("note", "Offline notes: ");
                while (cursor.moveToNext()){
                    Log.i("note",
                            "id: " + cursor.getInt(SQLHelper.COLUMN_ID_NUMBER) +
                                    " | title: " + cursor.getString(SQLHelper.COLUMN_NOTES_TITLE_NUMBER) +
                                    " | note: " + cursor.getString(SQLHelper.COLUMN_NOTES_NOTE_NUMBER) +
                                    " | date: " + cursor.getString(SQLHelper.COLUMN_NOTES_DATE_NUMBER) +
                                    " | IdOnline: " + cursor.getString(SQLHelper.COLUMN_NOTES_ID_ONLINE_NUMBER));


                }*/

        }

        @Override
        public void onError(ANError anError) {
            Tags.logError(anError);

            setOfflineNotes();
        }
    };

    private void checkIfEmptyNotes(){ //Si falta el título o la nota pone un texto de relleno
        for (Note note : notes) {
            if (note.getTitle().equals(""))
                note.setTitle(getText(R.string.default_title_note).toString());
            if (note.getNote().equals(""))
                note.setNote(getText(R.string.default_text_note).toString());
        }
    }

    public void setOfflineNotes(){ //Leemos las notas sin conexión

        Cursor notesCursor = db.getAllNotes();

        notes.addAll(SQLHelper.cursorToNotes(notesCursor));

        checkIfEmptyNotes();

        adapter.notifyDataSetChanged();

    }

    public boolean isNetworkAvailable() { //Comprobamos si hay internet
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class)); //Abrimos los ajustes
            //this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        notes.clear(); //Limpiamos las notas
        loadSettings(); //Leemos los ajustes

        if (!isNetworkAvailable()){ //Comprobramos de que forma debemos obtener las notas
            Toast.makeText(this, R.string.offline, Toast.LENGTH_LONG).show();
            Log.i("status", "offline");
            setOfflineNotes();
            sharedPrefs.setLastOnline(false);
        }else{
            Log.i("status", "online");
            Toast.makeText(this, R.string.synchronising, Toast.LENGTH_SHORT).show();

            if (!sharedPrefs.lastOnline()) {
                Log.i("status", "last offline");
                resetOnlineNotes();
            }else {
                Log.i("status", "last online");
                Server.getAllNotes(checkIfNotesOutdated, MainActivity.this);
            }


            sharedPrefs.setLastOnline(true);
        }



    }

}
