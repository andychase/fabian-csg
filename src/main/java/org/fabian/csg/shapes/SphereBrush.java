package org.fabian.csg.shapes;

import java.util.ArrayList;

import org.fabian.csg.CSG;
import org.fabian.csg.Polygon;
import org.fabian.csg.Vertex;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class SphereBrush extends CSG {
	private static Vertex sphereVertex(Vector3f c, float r, float theta, float phi) {
		theta *= FastMath.TWO_PI;
		phi *= FastMath.PI;
		Vector3f dir = new Vector3f(
				FastMath.cos(theta) * FastMath.sin(phi),
				FastMath.cos(phi),
				FastMath.sin(theta) * FastMath.sin(phi));
		return new Vertex(c.add(dir.mult(r)), dir);
	}
	public SphereBrush(Vector3f c, float r, float slices, float stacks) {
		super();
		ArrayList<Polygon> polygons = new ArrayList<Polygon>();
		ArrayList<Vertex> vertices = new ArrayList<Vertex>();
		for (int i = 0; i < slices; i++) {
			for (int j = 0; j < stacks; j++) {
				vertices.clear();
				vertices.add(sphereVertex(c, r, i/slices, j/stacks));
				if (j > 0) vertices.add(sphereVertex(c, r, (i+1) / slices, j/stacks));
				if (j < stacks - 1) vertices.add(sphereVertex(c, r, (i + 1) / slices, (j + 1) / stacks));
				vertices.add(sphereVertex(c, r, i/slices, (j+1)/stacks));
				polygons.add(new Polygon(vertices.toArray(new Vertex[0]), null));
			}
		}
		setPolygons(polygons.toArray(new Polygon[0]));
	}

}
