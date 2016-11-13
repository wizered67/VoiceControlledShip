package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Main libGDX class.
 * Created by hubert on 20.10.14.
 */
public class Game implements ApplicationListener {
    GL20 gl;
    ActionResolver actionResolver; // this exists to be able to call native Android methods.

    private Stage stage;
    private Stage guiStage;
    private Skin skin;
    private ShapeRenderer shapes;

    int normalWidth;
    int normalHeight;

    TextField textField;
    MicButton micButton;
    PlayerShip player;
    Compass compass;
    float direction;
    float targetDirection;
    int health;
    int energy;
    boolean firing;
    final int MAX_HEALTH = 100;
    final int MAX_ENERGY = 100;
    final float LOW_SPEED = 3.5f;
    //final float MEDIUM_SPEED = 10;
    final float HIGH_SPEED = 9;
    float speed = LOW_SPEED;
    TextureRegion laserTexture;
    TextureRegion smallLaserTexture;
    TextureRegion biggerLaserTexture;
    Vector2 scannedLocation;
    Timer.Task removeLocation;
    Timer.Task stopBurst;

    ArrayList<EnemyShip> enemies;
    ArrayList<EnemyBase> enemyBases;
    EnemyBase finalBase;
    int MAX_ENEMIES = 10;

    ShaderProgram defaultShader;
    ShaderProgram backgroundShader;

    Texture background;
    Animation smallEnemyAnimation;
    Animation largeEnemyAnimation;

    TextureRegion energyTexture;
    TextureRegion healthTexture;
    TextureRegion enemyBase;
    TextureRegion enemyBaseTurret;
    TextureRegion missileTexture;

    Sound explosionSfx;
    Sound hitSfx;
    Sound shootSfx;


    public Game(ActionResolver actionResolver) { this.actionResolver = actionResolver; }

    // Create the button:
    public class MicButton extends Actor {
        Texture texture = new Texture(Gdx.files.internal("mic.jpg"));

