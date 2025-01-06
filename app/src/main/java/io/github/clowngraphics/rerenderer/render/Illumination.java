package io.github.clowngraphics.rerenderer.render;

import io.github.alphameo.linear_algebra.vec.Vec3;
import io.github.alphameo.linear_algebra.vec.Vector3;
import io.github.clowngraphics.rerenderer.model.Model;

import java.util.List;


public class Illumination {

    private float brightness;

    private Vector3 ray;


    public Illumination(final float baseBrightness) {
        this(baseBrightness, new Vec3(1, 0, 0));
    }

    public Illumination(float brightness, Vector3 ray) {
        this.brightness = brightness;
        this.ray = ray;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setRay(Vector3 ray) {
        this.ray = ray;
    }

    public static List<Vec3> computeNormals(Model model){

        List<Vec3> normals = model.getNormals();

    }
}

