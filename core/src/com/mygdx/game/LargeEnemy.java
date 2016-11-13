package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Adam on 11/12/2016.
 */
public class LargeEnemy extends EnemyShip implements Collidable {
    Game game;
    public LargeEnemy(Animation a, Game g) {
        super();
        scale = 3;
        animation = a;
        game = g;
        texture = a.getKeyFrame(0);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }

    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        boundingRectangle.set(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return boundingRectangle;
    }
}
