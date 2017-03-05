package realnote.designconcept.cloud;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by ToniApps Studios on 15/01/2017.
 * Visit http://toniapps.es for more info ;)
 */

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_one_title), getString(R.string.intro_one_desc), R.drawable.note, Color.parseColor("#3498db"))); //Añadimos una diapositiva
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_two_title), getString(R.string.intro_two_desc), R.drawable.world, Color.parseColor("#2ecc71"))); //Añadimos una diapositiva
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro_three_title), getString(R.string.intro_three_desc), R.drawable.google, Color.parseColor("#1abc9c"))); //Añadimos una diapositiva

         setDepthAnimation(); // Establecemos la animación

        showSkipButton(false); //Ponemos que no se pueda saltar la introducción
        setProgressButtonEnabled(true); //Mostramos el botón de progreso


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
