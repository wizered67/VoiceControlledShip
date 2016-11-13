package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Adam on 11/12/2016.
 */
public class SmallEnemy extends EnemyShip {
    float speed = 4.5f;
    float direction;
    Game game;

    public SmallEnemy(Animation a, Game g) {
        scale = 4;
        shield = false;
        animation = a;
        game = g;
        texture = a.getKeyFrame(0);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }

    public void act(float delta) {
        super.act(delta);
        PlayerShip player = game.getPlayer();
        if (Vector2.dst(getX(), getY(), player.getX(), player.getY()) >= Gdx.graphics.getHeight()) {
            direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());
        }
        float x = (float) Math.cos(direction);
        float y = (float) Math.sin(direction);
        moveBy(speed * x, speed * y);
    }
}
