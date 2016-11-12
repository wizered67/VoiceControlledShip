package com.mygdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

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

    TextField textField;
    MicButton micButton;
    PlayerShip player;
    float direction;
    float targetDirection;
    int health;
    int energy;


    public Game(ActionResolver actionResolver) { this.actionResolver = actionResolver; }

    // Create the button:
    public class MicButton extends Actor {
        Texture texture = new Texture(Gdx.files.internal("mic.jpg"));

        public MicButton() {
            setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());

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
            batch.draw(texture, getX(), getY(), getOriginX(), getOriginY(), getWidth(),
                    getHeight(), getScaleX(), getScaleY(), getRotation(), 0, 0,
                    texture.getWidth(), texture.getHeight(), false, false);
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
        direction = 0;
        targetDirection = 90;
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
        player.setShield(true);

        EnemyShip enemy = new EnemyShip();
        stage.addActor(enemy);
        enemy.setPosition(3 * stage.getWidth() / 4, stage.getHeight() / 2);
        enemy.setShield(true);
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
        stage.getViewport().update(width, height, true);
        guiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        guiStage.dispose();
        skin.dispose();
    }

    @Override
    public void render() {
        gl.glClearColor(0f, 0f, 0f, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        guiStage.act(Gdx.graphics.getDeltaTime());
        direction = (float) Math.toDegrees(MathUtils.lerpAngle((float) Math.toRadians(direction),
                (float) Math.toRadians(targetDirection), 0.01f));
        //System.out.println(direction);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.getCamera().position.set(new Vector3(player.getX() + player.getWidth() / 2,
                player.getY() + player.getHeight() / 2, 0));
        stage.getCamera().up.set(0, 1, 0);
        stage.getCamera().direction.set(0, 0, -1);
        player.setRotation(-direction);
        ((OrthographicCamera)stage.getCamera()).rotate(direction);
        stage.draw();
        guiStage.draw();
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setProjectionMatrix(stage.getCamera().combined);
        for (Actor a : stage.getActors()) {
            if (a instanceof Ship) {
                if (((Ship) a).getShield()) {
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    shapes.setColor(0, 0, 1, 0.3f);
                    shapes.circle(a.getX() + a.getWidth() / 2, a.getY() + a.getHeight() / 2, a.getWidth());
                }
            }
        }
        shapes.end();
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
