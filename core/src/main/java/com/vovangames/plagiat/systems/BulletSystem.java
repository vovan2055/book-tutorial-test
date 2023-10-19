package com.vovangames.plagiat.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.vovangames.plagiat.components.BulletComponent;
import com.vovangames.plagiat.components.CharacterComponent;


/*The only thing that we will have to do is add these entities to the
bullet collision world.*/
    public class BulletSystem extends EntitySystem implements EntityListener {
        public final btCollisionConfiguration collisionConfiguration;
        public final btCollisionDispatcher dispatcher;
        public final btBroadphaseInterface broadphase;
        public final btConstraintSolver solver;
        public final btDiscreteDynamicsWorld collisionWorld;
        private btGhostPairCallback ghostPairCallback;
        public int maxSubSteps = 5;
        public float fixedTimeStep = 1f / 60f;
        public DebugDrawer drawer;

        @Override
        public void addedToEngine(Engine engine) {
            engine.addEntityListener(Family.all(BulletComponent.class).get(),
                this);
        }
        public BulletSystem() {
            collisionConfiguration = new
                btDefaultCollisionConfiguration();
            dispatcher = new
                btCollisionDispatcher(collisionConfiguration);
            broadphase = new btAxisSweep3(new Vector3(-1000, -1000,
                -1000), new Vector3(1000, 1000, 1000));
            solver = new btSequentialImpulseConstraintSolver();
            collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
            ghostPairCallback = new btGhostPairCallback();
            broadphase.getOverlappingPairCache().
                setInternalGhostPairCallback(ghostPairCallback);
            this.collisionWorld.setGravity(new Vector3(0, -10, 0));
            drawer = new DebugDrawer();
            collisionWorld.setDebugDrawer(drawer);
            System.out.println(collisionWorld.getGravity());
            drawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
        @Override
        public void update(float deltaTime) {
            collisionWorld.stepSimulation(deltaTime, maxSubSteps,
                fixedTimeStep);
        }
        public void dispose() {
            collisionWorld.dispose();
            if (solver != null) solver.dispose();
            if (broadphase != null) broadphase.dispose();
            if (dispatcher != null) dispatcher.dispose();
            if (collisionConfiguration != null)
                collisionConfiguration.dispose();
            ghostPairCallback.dispose();
            drawer.dispose();
        }
        @Override
        public void entityAdded(Entity entity) {
            BulletComponent bulletComponent =
                entity.getComponent(BulletComponent.class);
            if (bulletComponent.body != null) {
                collisionWorld.addRigidBody((btRigidBody)
                    bulletComponent.body);
            }
        }
        public void removeBody(Entity entity) {
            BulletComponent comp =
                entity.getComponent(BulletComponent.class);
            if (comp != null)
                collisionWorld.removeCollisionObject(comp.body);
            CharacterComponent character =
                entity.getComponent(CharacterComponent.class);
            if (character != null) {
                collisionWorld.removeAction
                    (character.characterController);
                collisionWorld.removeCollisionObject
                    (character.ghostObject);
            }
        }
        @Override
        public void entityRemoved(Entity entity) {
        }
    }
