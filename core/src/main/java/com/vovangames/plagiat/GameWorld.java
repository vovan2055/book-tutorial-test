package com.vovangames.plagiat;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.vovangames.plagiat.components.CharacterComponent;
import com.vovangames.plagiat.components.ModelComponent;
import com.vovangames.plagiat.managers.EntityFactory;
import com.vovangames.plagiat.systems.BulletSystem;
import com.vovangames.plagiat.systems.EnemySystem;
import com.vovangames.plagiat.systems.PlayerSystem;
import com.vovangames.plagiat.systems.RenderSystem;

public class GameWorld {

private static float FOV = 67;
private ModelBatch batch;
private Environment environment;
private PerspectiveCamera cam;

private Engine engine;
public BulletSystem bulletSystem;
public ModelBuilder modelBuilder = new ModelBuilder();
Model wallHorizontal = modelBuilder.createBox(40, 20, 1,
    new Material(ColorAttribute.createDiffuse(Color.WHITE),
        ColorAttribute.createSpecular(Color.RED), FloatAttribute
        .createShininess(16f)), VertexAttributes.Usage.Position
        | VertexAttributes.Usage.Normal);
Model wallVertical = modelBuilder.createBox(1, 20, 40,
    new Material(ColorAttribute.createDiffuse(Color.GREEN),
        ColorAttribute.createSpecular(Color.WHITE),
        FloatAttribute.createShininess(16f)),
    VertexAttributes.Usage.Position |
        VertexAttributes.Usage.Normal);
Model groundModel = modelBuilder.createBox(40, 1, 40,
    new Material(ColorAttribute.createDiffuse(Color.YELLOW),
        ColorAttribute.createSpecular(Color.BLUE),
        FloatAttribute.createShininess(16f)),
    VertexAttributes.Usage.Position
        | VertexAttributes.Usage.Normal);
private Entity character;
private void createPlayer(float x, float y, float z) {
    character = EntityFactory.createPlayer(bulletSystem, x, y, z);
    engine.addEntity(character);
}

private void initModelBatch() {
    batch = new ModelBatch();
}

private void initEnvironment() {
    environment = new Environment();
    environment.set(new ColorAttribute(ColorAttribute.AmbientLight,
        0.3f, 0.3f, 0.3f, 1f));
}

private void initPersCamera() {
    cam = new PerspectiveCamera(FOV, Core.VIRTUAL_WIDTH, Core.VIRTUAL_HEIGHT);
    cam.far = 100000;
    cam.position.set(0, 10, 0);
    cam.update();
}

/*With the camera set we can now fill in the resize function as well*/
public void resize(int width, int height) {
    cam.viewportHeight = height;
    cam.viewportWidth = width;
}


    public GameWorld(/*GameUI gameUI*/) {
        Bullet.init();
        initEnvironment();
        initModelBatch();
        initPersCamera();
        addSystems();
        addEntities();
    }

    private void addEntities() {
        createGround();
        createPlayer(5, 5, 5);
    }

    private void createGround() {
        engine.addEntity(EntityFactory.createStaticEntity
            (groundModel, 0, 0, 0));
        engine.addEntity(EntityFactory.createStaticEntity
            (wallHorizontal, 0, 10, -20));
        engine.addEntity(EntityFactory.createStaticEntity
            (wallHorizontal, 0, 10, 20));
        engine.addEntity(EntityFactory.createStaticEntity
            (wallVertical, 20, 10, 0));
        engine.addEntity(EntityFactory.createStaticEntity
            (wallVertical, -20, 10, 0));
        ModelBuilder b = new ModelBuilder();
        Model m = b.createLineGrid(1000, 1000, 1000, 1000, new Material(ColorAttribute.createDiffuse(Color.WHITE)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        engine.addEntity(new Entity().add(new ModelComponent(m, 0, 0, 0)));
    }

    private void addSystems() {
        engine = new Engine();
        engine.addSystem(new RenderSystem(batch, environment));
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(new PlayerSystem(this, cam));
        engine.addSystem(new EnemySystem(this));
    }

    public void render(float delta) {
        renderWorld(delta);
    }

    protected void renderWorld(float delta) {
        batch.begin(cam);
        engine.update(delta);
        batch.end();
        BulletSystem s = engine.getSystem(BulletSystem.class);
        s.drawer.begin(cam);
        s.collisionWorld.debugDrawWorld();
        s.drawer.end();
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent
            (CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject
            (character.getComponent(CharacterComponent.class).ghostObject);
        character.getComponent(CharacterComponent.class)
            .characterController.dispose();
        character.getComponent(CharacterComponent.class)
            .ghostObject.dispose();
        character.getComponent(CharacterComponent.class)
            .ghostShape.dispose();
        bulletSystem.dispose();
        bulletSystem = null;
        wallHorizontal.dispose();
        wallVertical.dispose();
        groundModel.dispose();
        batch.dispose();
        batch = null;
    }
}
