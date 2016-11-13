package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/12/2016.
 */
public class Compass extends Actor {
    TextureRegion texture;

    public Compass() {
        texture = new TextureRegion(new Texture(Gdx.files.internal("compass.png")));
        setWidth(texture.getRegionWidth() * 4);
        setHeight(texture.getRegionHeight() * 4);
        setPosition(Gdx.graphics.getWidth() - getWidth(), Gdx.graphics.getHeight() - getHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
