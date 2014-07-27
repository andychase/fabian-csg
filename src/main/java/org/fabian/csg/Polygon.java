package org.fabian.csg;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

public class Polygon implements Savable {
	private Vertex[] vertices;
	private Savable shared;
	private Plane plane;
	public Plane getPlane() {
		return plane;
	}
	public void setPlane(Plane plane) {
		this.plane = plane;
	}
	public Polygon(Vertex[] vertices, Savable shared) {
		this.vertices = vertices;
		this.shared = shared;
		this.plane = Plane.fromPoints(vertices[0].getPosition(), vertices[1].getPosition(), vertices[2].getPosition());
	}
	public Polygon clone() {
		Vertex[] newVertices = new Vertex[vertices.length];
		for (int i = 0; i < vertices.length; i++)
			newVertices[i] = vertices[i].clone();
		return new Polygon(newVertices, shared);
	}
	public Vertex[] getVertices() {
		return vertices;
	}
	public void setVertices(Vertex[] vertices) {
		this.vertices = vertices;
	}
	public Savable getShared() {
		return shared;
	}
	public void setShared(Savable shared) {
		this.shared = shared;
	}
	public void flip() {
		Vertex[] temp = new Vertex[vertices.length];
		for (int i = 0; i < temp.length; i++) {
			vertices[i].flip();
			temp[temp.length - i - 1] = vertices[i];
		}
		plane.flip();
	}
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule capsule = ex.getCapsule(this);
		capsule.write(vertices, "Vertices", new Vertex[0]);
		capsule.write(shared, "Shared", null);
		capsule.write(plane, "Plane", null);
	}
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule in = im.getCapsule(this);
		vertices = (Vertex[]) in.readSavableArray("Vertices", new Vertex[0]);
		shared = in.readSavable("Shared", null);
		plane = (Plane) in.readSavable("Plane", null);
	}
}
