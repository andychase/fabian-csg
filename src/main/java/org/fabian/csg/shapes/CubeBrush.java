package org.fabian.csg.shapes;

import java.util.ArrayList;

import org.fabian.csg.CSG;
import org.fabian.csg.Polygon;
import org.fabian.csg.Vertex;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;

public class CubeBrush extends CSG {
	private static final int dE(int v) {
		return v == 0 ? 0 : 1;
	}
	public CubeBrush(Mesh m) {
		this((BoundingBox)m.getBound());
	}
	public CubeBrush(Spatial s) {
		this((BoundingBox)s.getWorldBound());
	}
	public CubeBrush(Spatial s, boolean recentralize) {
		this((BoundingBox)s.getWorldBound(), recentralize);
	}
	public CubeBrush(BoundingBox bb) {
		this(bb.getCenter(), new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent()));
	}
	public CubeBrush(BoundingBox bb, boolean recentralize) {
		this(new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent()), new Vector3f(bb.getXExtent(), bb.getYExtent(), bb.getZExtent()));
	}
	public CubeBrush(Vector3f c, Vector3f r) {
		super();
		Vector3f[] normals = new Vector3f[] {
				new Vector3f(-1, 0, 0),
				new Vector3f(1, 0, 0),
				new Vector3f(0, -1, 0),
				new Vector3f(0, 1, 0),
				new Vector3f(0, 0, -1),
				new Vector3f(0, 0, 1)
		};
		int[][] offsets = new int[][] {
			new int[] {0, 4, 6, 2},
			new int[] {1, 3, 7, 5},
			new int[] {0, 1, 5, 4},
			new int[] {2, 6, 7, 3},
			new int[] {0, 2, 3, 1},
			new int[] {4, 5, 7, 6}
		};
		Polygon[] polygons = new Polygon[offsets.length];
		for (int idx = 0; idx < offsets.length; idx++) {
			ArrayList<Vertex> vertices = new ArrayList<Vertex>();
			for (int in = 0; in < offsets[idx].length; in++) {
				int i = offsets[idx][in];
				Vector3f pos = new Vector3f(
						c.x + r.x * (2 * dE(i & 1) - 1),
						c.y + r.y * (2 * dE(i & 2) - 1),
						c.z + r.z * (2 * dE(i & 4) - 1)
						);
				boolean uZero = pos.z < c.z;
				boolean vZero = pos.y > c.y;
				if (normals[idx].y == 1) {
					uZero = pos.z < c.z;
					vZero = pos.x > c.x;
				} else if (normals[idx].y == -1) {
					uZero = pos.z > c.z;
					vZero = pos.x < c.x;
				} else if (normals[idx].z == 1) {
					uZero = pos.x < c.x;
					vZero = pos.y > c.y;
				} else if (normals[idx].z == -1) {
					uZero = pos.x > c.x;
					vZero = pos.y > c.y;
				}
				vertices.add(new Vertex(pos, normals[idx], new Vector2f(uZero ? 0f : 1f, vZero ? 0f : 1f)));
			}
			polygons[idx] = new Polygon(vertices.toArray(new Vertex[0]), null);
		}
		this.setPolygons(polygons);
	}
}
