package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/12/2016.
 */
public class PlayerShip extends Actor implements Ship {
    private TextureRegion texture;
    private Animation animation;
    private boolean shield;

    public PlayerShip() {
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

    public void setShield(boolean status) {
        shield = status;
    }

    public boolean getShield() {
        return shield;
    }

}
