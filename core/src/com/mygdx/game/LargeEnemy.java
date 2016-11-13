package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Adam on 11/12/2016.
 */
public class LargeEnemy extends EnemyShip implements Collidable {
    Game game;
    float speed = 2.5f;
    final float MAX_SPEED = 3.5f;
    final float REGULAR_SPEED = 2.5f;
    float direction;
    boolean stunned;
    Timer.Task actTimer;
    public LargeEnemy(Animation a, Game g) {
        super();
        g.addEnemy(this);
        health = 1;
        collideDamage = 40;
        scale = 3;
        animation = a;
        game = g;
        texture = a.getKeyFrame(0);
        stunned = false;
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        if (!stunned) {
            PlayerShip player = game.getPlayer();
            if (Vector2.dst(getX(), getY(), player.getX(), player.getY()) >= Gdx.graphics.getHeight()) {
                direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX()) + MathUtils.random(0, 0.54f);
                speed = MAX_SPEED;
                if (actTimer != null) {
                    actTimer.cancel();
                    actTimer = null;
                }
            } else if (actTimer == null) {
                actTimer = new Timer.Task() {
                    @Override
                    public void run() {
                        fireAtPlayer();
                        actTimer = null;
                    }
                };
                Timer.schedule(actTimer, MathUtils.random(3, 6));
            } else {
                direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX())
                        + MathUtils.random(0, 0.54f) - (float) Math.PI / 2;
            }
            float x = (float) Math.cos(direction);
            float y = (float) Math.sin(direction);
            moveBy(speed * x, speed * y);
            setRotation(direction);
        }
        boundingRectangle.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }

    public void destroy() {
        game.removeEnemy(this);
    }

    public void fireAtPlayer() {
        PlayerShip player = game.getPlayer();
        direction = (float) Math.atan2(player.getY() - getY(), player.getX() - getX());

        float degDir = (float) Math.toDegrees(direction) + MathUtils.random(-6, 6);
        if (degDir < 0) {
            degDir += 360;
        }
        Laser laser = new Laser(game.biggerLaserTexture, false, degDir);
        laser.setPosition(getX(), getY());
        laser.setDamage(20);

        Laser laser2 = new Laser(game.biggerLaserTexture, false, degDir + 10);
        laser2.setPosition(getX(), getY());
        laser2.setDamage(20);

        Laser laser3 = new Laser(game.biggerLaserTexture, false, degDir - 10);
        laser3.setPosition(getX(), getY());
        laser3.setDamage(20);

        game.shootSfx.play();
        game.shootSfx.play();
        game.shootSfx.play();

        game.addToStage(laser);
        game.addToStage(laser2);
        game.addToStage(laser3);

        stunned = true;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                stunned = false;
            }
        }, MathUtils.random(3, 6));
    }
}
