package io.github.clowngraphics.rerenderer.render;

import io.github.alphameo.linear_algebra.vec.*;
import io.github.clowngraphics.rerenderer.render.texture.PolygonUVCoordinates;
import io.github.shimeoki.jshaper.ObjFile;
import io.github.shimeoki.jshaper.obj.Face;
import io.github.shimeoki.jshaper.obj.TextureVertex;

import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private List<Vertex> vertices;
    private List<Integer> vertexIndices;
    public PolygonUVCoordinates polygonUVCoordinates;
    public Vector3 normal;

    public Polygon(List<Vertex> vertices, PolygonUVCoordinates polygonUVCoordinates, List<Integer> vertexIndices) {
        this.vertices = vertices;
        this.polygonUVCoordinates = polygonUVCoordinates;
        this.normal = computeNormal();
        this.vertexIndices = vertexIndices;
    }

    public Polygon(List<Vertex> vertices, PolygonUVCoordinates polygonUVCoordinates) {
        this.vertices = vertices;
        this.polygonUVCoordinates = polygonUVCoordinates;
        this.normal = computeNormal();
        this.vertexIndices = new ArrayList<>();
        vertexIndices.add(0);
        vertexIndices.add(1);
        vertexIndices.add(2);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public void setVertexIndices(List<Integer> vertexIndices) {
        this.vertexIndices = vertexIndices;
    }


    public PolygonUVCoordinates getPolygonUVCoordinates() {
        return polygonUVCoordinates;
    }

    public void setPolygonUVCoordinates(PolygonUVCoordinates polygonUVCoordinates) {
        this.polygonUVCoordinates = polygonUVCoordinates;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public Vector3 computeNormal() {
        if (vertices.size() < 3) {
            throw new IllegalStateException("Polygon must have at least 3 vertices to compute a normal");
        }

        Vector4 normal = new Vec4(0, 0, 0, 0);

        for (int i = 0; i < vertices.size(); i++) {
            Vertex current = vertices.get(i);
            Vertex next = vertices.get((i + 1) % vertices.size());

            Vector4 currentPosition = current.getValues();
            Vector4 nextPosition = next.getValues();

            normal = Vec4Math.add(normal, Vec4Math.sub(currentPosition, nextPosition));
        }


        return normalize(new Vec3(normal.x(), normal.y(), normal.z()));
    }

    private Vector3 normalize(Vec3 v) {
        double length = Math.sqrt(v.x() * v.x() + v.y() * v.y() + v.z() * v.z());
        if (length == 0) {
            throw new IllegalStateException("Cannot normalize a zero-length vector");
        }
        return new Vec3((float) (v.x() / length),(float) (v.y() / length),(float) (v.z() / length));
    }


    public static List<Polygon> convertPolgonsFromJShaper(ObjFile obj) {

        List<Face> faces = obj.elements().faces();
        List<Polygon> newPolygons = new ArrayList<>();


        for (Face face : faces) {

            // Getting vertices
            List<io.github.shimeoki.jshaper.obj.Vertex> oldVertices = new ArrayList<>();
            oldVertices.add(face.triplets().get(0).vertex());
            oldVertices.add(face.triplets().get(1).vertex());
            oldVertices.add(face.triplets().get(2).vertex());

            // Getting UV's
            TextureVertex tv0 = face.triplets().get(0).textureVertex();
            TextureVertex tv1 = face.triplets().get(1).textureVertex();
            TextureVertex tv2 = face.triplets().get(2).textureVertex();

            List<Integer> vertexIndices = new ArrayList<>();
            vertexIndices.add(obj.vertexData().vertices().indexOf(oldVertices.get(0)));
            vertexIndices.add(obj.vertexData().vertices().indexOf(oldVertices.get(1)));
            vertexIndices.add(obj.vertexData().vertices().indexOf(oldVertices.get(2)));

            newPolygons.add(new Polygon(Vertex.convertVerticesFromJShaper(oldVertices),
                    PolygonUVCoordinates.convertToUV(tv0, tv1, tv2),
                    vertexIndices));
        }

        return newPolygons;
    }

    public static Polygon copy(Polygon polygon){
        return new Polygon(polygon.getVertices(),polygon.getPolygonUVCoordinates(), polygon.getVertexIndices());
    }

}
