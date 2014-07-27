package org.fabian.csg;

import java.io.IOException;
import java.util.ArrayList;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;

public class Plane implements Savable {
	public static final double EPSILON = 1e-5;
	private Vector3f normal;
	private float w;
	public Plane(Vector3f normal, float w) {
		this.normal = normal;
		this.w = w;
	}
	
	public static Plane fromPoints(Vector3f a, Vector3f b, Vector3f c) {
		Vector3f n = b.subtract(a).cross(c.subtract(a)).normalizeLocal();
		return new Plane(n, n.dot(a));
	}
	
	public Plane clone() {
		return new Plane(normal.clone(), w);
	}
	
	public void flip() {
		normal = normal.negate();
		w = -w;
	}
	
	public void splitPolygon(Polygon polygon, ArrayList<Polygon> coplanarFront, ArrayList<Polygon> coplanarBack, ArrayList<Polygon> front, ArrayList<Polygon> back) {
		final int COPLANAR = 0;
		final int FRONT = 1;
		final int BACK = 2;
		final int SPANNING = 3;
		
		int polygonType = 0;
		int[] types = new int[polygon.getVertices().length];
		for (int i = 0; i < polygon.getVertices().length; i++) {
			float t = normal.dot(polygon.getVertices()[i].getPosition()) - w;
			int type = (t < -EPSILON) ? BACK : (t > EPSILON) ? FRONT : COPLANAR;
			polygonType |= type;
			types[i] = type;
		}
		
		switch (polygonType) {
		case COPLANAR:
			(normal.dot(polygon.getPlane().normal) > 0 ? coplanarFront : coplanarBack).add(polygon);
			break;
		case FRONT:
			front.add(polygon);
			break;
		case BACK:
			back.add(polygon);
			break;
		case SPANNING:
			ArrayList<Vertex> f = new ArrayList<Vertex>();
			ArrayList<Vertex> b = new ArrayList<Vertex>();
			for (int i = 0; i < polygon.getVertices().length; i++) {
				int j = (i + 1) % polygon.getVertices().length;
				int ti = types[i];
				int tj = types[j];
				Vertex vi = polygon.getVertices()[i];
				Vertex vj = polygon.getVertices()[j];
				if (ti != BACK) f.add(vi);
				if (ti != FRONT) b.add(ti != BACK ? vi.clone() : vi);
				if ((ti | tj) == SPANNING) {
					float t = (w - normal.dot(vi.getPosition())) / normal.dot(vj.getPosition().subtract(vi.getPosition()));
					Vertex v = vi.interpolate(vj, t);
					f.add(v);
					b.add(v.clone());
				}
			}
			if (f.size() >= 3) front.add(new Polygon(f.toArray(new Vertex[0]), polygon.getShared()));
			if (b.size() >= 3) back.add(new Polygon(b.toArray(new Vertex[0]), polygon.getShared()));
			break;
		}
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule out = ex.getCapsule(this);
		out.write(normal, "Normal", Vector3f.ZERO);
		out.write(w, "Width", 0f);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule in = im.getCapsule(this);
		w = in.readFloat("Width", 0f);
		normal = (Vector3f) in.readSavable("Normal", Vector3f.ZERO);
	}
}
