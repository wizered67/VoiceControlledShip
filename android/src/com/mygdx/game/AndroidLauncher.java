package com.mygdx.game;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;


public class AndroidLauncher extends AndroidApplication {

    ActionResolverAndroid actionResolver;

    public static Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionResolver = new ActionResolverAndroid(this);

        game = new Game(actionResolver);
        initialize(game);
        actionResolver.setGdx(game);
        //TODO: Figure a way to stop clunkily passing this object into MyListener.
    }

}
