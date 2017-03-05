package realnote.designconcept.cloud.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ToniApps Studios on 15/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class SQLHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    public static final String DATABASE_NAME = "RealNote.sql";
    public static final int DATABASE_VERSION = 1; //TODO VERSION DE LA BASE DE DATOS (AUMENTAR SI LA CAMBIAS)

    public static final String ID = "id";
    public static final int COLUMN_ID_NUMBER = 0;

    //NOTES Table
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_NOTES_TITLE = "title";
    public static final String COLUMN_NOTES_NOTE = "note";
    public static final String COLUMN_NOTES_DATE = "date";
    public static final String COLUMN_NOTES_ID_ONLINE = "id_online";
    public static final String COLUMN_NOTES_DELETE_NOTE = "delete_online";

    //NOTES Column Number
    public static final int COLUMN_NOTES_TITLE_NUMBER = 1;
    public static final int COLUMN_NOTES_NOTE_NUMBER = 2;
    public static final int COLUMN_NOTES_DATE_NUMBER = 3;
    public static final int COLUMN_NOTES_ID_ONLINE_NUMBER = 4;
    public static final int COLUMN_NOTES_DELETE_NOTE_NUMBER = 5;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) { //Se crea la tabla
        db.execSQL("create table "+
                TABLE_NOTES +
                "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                COLUMN_NOTES_TITLE + " TEXT,"+
                COLUMN_NOTES_NOTE + " TEXT,"+
                COLUMN_NOTES_DATE + " TEXT,"+
                COLUMN_NOTES_ID_ONLINE + " INTEGER,"+
                COLUMN_NOTES_DELETE_NOTE + " INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { //Se borra la tabla y se crea de nuevo
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NOTES);
        onCreate(db);
    }

    public static String getDate(){ //Obtenemos la fecha
        SimpleDateFormat sdf = new SimpleDateFormat(Consts.DATE_FORMAT, Locale.ENGLISH);
        return sdf.format(new Date());
    }

    public static int[] splitDate(String date){ //Separamos la fecha
        String[] dateSplittedEspace = date.split(" ");
        String[] dateSplittedNumbersStringOne =  dateSplittedEspace[0].split("-");
        String[] dateSplittedNumbersStringTwo =  dateSplittedEspace[1].split(":");
        return new int[]{
                Integer.valueOf(dateSplittedNumbersStringOne[0]),
                Integer.valueOf(dateSplittedNumbersStringOne[1]),
                Integer.valueOf(dateSplittedNumbersStringOne[2]),
                Integer.valueOf(dateSplittedNumbersStringTwo[0]),
                Integer.valueOf(dateSplittedNumbersStringTwo[1]),
                Integer.valueOf(dateSplittedNumbersStringTwo[2])};

    }

    public static Boolean dateOneAfter(String date1, String date2){ //Comparación de fechas (experimental)

        int[] dateOneSplitted = splitDate(date1);
        int[] dateTwoSplitted = splitDate(date2);

        for (int i = 0; i<dateOneSplitted.length; i++)
            if (dateOneSplitted[i]>dateTwoSplitted[i])
                return true;
        return false;
        //TODO fix that
    }

    public Cursor getAllNotes(){ //Obtener todas las notas
        Cursor notes = db.rawQuery("SELECT * from "+ TABLE_NOTES + " WHERE " + COLUMN_NOTES_DELETE_NOTE + " = 0 ORDER BY " + COLUMN_NOTES_DATE + " DESC", null);
        return notes;
    }

    public Note getNoteById(int id){ //obtener una nota por el ID
        Cursor notes = db.rawQuery("SELECT * from "+ TABLE_NOTES + " WHERE " + ID + " = " + id, null);
        notes.moveToFirst();
        Note note = new Note(
                notes.getInt(COLUMN_ID_NUMBER),
                notes.getString(COLUMN_NOTES_TITLE_NUMBER),
                notes.getString(COLUMN_NOTES_NOTE_NUMBER),
                notes.getString(COLUMN_NOTES_DATE_NUMBER),
                notes.getInt(COLUMN_NOTES_ID_ONLINE_NUMBER)
                );
        return note;
    }

    public Note getNoteByIdOnline(int idOnline){ //Obtener una nota por el ID Online
        Cursor notes = db.rawQuery("SELECT * from "+ TABLE_NOTES + " WHERE " + COLUMN_NOTES_ID_ONLINE + " = " + idOnline, null);
        if (notes == null || notes.getCount() <= 0 /* || notes.isNull(COLUMN_ID_NUMBER) */) {
            return new Note();
        }
        notes.moveToFirst();

        return new Note(
                notes.getInt(COLUMN_ID_NUMBER),
                notes.getString(COLUMN_NOTES_TITLE_NUMBER),
                notes.getString(COLUMN_NOTES_NOTE_NUMBER),
                notes.getString(COLUMN_NOTES_DATE_NUMBER),
                notes.getInt(COLUMN_NOTES_ID_ONLINE_NUMBER));
    }

    public boolean insertNote(Note note){ //Insertamos una nota
        ContentValues noteValues = new ContentValues();
        noteValues.put(COLUMN_NOTES_TITLE, note.getTitle());
        noteValues.put(COLUMN_NOTES_NOTE, note.getNote());
        noteValues.put(COLUMN_NOTES_DATE, getDate());
        noteValues.put(COLUMN_NOTES_ID_ONLINE, note.getIdOnline());
        long rows = db.insert(TABLE_NOTES, null, noteValues);
        return rows != -1;
    }

    public boolean updateNote(Note note){ //Actualizar la nota
        ContentValues noteValues = new ContentValues();
        noteValues.put(ID, note.getId());
        noteValues.put(COLUMN_NOTES_TITLE, note.getTitle());
        noteValues.put(COLUMN_NOTES_NOTE, note.getNote());
        noteValues.put(COLUMN_NOTES_DATE, note.getDate());
        noteValues.put(COLUMN_NOTES_ID_ONLINE, note.getIdOnline());
        int rows = db.update(TABLE_NOTES, noteValues, ID + " = ?", new String[] {String.valueOf(note.getId())});
        return rows != 0;
    }

    public boolean deleteNote(int id){ //Borrar la nota
        int rows = db.delete(TABLE_NOTES, ID + "= ?" ,new String[] {String.valueOf(id)});
        return rows != 0;
    }

    public  boolean wipeNotes(){ //Limpiar la tabla
        int rows = db.delete(TABLE_NOTES, "1" , null);
        return rows != 0;
    }

    public static List<Note> cursorToNotes(Cursor notes){ //Transformar de Cursor a Lista de Notas
        List<Note> notesList = new ArrayList<>();
        while (notes.moveToNext()){
            Note note = new Note(
                    notes.getInt(COLUMN_ID_NUMBER),
                    notes.getString(COLUMN_NOTES_TITLE_NUMBER),
                    notes.getString(COLUMN_NOTES_NOTE_NUMBER),
                    notes.getString(COLUMN_NOTES_DATE_NUMBER),
                    notes.getInt(COLUMN_NOTES_ID_ONLINE_NUMBER)
            );
            Log.i("noteOff", "id: "+ note.getId() + ", idOnline: "+ note.getIdOnline());
            note.setNote(note.trimNote());
            notesList.add(note);
        }
        return notesList;
    }

    public boolean deleteNoteOffline(Note note){ //Borrar una nota offline
        ContentValues noteValues = new ContentValues();
        noteValues.put(ID, note.getId());
        noteValues.put(COLUMN_NOTES_TITLE, note.getTitle());
        noteValues.put(COLUMN_NOTES_NOTE, note.getNote());
        noteValues.put(COLUMN_NOTES_DATE, note.getDate());
        noteValues.put(COLUMN_NOTES_ID_ONLINE, note.getIdOnline());
        noteValues.put(COLUMN_NOTES_DELETE_NOTE, 1);
        int rows = db.update(TABLE_NOTES, noteValues, ID + " = ?", new String[] {String.valueOf(note.getId())});
        return rows != 0;
    }

    public List<Note> getNotesToDelete() { //Obtener notas a borrar cuando haya internet
        Cursor notes = db.rawQuery("SELECT * from "+ TABLE_NOTES + " WHERE "+ COLUMN_NOTES_DELETE_NOTE +" = 1", null);
        return cursorToNotes(notes);
    }

    public boolean shouldBeDeleted(int id){ //Comprobamos si la nota debería ser borrada o no

        Cursor notes = db.rawQuery("SELECT * from "+ TABLE_NOTES + " WHERE " + ID + " = " + id, null);

        notes.moveToFirst();

        int size = notes.getCount();
        return size == 1;
    }

}
