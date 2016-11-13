package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Adam on 11/12/2016.
 */
public class SmallEnemy extends EnemyShip {
    float speed = 4.5f;
    float HIGH_SPEED = 4.5f;
    float MEDIUM_SPEED = 4f;
    float LOW_SPEED = 3f;
    float direction;
    Game game;
    int numWarningShots;
    boolean readyToAct;
    Timer.Task actTimer;
    boolean startHoming;


    public SmallEnemy(Animation a, Game g) {
        super();
        scale = 4;
        shield = false;
        animation = a;
        game = g;
        texture = a.getKeyFrame(0);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
        numWarningShots = 0;
        readyToAct = false;
        startHoming = false;
    }

    public void act(float delta) {
        super.act(delta);
        PlayerShip player = game.getPlayer();
        if (startHoming) {
            speed = MEDIUM_SPEED;
            direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());
        } else if (Vector2.dst(getX(), getY(), player.getX(), player.getY()) >= Gdx.graphics.getHeight()) {
            direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());
            speed = HIGH_SPEED;
            if (actTimer != null) {
                actTimer.cancel();
                actTimer = null;
            }
        } else {
            if (!startHoming) {
                direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());
                speed = LOW_SPEED;
            }
            if (readyToAct) {
                if (numWarningShots < 2) {
                    readyToAct = false;
                    numWarningShots += 1;
                    shootAtPlayer();
                } else {
                    startHoming = true;
                }
            } else if (actTimer == null) {
                actTimer = new Timer.Task() {
                    @Override
                    public void run() {
                        readyToAct = true;
                    }
                };
                Timer.schedule(actTimer, MathUtils.random(4, 8));
            }
        }
        float x = (float) Math.cos(direction);
        float y = (float) Math.sin(direction);
        moveBy(speed * x, speed * y);
        boundingRectangle.set(getX() + getWidth() / 4, getY() + getHeight() / 4, getWidth() / 2, getHeight() / 2);
    }

    public void shootAtPlayer() {
        Laser laser = new Laser(game.getLaserTexture(false), false, direction);
    }
}
