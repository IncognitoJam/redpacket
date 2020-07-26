package com.github.incognitojam.redengine.physics;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class PhysicsBody {
    private final float mass;
    @NotNull
    private final Vector3f velocity;
    @NotNull
    private final Vector3f acceleration;

    public PhysicsBody(float mass) {
        this.mass = mass;
        velocity = new Vector3f();
        acceleration = new Vector3f();
    }

    protected void update(double interval) {
        final float deltaTime = (float) interval;

        // s = ut + (1/2)at^2
        final Vector3f deltaPosition = new Vector3f(acceleration)
            .mul(0.5F * deltaTime * deltaTime)
            .add(new Vector3f(velocity).mul(deltaTime));

        // v = u + at
        velocity.add(acceleration.mul(deltaTime));

        // reset acceleration after each update
        acceleration.zero();

        // Update X axis.
        if (!updatePosition(getPosition().add(deltaPosition.x, 0, 0, new Vector3f()))) {
            velocity.x = 0;
        }
        // Update Y axis.
        if (!updatePosition(getPosition().add(0, deltaPosition.y, 0, new Vector3f()))) {
            velocity.y = 0;
        }
        // Update Z axis.
        if (!updatePosition(getPosition().add(0, 0, deltaPosition.z, new Vector3f()))) {
            velocity.z = 0;
        }
    }

    @NotNull
    public abstract Vector3fc getPosition();

    public abstract boolean updatePosition(@NotNull Vector3fc position);

    public float getMass() {
        return mass;
    }

    @NotNull
    public Vector3fc getVelocity() {
        return velocity;
    }

    @NotNull
    public Vector3fc getAcceleration() {
        return acceleration;
    }

    public void applyForce(@NotNull Vector3fc force) {
        acceleration.add(force.div(getMass(), new Vector3f()));
    }
}
