package org.fabian.csg.shapes;

import java.util.ArrayList;

import org.fabian.csg.CSG;
import org.fabian.csg.Polygon;
import org.fabian.csg.Vertex;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class CylinderBrush extends CSG {
	private Vector3f axisX;
	private Vector3f axisY;
	private Vector3f axisZ;
	private Vector3f start;
	private Vector3f end;
	private Vector3f ray;
	private float radius;
	private int slices;
	private Vertex point(int stack, float slice, float normalBlend) {
		float angle = slice * FastMath.PI * 2;
		Vector3f out = axisX.mult((float) Math.cos(angle)).addLocal(axisY.mult((float) Math.sin(angle)));
		Vector3f pos = start.add(ray.mult(stack)).add(out.mult(radius));
		Vector3f normal = out.mult(1-Math.abs(normalBlend)).add(axisZ.mult(normalBlend));
		return new Vertex(pos, normal);
	}
	public CylinderBrush(Vector3f start, Vector3f end, float radius, int slices) {
		this.start = start;
		this.end = end;
		this.radius = radius;
		this.slices = slices;
		ray = end.subtract(start);
		axisZ = ray.normalize();
		boolean isY = (Math.abs(axisZ.y) > 0.5f);
		axisX = new Vector3f(isY ? 1 : 0, isY ? 0 : 1, 0).cross(axisZ).normalizeLocal();
		axisY = axisX.cross(axisZ).normalizeLocal();
		Vertex startV = new Vertex(start, axisZ.negate());
		Vertex endV = new Vertex(end, axisZ.normalizeLocal());
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		for (int i = 0; i < slices; i++) {
			float t0 = i / 1f / slices;
			float t1 = (i + 1f) / slices;
			polygons.add(new Polygon(new Vertex[] {startV, point(0, t0, -1), point(0, t1, -1)}, null));
			polygons.add(new Polygon(new Vertex[] {point(0, t1, 0), point(0, t0, 0), point(1, t0, 0), point(1, t1, 0)}, null));
			polygons.add(new Polygon(new Vertex[] {endV, point(1, t1, 1), point(1, t0, 1)}, null));
		}
		setPolygons(polygons.toArray(new Polygon[0]));
	}

}
