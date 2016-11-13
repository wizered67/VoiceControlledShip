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
    float speed = 3f;
    float HIGH_SPEED = 3f;
    float MEDIUM_SPEED = 2.5f;
    float LOW_SPEED = 2f;
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
        health = 2;
        collideDamage = 20;
        animation = a;
        game = g;
        game.addEnemy(this);
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
            direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX()) + MathUtils.random(0, 0.13f);
        } else if (Vector2.dst(getX(), getY(), player.getX(), player.getY()) >= Gdx.graphics.getHeight()) {
            direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX()) + MathUtils.random(0, 0.54f);
            speed = HIGH_SPEED;
            numWarningShots = 0;
            if (actTimer != null) {
                actTimer.cancel();
                actTimer = null;
            }
        } else {
            if (!startHoming) {
                direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX()) + MathUtils.random(0, 0.27f);
                speed = LOW_SPEED;
            }
            if (readyToAct) {
                System.out.println("READY TO ACT.");
                if (numWarningShots < 4) {
                    readyToAct = false;
                    actTimer = null;
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
                Timer.schedule(actTimer, MathUtils.random(2, 5));
            }
        }
        float x = (float) Math.cos(direction);
        float y = (float) Math.sin(direction);
        moveBy(speed * x, speed * y);
        boundingRectangle.set(getX() + getWidth() / 4, getY() + getHeight() / 4, getWidth() / 2, getHeight() / 2);
        setRotation(direction);
    }

    public void shootAtPlayer() {
        PlayerShip p = game.getPlayer();
        float degrees = (float)Math.toDegrees(Math.atan2(p.getY() - getY(), p.getX() - getX())) + MathUtils.random(0, 6);
        if (degrees < 0) {
            degrees += 360;
        }
        Laser laser = new Laser(game.getLaserTexture(false), false,
                degrees);
        laser.setPosition(getX(), getY());
        laser.setDamage(20);
        game.addToStage(laser);
        game.shootSfx.play();
    }

    public void destroy() {
        game.removeEnemy(this);
    }
}
