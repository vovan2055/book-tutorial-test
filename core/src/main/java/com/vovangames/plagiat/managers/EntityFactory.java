package com.vovangames.plagiat.managers;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.vovangames.plagiat.bullet.MotionState;
import com.vovangames.plagiat.components.*;
import com.vovangames.plagiat.systems.BulletSystem;

public class EntityFactory {

    static ModelBuilder modelBuilder;
    static Texture playerTexture;
    static Model playerModel;
    static Material material;

    static {
        modelBuilder = new ModelBuilder();
        playerTexture = new
            Texture("libgdx.png");
        material = new
            Material(TextureAttribute.createDiffuse(playerTexture),
            ColorAttribute.createSpecular(1, 1, 1, 1),
            FloatAttribute.createShininess(8f));
        playerModel = modelBuilder.createCapsule(2f, 6f, 16, material,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                | VertexAttributes.Usage.TextureCoordinates);
    }
    private static Entity createCharacter(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(playerModel, x, y, z);
        entity.add(modelComponent);
        CharacterComponent characterComponent = new
            CharacterComponent();
        characterComponent.ghostObject = new btPairCachingGhostObject();
        characterComponent.ghostObject.setWorldTransform
            (modelComponent.instance.transform);
        characterComponent.ghostShape = new btCapsuleShape(2f, 2f);
        characterComponent.ghostObject.setCollisionShape
            (characterComponent.ghostShape);
        characterComponent.ghostObject.setCollisionFlags(btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT);
        characterComponent.characterController = new
            btKinematicCharacterController(characterComponent.ghostObject,
            characterComponent.ghostShape, .35f);
        characterComponent.ghostObject.userData = entity;
        entity.add(characterComponent);
        bulletSystem.collisionWorld.addCollisionObject
            (entity.getComponent(CharacterComponent.class).ghostObject,
                (short)
                    btBroadphaseProxy.CollisionFilterGroups.CharacterFilter,
                (short)
                    (btBroadphaseProxy.CollisionFilterGroups.AllFilter));
        bulletSystem.collisionWorld.addAction(entity.getComponent
            (CharacterComponent.class).characterController);
        return entity;
    }

    public static Entity createStaticEntity(Model model, float x, float y, float z) {
        final BoundingBox boundingBox = new BoundingBox();
        model.calculateBoundingBox(boundingBox);
        Vector3 tmpV = new Vector3();
        btCollisionShape col = new
            btBoxShape(tmpV.set(boundingBox.getWidth() * 0.5f,
            boundingBox.getHeight() * 0.5f, boundingBox.getDepth() * 0.5f));
        Entity entity = new Entity();
        ModelComponent modelComponent = new ModelComponent(model, x, y,
            z);
        entity.add(modelComponent);
        BulletComponent bulletComponent = new BulletComponent();
        bulletComponent.bodyInfo = new
            btRigidBody.btRigidBodyConstructionInfo(0, null, col,
            Vector3.Zero);
        bulletComponent.body = new
            btRigidBody(bulletComponent.bodyInfo);
        bulletComponent.body.userData = entity;
        bulletComponent.motionState = new
            MotionState(modelComponent.instance.transform);
        ((btRigidBody) bulletComponent.body)
            .setMotionState(bulletComponent.motionState);
        entity.add(bulletComponent);
        return entity;
    }

    public static Entity createPlayer(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new PlayerComponent());
        return entity;
    }

    public static Entity createEnemy(BulletSystem bulletSystem, float x, float y, float z) {
        Entity entity = createCharacter(bulletSystem, x, y, z);
        entity.add(new EnemyComponent(EnemyComponent.STATE.HUNTING));
        entity.add(new StatusComponent());
        return entity;
    }
}
