package realnote.designconcept.cloud;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import realnote.designconcept.cloud.classes.SharedPrefs;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    GoogleApiClient mGoogleApiClient;
    SharedPrefs prefs;

    public final int RC_SIGN_IN = 1375; //Código de identificación de la petición de Google

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        prefs = new SharedPrefs(SignInActivity.this); //Cogemos las preferencias guardadas
        if (!prefs.userID().equals("-1")){ //Comprobamos si hay usuario guardado
            launchActivity(); //Si lo hay hacemos mención al metodo de lanzar la actividad
            return;
        }else{
            if (prefs.showIntro()) //Y si no no hacemos nada a no ser que en las preferencias ponga que se muestre la intro
                showIntro(); //Mostramos la intro
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build(); //Preferencias de google+ para el inicio de sesión

        mGoogleApiClient = new GoogleApiClient.Builder(this) //Construimos el cliente de Google
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(this); //Configuramos el botón de inicio
    }

    private void signIn() { //Método para iniciar sesión
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) { //Manejamos el inicio de sesión
        Log.d("SignIn", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) { //Si se ha iniciado correctamente

            GoogleSignInAccount acct = result.getSignInAccount(); //Obtenemos la cuenta
            prefs.setUserID(acct.getId()); //Guardamos el user ID
            Log.i("prefsIDU", "" + prefs.userID()+ "acct id: "+ acct.getId());
            launchActivity(); //lanzamos el activity principal

        } else {

            prefs.setUserID("-1"); //si se cierra sesión quitamos el UID de las prefs
        }
    }

    private void launchActivity() { //Lanzamos el activity principal de las notas.
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void showIntro() {
        startActivity(new Intent(this, IntroActivity.class));
    } //Mostramos la activity de la intro

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { //Si coincide el código de resultado
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data); //Cogemos el resultado de google
            handleSignInResult(result); //Lo mandamos a manejar
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn(); //iciniamos sesión si se hace clic
                break;
        }
    }
}
