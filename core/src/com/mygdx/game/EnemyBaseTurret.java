package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/13/2016.
 */
public class EnemyBaseTurret extends Actor {
    TextureRegion textureRegion;
    Game game;
    public EnemyBaseTurret(TextureRegion tr, Game g) {
        textureRegion = tr;
        game = g;
        //game.addToStage(this);
        //setHeight(textureRegion.getRegionHeight() * 20);
        //setWidth(textureRegion.getRegionWidth() * 20);
        //toBack();
    }

    @Override
    public void act(float delta) {
        //setOrigin(getWidth() / 2, getHeight() / 2);
        for (Action a : getActions()) {
            a.act(delta);
            destroy();
        }
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public void destroy() {

    }
}
