package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;

/**
 * Created by Adam on 11/13/2016.
 */
public class EnemyBase extends EnemyShip {
    Game game;
    TextureRegion textureRegion;
    float turretDirection;
    EnemyBaseTurret baseTurret;
    public EnemyBase(TextureRegion tr, TextureRegion turret, Game g) {
        game = g;
        g.addToStage(this);
        textureRegion = tr;
        baseTurret = new EnemyBaseTurret(turret, g);
        turretDirection = 0;
        setWidth(tr.getRegionWidth() * 10);
        setHeight(tr.getRegionHeight() * 10);
        baseTurret.setHeight(getHeight());
        baseTurret.setWidth(getWidth());
        baseTurret.setX(getX());
        baseTurret.setY(getY());
        //baseTurret.setX(getX() + getWidth() / 3 + 64);
        //baseTurret.setY(getY() + getHeight());
        g.addEnemy(this);
        //toBack();
        Timer.Task spawn = new Timer.Task() {
            @Override
            public void run() {
                spawnEnemy();
                Timer.schedule(this, 20);
            }
        };
        Timer.schedule(spawn, 20);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(textureRegion, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                getHeight(), getScaleX(), getScaleY(), getRotation());
    }

    @Override
    public void act(float delta) {
        for (Action a : getActions()) {
            a.act(delta);
            destroy();
        }
        spawnEnemy();
        turretDirection += 1;
        baseTurret.setRotation(turretDirection);
        boundingRectangle.set(getX(), getY(), getWidth(), getHeight());
    }

    public void spawnEnemy() {
        if (game.enemies.size() < game.MAX_ENEMIES) {
            boolean small = MathUtils.randomBoolean(0.7f);
            if (small) {
                SmallEnemy enemy = new SmallEnemy(game.smallEnemyAnimation, game);
                enemy.setPosition(getX() + MathUtils.random(-200, 200), getY() + MathUtils.random(-200, 200));
                game.addToStage(enemy);
                game.addEnemy(enemy);
            } else {
                LargeEnemy enemy = new LargeEnemy(game.largeEnemyAnimation, game);
                enemy.setPosition(getX() + MathUtils.random(-200, 200), getY() + MathUtils.random(-200, 200));
                game.addToStage(enemy);
                game.addEnemy(enemy);
            }
        }
    }

    public void destroy() {
        game.removeEnemy(this);
        baseTurret.addAction(Actions.removeActor());
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        baseTurret.setX(x); //+ getWidth() / 3 + 64);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        baseTurret.setY(y);// + getHeight());
    }
}
