package org.fabian.csg;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

public class Vertex implements Savable {
	private Vector3f position;
	private Vector3f normal;
	private Vector2f uv;
	public Vertex(Vector3f position, Vector3f normal) {
		this(position, normal, new Vector2f(0f, 0f));
	}
	public Vertex(Vector3f position, Vector3f normal, Vector2f uv) {
		this.position = position;
		this.normal = normal;
		this.uv = uv;
	}
	public Vertex clone() {
		return new Vertex(position.clone(), normal.clone(), uv.clone());
	}
	public void flip() {
		normal.negateLocal();
	}
	public Vector2f getUv() {
		return uv;
	}
	public void setUv(Vector2f uv) {
		this.uv = uv;
	}
	public Vector3f getPosition() {
		return position;
	}
	public void setPosition(Vector3f position) {
		this.position = position;
	}
	public Vector3f getNormal() {
		return normal;
	}
	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}
	public Vertex interpolate(Vertex other, float progress) {
		Vector3f newPosition = position.add(other.getPosition().subtract(position).mult(progress));
		Vector3f newNormal = normal.add(other.getNormal().subtract(normal).mult(progress));
		Vector2f newUv = uv.add(other.getUv().subtract(uv).mult(progress));
		return new Vertex(newPosition, newNormal, newUv);
	}
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule out = ex.getCapsule(this);
		out.write(position, "Position", Vector3f.ZERO);
		out.write(normal, "Normal", Vector3f.ZERO);
		out.write(uv, "UV", Vector2f.ZERO);
	}
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule in = im.getCapsule(this);
		position = (Vector3f) in.readSavable("Position", Vector3f.ZERO);
		normal = (Vector3f) in.readSavable("Normal", Vector3f.ZERO);
		uv = (Vector2f) in.readSavable("UV", Vector2f.ZERO);
	}
}
