package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Adam on 11/12/2016.
 */
public class Laser extends Actor {
    boolean playerOwned = false;
    int direction;
    int speed = 15;
    float scale = 4f;
    TextureRegion texture;
    public Laser(TextureRegion tex, boolean player, int dir) {
        playerOwned = player;
        direction = dir;
        texture = tex;//new TextureRegion(new Texture(Gdx.files.internal("laser.png")));
        setRotation(360 - direction);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
        setOrigin(getWidth() / 2, getHeight() / 2);
    }
    @Override
    public void act(float delta) {
        float x = (float)Math.cos(Math.toRadians(360 - direction + 90));
        float y = (float)Math.sin(Math.toRadians(360 - direction + 90));
        //System.out.println("direction " + direction + ", x " + x + ", y " + y);
        moveBy(speed * x, speed * y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
