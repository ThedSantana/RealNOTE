package realnote.designconcept.cloud;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import realnote.designconcept.cloud.classes.Consts;
import realnote.designconcept.cloud.classes.Note;
import realnote.designconcept.cloud.classes.SQLHelper;

public class EditorActivity extends AppCompatActivity {

    TextInputEditText inputTextTitle, inputTextNote;
    Note note;
    SQLHelper db;

    boolean save = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true); //Mostramos la flecha para atrás

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(deleteNote);

        defineVariables(); //Definimos las variables
        getExtras(); //Obtenemos los extras

    }

    private void defineVariables(){ //Asignamos las variables

        inputTextTitle = (TextInputEditText) findViewById(R.id.edit_text_note_title);
        inputTextNote = (TextInputEditText) findViewById(R.id.edit_text_note_text);

        db = new SQLHelper(this);

    }

    private View.OnClickListener deleteNote = new View.OnClickListener() {
        @Override
        public void onClick(View view) { //Botón para borrar la nota

            Snackbar.make(view, getString(R.string.delete_note), Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.yes, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            save = false;
                            note.delete(getApplicationContext(), isNetworkAvailable());
                            EditorActivity.this.finish();
                        }
                    }).show();

        }
    };

    private void getExtras() { //Obtenemos el extra del UID

        Intent launchIntent = getIntent();

        int noteId = launchIntent.getIntExtra(Consts.NOTE_EXTRA_NAME, -1);

        note = db.getNoteByIdOnline(noteId);

        if (note == null){
            Toast.makeText(this, getString(R.string.error_not_existing_note), Toast.LENGTH_SHORT).show();
            this.finish();
        }

        inputTextTitle.setText(note.getTitle()); //Ponemos los textos
        inputTextNote.setText(note.getNote());

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (!save) //Comprobamos si no hay que guardar y ya cerramos el proceso
            return;

        String title = inputTextTitle.getText().toString(); //Obtenemos los textos de la nota
        String text = inputTextNote.getText().toString();

        if ((title.equals("") && note.getTitle().equals("")) && (text.equals("") && note.getNote().equals(""))){

            note.delete(getApplicationContext(), isNetworkAvailable()); //Borramos la nota si está to.do vacío

            return;
        }

        if (!title.equals(note.getTitle()) || !text.equals(note.getNote())) {

            note.setTitle(title); //Si se ha cambiado algo guardamos la nota
            note.setNote(text);
            note.setDate(SQLHelper.getDate());

            note.save(getApplicationContext());

        }

    }

    public boolean isNetworkAvailable() { //Para comprobar si hay internet
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_share:

                String textToShare = String.format(
                        getString(R.string.share_text),
                        inputTextTitle.getText().toString(),
                        inputTextNote.getText().toString()
                ); //Para compartir el texto



                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.action_share))); //Compartimos la nota

                break;

            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
