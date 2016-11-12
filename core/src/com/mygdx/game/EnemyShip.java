package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/12/2016.
 */
public class EnemyShip extends Actor implements Ship {
    boolean shield;
    TextureRegion texture;
    public EnemyShip() {
        shield = false;
        texture = new TextureRegion(new Texture("mic.jpg"));
        setWidth(texture.getRegionWidth());
        setHeight(texture.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    public boolean getShield() {
        return shield;
    }

    public void setShield(boolean status) {
        shield = status;
    }
}
