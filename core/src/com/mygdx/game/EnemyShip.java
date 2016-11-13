package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;

/**
 * Created by Adam on 11/12/2016.
 */
public class EnemyShip extends Actor implements Ship, Collidable {
    boolean shield;
    int shieldPower = 2;
    TextureRegion texture;
    float scale;
    protected Animation animation;
    private float timer = 0;
    Rectangle boundingRectangle;
    public int health;
    public int collideDamage;
    public EnemyShip() {
        collideDamage = 0;
        health = 3;
        shield = false;
        boundingRectangle = new Rectangle();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        for (Action a : getActions()) {
            a.act(delta);
            destroy();
        }
        timer += delta;
        texture = animation.getKeyFrame(timer, true);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }

    public void destroy() {

    }

    public boolean getShield() {
        return shield;
    }

    @Override
    public void getHit(int damage) {
        if (!shield || damage >= 8) {
            health -= damage;
            if (health <= 0) {
                addAction(Actions.removeActor());
            }
        } else {
            shieldPower -= damage;
            if (shieldPower <= 0) {
                shield = false;
            }
        }
    }

    public void setShield(boolean status) {
        shield = status;
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }
}
