package realnote.designconcept.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import realnote.designconcept.cloud.classes.SQLHelper;
import realnote.designconcept.cloud.classes.SharedPrefs;

public class SettingsActivity extends AppCompatActivity {

    SharedPrefs sharedPrefs;

    RadioButton linearLayoutRadioButton, gridLayoutRadioButton;

    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPrefs = new SharedPrefs(this);

        declareVariables();

        loadSettings();

    }

    private void declareVariables() {
        linearLayoutRadioButton = (RadioButton) findViewById(R.id.settings_list_type_list);
        gridLayoutRadioButton = (RadioButton) findViewById(R.id.settings_list_type_grid);
        signOutButton = (Button) findViewById(R.id.button_sign_out);

        linearLayoutRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    sharedPrefs.setRvLayout(SharedPrefs.RV_LAYOUT_LINEAR);

            }
        });

        gridLayoutRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    sharedPrefs.setRvLayout(SharedPrefs.RV_LAYOUT_GRID);
            }
        });

        signOutButton.setOnClickListener(buttonSignOut);
    }

    private void loadSettings(){
        if (sharedPrefs.rvLayout() == SharedPrefs.RV_LAYOUT_LINEAR) {
            linearLayoutRadioButton.setChecked(true);
            gridLayoutRadioButton.setChecked(false);
        } else{
            linearLayoutRadioButton.setChecked(false);
            gridLayoutRadioButton.setChecked(true);
        }
    }

    View.OnClickListener buttonSignOut = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            sharedPrefs.clear();
            sharedPrefs.setIntro(false);
            SQLHelper db = new SQLHelper(SettingsActivity.this);
            db.wipeNotes();
            startActivity(new Intent(SettingsActivity.this, SignInActivity.class));
            SettingsActivity.this.finish();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            //startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
