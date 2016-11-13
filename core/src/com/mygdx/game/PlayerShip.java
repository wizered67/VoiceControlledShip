package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Adam on 11/12/2016.
 */
public class PlayerShip extends Actor implements Ship {
    private TextureRegion texture;
    private Animation animation;
    private boolean shield;
    private float timer = 0;
    public Rectangle boundingBox;

    public PlayerShip() {
        shield = false;
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("playerShip.pack"));
        Array<TextureAtlas.AtlasRegion> ship = atlas.findRegions("Frame");
        animation = new Animation(0.1f, ship);
        boundingBox = new Rectangle();
        //texture = new TextureRegion(new Texture("mic.jpg"));

        //setWidth(texture.getRegionWidth());
        //setHeight(texture.getRegionHeight());
    }

    @Override
    public void act(float delta) {
        timer += delta;
        texture = animation.getKeyFrame(timer, true);
        setWidth(texture.getRegionWidth() * 2);
        setHeight(texture.getRegionHeight() * 2);
        boundingBox.setPosition(getX() + getWidth() / 3, getY() + getHeight() / 3);
        boundingBox.setWidth(getWidth() / 3);
        boundingBox.setHeight(getHeight() / 3);
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
