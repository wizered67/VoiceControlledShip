package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/12/2016.
 */
public class EnemyShip extends Actor implements Ship {
    boolean shield;
    TextureRegion texture;
    float scale;
    protected Animation animation;
    private float timer = 0;
    public EnemyShip() {
        shield = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        timer += delta;
        texture = animation.getKeyFrame(timer, true);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }

    public boolean getShield() {
        return shield;
    }

    public void setShield(boolean status) {
        shield = status;
    }
}
