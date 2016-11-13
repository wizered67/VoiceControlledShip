package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;

/**
 * Created by Adam on 11/12/2016.
 */
public class LargeEnemy extends EnemyShip {
    Game game;
    public LargeEnemy(Animation a, Game g) {
        scale = 3;
        animation = a;
        game = g;
        texture = a.getKeyFrame(0);
        setWidth(texture.getRegionWidth() * scale);
        setHeight(texture.getRegionHeight() * scale);
    }
}
