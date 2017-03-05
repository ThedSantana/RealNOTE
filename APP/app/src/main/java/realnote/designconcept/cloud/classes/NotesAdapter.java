package realnote.designconcept.cloud.classes;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import realnote.designconcept.cloud.MainActivity;
import realnote.designconcept.cloud.R;

/**
 * Created by ToniApps Studios on 11/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {

    List<Note> notes; //La lista con todas las notas

    public NotesAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //Asignamos el holder y la vista
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_recycler_view, parent, false);
        NotesViewHolder holder = new NotesViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NotesViewHolder holder, int position) {
        final Note note = notes.get(position); //Cogemos la nota en la posición

        holder.titleTextView.setText(note.getTitle()); //Establecemos el texto
        holder.noteTextView.setText(note.getNote());

        holder.noteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.edit(holder.context);
            }
        }); //Si se hace clic que se edite la nota

        holder.noteItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { //Si se hace un clic largo mensaje para borrar
                new AlertDialog.Builder(holder.context)
                        .setTitle(R.string.delete_note_title_alert)
                        .setMessage(R.string.delete_note_text_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ConnectivityManager connectivityManager //Comprobamos si hay internet
                                        = (ConnectivityManager) holder.context.getSystemService(Context.CONNECTIVITY_SERVICE);
                                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

                                boolean online = activeNetworkInfo != null && activeNetworkInfo.isConnected();

                                note.delete(holder.context, online); //Borramos la nota

                                notes.remove(note); //La quitamos de las listas y el RV
                                MainActivity.notes.remove(note);
                                MainActivity.adapter.notifyDataSetChanged();
                            }
                        })
                        .setCancelable(true)
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(R.drawable.ic_delete_forever_black_48dp)
                        .create().show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder {

        LinearLayout noteItem;
        TextView titleTextView, noteTextView;
        Context context;

        public NotesViewHolder(View itemView) { //Asignamos las variables
            super(itemView);
            noteItem = (LinearLayout) itemView.findViewById(R.id.note_item);
            titleTextView = (TextView) itemView.findViewById(R.id.note_item_title);
            noteTextView = (TextView) itemView.findViewById(R.id.note_item_note);

            context = itemView.getContext();
        }
    }
}