        public MicButton() {
            setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
            getColor().a = 0.4f;
            addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    actionResolver.recognizeSpeech();
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    actionResolver.stopSpeech();
                }
            });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Color normalColor = batch.getColor();
            batch.setColor(getColor());
            batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                    getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0,
                    texture.getWidth(), texture.getHeight(), false, false);
            batch.setColor(normalColor);
        }
    }

    @Override
    public void create() {
        gl = Gdx.app.getGraphics().getGL20();
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false);
        stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2, camera));
        guiStage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin(Gdx.files.internal("Scene2D/uiskin.json"));
        shapes = new ShapeRenderer();
        Gdx.input.setInputProcessor(guiStage);

        enemies = new ArrayList<EnemyShip>();
        enemyBase = new TextureRegion(new Texture(Gdx.files.internal("EnemyBase.png")));
        enemyBaseTurret = new TextureRegion(new Texture(Gdx.files.internal("EnemyBaseTurret.png")));
        finalBase = new EnemyBase(enemyBase, enemyBaseTurret, this);
        finalBase.setZIndex(200);
        finalBase.baseTurret.setZIndex(400);
        //stage.addActor(baseTest);
        finalBase.setPosition(MathUtils.random(-2000 * 5, 2000*5), MathUtils.random(-2000 * 5, 2000 * 5));

        direction = 0;
        targetDirection = 0;
        laserTexture = new TextureRegion(new Texture(Gdx.files.internal("laser.png")));
        smallLaserTexture = new TextureRegion(new Texture(Gdx.files.internal("small laser.png")));
        biggerLaserTexture = new TextureRegion(new Texture(Gdx.files.internal("enemyLaser.png")));
        missileTexture = new TextureRegion(new Texture(Gdx.files.internal("missile.png")));
        background = new Texture(Gdx.files.internal("background.png"));
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);



        explosionSfx = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        hitSfx = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        shootSfx = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));

        energyTexture = new TextureRegion(new Texture(Gdx.files.internal("energybar.png")));
        healthTexture = new TextureRegion(new Texture(Gdx.files.internal("healthbar.png")));
        /*
        textField = new TextField("\t...", skin);
        textField.setWidth(Gdx.graphics.getWidth() - Gdx.graphics.getWidth() / 10.0f);
        textField.setHeight(80);
        float offset = Gdx.graphics.getWidth() - textField.getWidth();
        textField.setX(offset - offset / 2); // Centered
        textField.setY(Gdx.graphics.getHeight() - Gdx.graphics.getHeight() / 2.5f);

        textField.setDisabled(false); // Keyboard input enabled.
        */
        micButton = new MicButton();
        micButton.setTouchable(Touchable.enabled);
        micButton.setPosition(guiStage.getWidth() / 2 - micButton.getWidth() / 2, 0);

        //stage.addActor(textField);
        guiStage.addActor(micButton);

        player = new PlayerShip();
        stage.addActor(player);
        player.setPosition(stage.getWidth() / 2 - player.getWidth() / 2, player.getHeight() * 4);
        health = MAX_HEALTH;
        energy = MAX_ENERGY;
        firing = false;



        //stage.addActor(new Laser(laserTexture, true, 70));

        ShaderProgram.pedantic = false;
        defaultShader = SpriteBatch.createDefaultShader();
        final String vertexShader = Gdx.files.internal("vertexshader.glsl").readString();
        final String fragmentShader = Gdx.files.internal("fragmentshader.glsl").readString();// DefaultShader.getDefaultFragmentShader();
        backgroundShader = new ShaderProgram(vertexShader, fragmentShader);
        if (backgroundShader.isCompiled()){
            System.out.println("Background Shader compiled successfully.");
        }
        else {
            System.out.println(backgroundShader.getLog());
        }

        int delay = 5;
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                energy = Math.min(MAX_ENERGY, energy + 20);
                System.out.println("energy gained.");

            }
        }, delay, delay);

        compass = new Compass();
        guiStage.addActor(compass);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("SmallEnemy.pack"));
        Array<TextureAtlas.AtlasRegion> ship = atlas.findRegions("Frame");
        smallEnemyAnimation = new Animation(0.2f, ship);

        TextureAtlas largeAtlas = new TextureAtlas(Gdx.files.internal("LargeEnemy.pack"));
        Array<TextureAtlas.AtlasRegion> largeShip = largeAtlas.findRegions("Frame");
        largeEnemyAnimation = new Animation(0.2f, largeShip);


        /*
        final SmallEnemy enemy = new SmallEnemy(smallEnemyAnimation, this);
        stage.addActor(enemy);
        enemy.setPosition(3 * stage.getWidth() / 4, stage.getHeight() / 2);

        final LargeEnemy enemy2 = new LargeEnemy(largeEnemyAnimation, this);
        stage.addActor(enemy2);
        enemy2.setPosition(stage.getWidth() / 2, 3 * stage.getHeight() / 4);
        enemy2.setShield(true);
        */


        //actionResolver.showToast("Tap the mic icon to speak", 5000);
    }

    public void shakeMicButton() {
        Action shakeAction = Actions.repeat(2,
                (Actions.sequence(
                        Actions.moveBy(10, 0,0.05f),
                        Actions.moveBy(-10, 0,0.05f)
                )));
        micButton.addAction(Actions.sequence(shakeAction));
        micButton.act(Gdx.graphics.getDeltaTime());
    }

    public void setTextFieldText(String text) {
        //textField.setText(" " + text);
        actionResolver.showToast(text, 5000);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
        System.out.println(width + ", " + height);
        guiStage.getViewport().update(width, height);
        normalWidth = width;
        normalHeight = height;
    }

    @Override
    public void dispose() {
        stage.dispose();
        guiStage.dispose();
        skin.dispose();
    }

    public void addEnemy(EnemyShip e) {
        enemies.add(e);
    }

    public void removeEnemy(EnemyShip e) {
        enemies.remove(e);
    }

    public void createLockOnLaser() {
        /*
        EnemyShip closest = null;
        float closestDistance = Float.MAX_VALUE;
        float angle = 0;
        for (EnemyShip e : enemies) {
            float dist = Vector2.dst2(e.getX(), e.getY(), player.getX(), player.getY());
            if (dist < closestDistance) {
                float angleBetween = (float) Math.atan2(e.getY() - player.getY(), e.getX() - player.getX());
                angleBetween = angleBetween % 360;
                if (angleBetween < 0) {
                    angleBetween += 360;
                }
                float currentDirection = direction % 360;
                if (currentDirection < 0) {
                    currentDirection += 360;
                }
                if (Math.abs(currentDirection - angleBetween) < 25) {
                    closest = e;
                    closestDistance = dist;
                    angle = angleBetween;
                }
            }
        }

        Laser laser; //TODO get homing attacks working make final boss
        if (closest != null) {
            laser = new Laser(laserTexture, true, (int) angle);
        } else {
        */
            Laser laser = new Laser(laserTexture, true, (int) direction);
       // }
            laser.setPosition(player.getX() + player.getWidth() / 2 * (float)Math.cos(Math.toRadians(direction + 90)),
                    player.getY() + player.getHeight() / 2 * (float)Math.sin(Math.toRadians(direction + 90)));
            laser.setDamage(2);
            stage.addActor(laser);
            shootSfx.play();



    }

    @Override
    public void render() {
       // System.out.println(health);
        gl.glClearColor(0f, 1f, 0f, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiStage.act(Gdx.graphics.getDeltaTime());
        //direction = (float) Math.toDegrees(MathUtils.lerpAngle((float) Math.toRadians(direction),
        //        (float) Math.toRadians(targetDirection), 0.5f));
        direction = targetDirection;
        //System.out.println(direction);
        float x = (float)Math.cos(Math.toRadians(360 - direction + 90));
        float y = (float)Math.sin(Math.toRadians(360 - direction + 90));
        player.moveBy(speed * x, speed * y);
        stage.act(Gdx.graphics.getDeltaTime());

        /*
        if (Vector2.dst(player.getX(), player.getY(), finalBase.getX(), finalBase.getY()) < normalHeight) {
            stage.getCamera().viewportHeight = normalHeight * 8;
            stage.getCamera().viewportWidth = normalWidth * 8;
        } else {
            stage.getCamera().viewportHeight = normalHeight * 2;
            stage.getCamera().viewportWidth = normalWidth * 2;
        }
        */


       // if (Math.abs(direction % 360 - targetDirection % 360) < 1) {
            stage.getCamera().position.lerp(new Vector3(player.getX() + Gdx.graphics.getWidth() * x - 32,
                    player.getY() + Gdx.graphics.getHeight() * y - 32, 0), 0.01f);
        //} else {
        //    stage.getCamera().position.lerp(new Vector3(player.getX() + Gdx.graphics.getWidth() * x,
        //            player.getY() + Gdx.graphics.getHeight() * y, 0), 0.005f);
       // }
        //System.out.println(player.getX() + player.getWidth() / 2 + ", " + (player.getY() + player.getHeight() / 2));
        //System.out.println(stage.getCamera().position.x + "` "  + stage.getCamera().position.y);
        stage.getCamera().up.set(0, 1, 0);
        stage.getCamera().direction.set(0, 0, -1);
        player.setOrigin(player.getWidth() / 2, player.getHeight() / 2);
        player.setRotation(-direction);
        compass.setRotation(-direction);

        //((OrthographicCamera)stage.getCamera()).rotate(direction);
        stage.getCamera().update();
        backgroundShader.begin();
        backgroundShader.setUniformf("offset", player.getX() / background.getWidth(), player.getY() / background.getHeight());
        backgroundShader.end();
        stage.getBatch().setShader(backgroundShader);
        stage.getBatch().begin();
        stage.getBatch().draw(background, stage.getCamera().position.x - Gdx.graphics.getWidth() * 2,
                stage.getCamera().position.y - Gdx.graphics.getHeight() * 2,
                stage.getCamera().viewportWidth * 2, stage.getCamera().viewportHeight * 2, 0, 0,
                Gdx.graphics.getWidth() * 2 / background.getWidth(), Gdx.graphics.getHeight() * 2 / background.getHeight());
        stage.getBatch().end();
        stage.getBatch().setShader(defaultShader);
        stage.draw();
        guiStage.draw();
        shapes.setProjectionMatrix(stage.getCamera().combined);
        if (scannedLocation != null) {
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.RED);
            shapes.line(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, scannedLocation.x, scannedLocation.y);
            shapes.end();
        }
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Actor a : stage.getActors()) {
            if (a instanceof Ship) {
                if (((Ship) a).getShield()) {
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    shapes.setColor(0, 0, 1, 0.3f);
                    shapes.circle(a.getX() + a.getWidth() / 2, a.getY() + a.getHeight() / 2, a.getWidth() / 2);
                }
                if (a != player) {
                    Rectangle rect = ((Collidable) a).getBoundingRectangle();
                    if (rect.overlaps(player.boundingBox) && ((EnemyShip) a).health > 0 && !(a instanceof EnemyBase)) {
                        if (player.getShield()) {
                            player.setShield(false);
                            energy = Math.max(0, energy - 60);
                        } else {
                            health = Math.max(0, health - ((EnemyShip) a).collideDamage);
                        }
                        if (speed == HIGH_SPEED) {
                            ((Ship) a).getHit(8);
                        } else {
                            ((Ship) a).getHit(8);
                        }
                        explosionSfx.play();
                    }
                }
            }

            if (a instanceof Laser) {
                Laser l = (Laser) a;
                if (l.playerOwned) {
                    for (Actor other : enemies) {
                        if (other != null && other instanceof EnemyShip) {
                            EnemyShip enemy = (EnemyShip) other;
                            if (enemy.getBoundingRectangle().overlaps(l.boundingRectangle)) {
                                if (l.shieldPiercing) {
                                    enemy.shield = false;
                                    explosionSfx.play();
                                } else {
                                    hitSfx.play();
                                }
                                enemy.getHit(l.getDamage());
                                l.addAction(Actions.removeActor());
                            }
                        } else if (other == null) {

                        }
                    }
                } else {
                    if (l.boundingRectangle.overlaps(player.boundingBox)) {
                        if (player.getShield()) {
                            energy = Math.max(0, energy - l.getDamage() * 2);
                            if (energy <= 0) {
                                player.setShield(false);
                            }
                        } else {
                            health = Math.max(0, health - l.getDamage());
                        }
                        l.addAction(new RemoveActorAction());
                        hitSfx.play();
                    }
                }
            }
            if (a instanceof Collidable) {
                //Rectangle rect = ((Collidable) a).getBoundingRectangle();
                //shapes.rect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
            }
        }
        int guiBarHeight = Gdx.graphics.getHeight() / 32;
        //shapes.setColor(1, 1, 1, 0.3f);
        //shapes.rect(player.boundingBox.x, player.boundingBox.y, player.boundingBox.getWidth(), player.boundingBox.getHeight());
        shapes.end();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setProjectionMatrix(guiStage.getCamera().combined);
        //shapes.setColor(Color.GRAY);
        //shapes.rect(0, Gdx.graphics.getHeight() - guiBarHeight, Gdx.graphics.getWidth() / 2, guiBarHeight);
        shapes.setColor(new Color(152f / 255, 222f / 255, 90f / 255, 1));
        shapes.rect(0, Gdx.graphics.getHeight() - guiBarHeight, Gdx.graphics.getWidth() / 2 * ((float)health / MAX_HEALTH), guiBarHeight);

        //shapes.setColor(Color.GRAY);
        //shapes.rect(0, Gdx.graphics.getHeight() - guiBarHeight * 2, Gdx.graphics.getWidth() / 2, guiBarHeight);
        shapes.setColor(new Color(253f / 255, 220f / 255, 90f / 255, 1));
        shapes.rect(0, Gdx.graphics.getHeight()  - guiBarHeight * 2, Gdx.graphics.getWidth() / 2 * ((float)energy / MAX_ENERGY), guiBarHeight);
        shapes.end();
        guiStage.getBatch().begin();
        guiStage.getBatch().draw(healthTexture, 0, Gdx.graphics.getHeight() - guiBarHeight, Gdx.graphics.getWidth() / 2, guiBarHeight);
        guiStage.getBatch().draw(energyTexture, 0, Gdx.graphics.getHeight() - guiBarHeight * 2, Gdx.graphics.getWidth() / 2, guiBarHeight);
        guiStage.getBatch().end();
        //System.out.println(Gdx.graphics.getWidth() / 3 * ((float) health / MAX_HEALTH));
    }

    public TextureRegion getLaserTexture(boolean large) {
        if (large) {
            return laserTexture;
        } else {
            return smallLaserTexture;
        }
    }

    public void addToStage(Actor a) {
        stage.addActor(a);
    }

    public void execute(Command c) {
        switch (c.commandType) {
            case TURN:
                doTurn(c);
                break;
            case MOVE:
                doMove(c);
                break;
            case FIRE:
                doFire(c);
                break;
            case SCAN:
                doScan(c);
                break;
            case SHIELD:
                doShield(c);
                break;
            case REPAIR:
                doRepair(c);
                break;
            case CEASEFIRE:
                doCeasefire(c);
                break;
            case MISSILE:
                doMissile(c);
                break;
        }
    }

    public PlayerShip getPlayer() {
        return player;
    }

    public void doTurn(Command c) {
        String[] args = c.data;
        if (args[0] == null) {
            return;
        }
        args[0] = args[0].replaceAll(":", "");
        args[0] = args[0].replaceAll("to", "2");
        if (args[0].equalsIgnoreCase("right")) {
            args[0] = "45";
        } else if (args[0].equalsIgnoreCase("left")) {
            args[0] = "315";
        }
        targetDirection += Integer.parseInt(args[0]);
        targetDirection %= 360;
        System.out.println("New direction is " + targetDirection);
    }

    public void doMove(Command c) {
        if (energy >= 60) {
            if (stopBurst != null) {
                stopBurst.cancel();
            }
            speed = HIGH_SPEED;
            stopBurst = new Timer.Task() {
                @Override
                public void run() {
                    speed = LOW_SPEED;
                }
            };
            Timer.schedule(stopBurst, 3.5f);
            energy = Math.max(0, energy - 60);
        }
    }

    public void doFire(Command c) {
        if (firing) {
            return;
        }
        if (energy >= 20) {
            firing = true;
            //int dir = (int)targetDirection + Integer.parseInt(args[0]);
            //dir %= 360;
            /*
            Laser laser = new Laser(laserTexture, true,(int) direction);
            laser.setPosition(player.getX(), player.getY());
            laser.setDamage(2);
            stage.addActor(laser);
            */
            createLockOnLaser();
            Timer.Task task = new Timer.Task() {
                @Override
                public void run() {
                    if (energy < 20) {
                        firing = false;
                        return;
                    } else {
                        Timer.schedule(this, 2);
                        createLockOnLaser();
                        energy = Math.max(0, energy - 20);
                    }
                }
            };
            Timer.schedule(task, 2);
            //shootSfx.play();
            energy = Math.max(0, energy - 20);
        }
    }

    public void doShield(Command c) {
        String[] args = c.data;
        if (args[0] == null) {
            return;
        }
        if (args[0].equalsIgnoreCase("down")) {
            player.setShield(false);
        } else {
            if (energy >= 20) {
                player.setShield(true);
                energy = Math.max(0, energy - 20);
            }
        }

    }

    public void doScan(Command c) {
        if (energy >= 25) {
            Actor nearestEnemy = null;
            float distance = Float.MAX_VALUE;
            for (Actor a : stage.getActors()) {
                if (a instanceof EnemyShip) {
                    float dist = Vector2.dst2(a.getX(), a.getY(), player.getX(), player.getY());
                    if (dist < distance) {
                        distance = dist;
                        nearestEnemy = a;
                    }
                }
            }
            if (nearestEnemy != null) {
                scannedLocation = new Vector2(nearestEnemy.getX() + nearestEnemy.getWidth() / 2,
                        nearestEnemy.getY() + nearestEnemy.getHeight() / 2);
                if (removeLocation != null) {
                    removeLocation.cancel();
                }
                removeLocation = new Timer.Task() {
                    @Override
                    public void run() {
                        scannedLocation = null;
                    }
                };
                Timer.schedule(removeLocation, 10);
            }
            energy = Math.max(0, energy - 25);
        }
    }

    public void doRepair(Command c) {
        if (energy >= 80) {
            energy = Math.max(0, energy - 80);
            health = Math.min(MAX_HEALTH, health + 30);
        }
    }

    public void doMissile(Command c) {
        if (energy >= 30) {
            String[] args = c.data;
            args[0] = args[0].replace(":", "");
            if (args[0].equalsIgnoreCase("right")) {
                args[0] = "90";
            } else if (args[0].equalsIgnoreCase("left")) {
                args[0] = "270";
            } else if (args[0].equalsIgnoreCase("ahead")) {
                args[0] = "0";
            } else if (args[0].equalsIgnoreCase("behind")) {
                args[0] = "180";
            }
            int dir = (int) targetDirection + Integer.parseInt(args[0]);
            dir %= 360;
            Laser laser = new Laser(missileTexture, true, (int) 360 - dir + 90);
            laser.setPosition(player.getX(), player.getY());
            laser.setDamage(5);
            laser.shieldPiercing = true;
            stage.addActor(laser);
            energy = Math.max(0, energy - 30);
        }
    }

    public void doCeasefire(Command c) {
        firing = false;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public void showToast(String message) {
        actionResolver.showToast(message, 5000);
    }
}
