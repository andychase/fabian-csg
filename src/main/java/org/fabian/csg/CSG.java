package org.fabian.csg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

public class CSG implements Comparable<CSG>, Savable {
	public static enum BrushType {
		ADDITIVE,
		SUBTRACTIVE,
		INTERSECTION;
	}
	private BrushType type = BrushType.ADDITIVE;
	public BrushType getType() {
		return type;
	}
	public void setType(BrushType type) {
		this.type = type;
	}
	private int order = 0;
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public Mesh toMesh() {
		Mesh m = new Mesh();
		ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector2f> uv = new ArrayList<Vector2f>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		Polygon[] polygons = getPolygons();
		int indexPtr = 0;
		for (int i = 0; i < polygons.length; i++) {
			Polygon p = polygons[i];
			ArrayList<Integer> vertexPointers = new ArrayList<Integer>();
			for (Vertex v : p.getVertices()) {
				positions.add(v.getPosition());
				normals.add(v.getNormal());
				uv.add(v.getUv());
				vertexPointers.add(indexPtr++);
			}
			for (int ptr = 2; ptr < p.getVertices().length; ptr++) {
				indices.add(vertexPointers.get(0));
				indices.add(vertexPointers.get(ptr-1));
				indices.add(vertexPointers.get(ptr));
			}
		}
		Vector3f[] positionArray = positions.toArray(new Vector3f[0]);
		Vector3f[] normalArray = normals.toArray(new Vector3f[0]);
		Vector2f[] uvArray = uv.toArray(new Vector2f[0]);
		int[] indicesIntArray = new int[indices.size()];
		for (int i = 0; i < indices.size(); i++) {
			indicesIntArray[i] = indices.get(i);
		}
		m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positionArray));
		m.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normalArray));
		m.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indicesIntArray));
		m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvArray));
		m.updateBound();
		m.updateCounts();
		return m;
	}
	protected CSG() {
		// Go away! No public constructors here!
	}
	private Polygon[] polygons;
	public Polygon[] getPolygons() {
		return polygons;
	}
	public void setPolygons(Polygon[] polygons) {
		this.polygons = polygons;
	}
	public static CSG fromPolygons(Polygon[] polygons) {
		CSG csg = new CSG();
		csg.setPolygons(polygons);
		return csg;
	}
	public CSG clone() {
		CSG csg = new CSG();
		Polygon[] newPolys = new Polygon[polygons.length];
		for (int i = 0; i < newPolys.length; i++)
			newPolys[i] = polygons[i].clone();
		csg.setPolygons(newPolys);
		return csg;
	}
	public Polygon[] toPolygons() {
		return getPolygons(); // Alias function so I can be lazy.
	}
	
	public CSG union(CSG csg) {
		Node a = new Node(clone().polygons);
		Node b = new Node(csg.clone().polygons);
		a.clipTo(b);
		b.clipTo(a);
		b.invert();
		b.clipTo(a);
		b.invert();
		a.build(b.allPolygons());
		return CSG.fromPolygons(a.allPolygons());
	}
	
	public CSG subtract(CSG csg) {
		Node a = new Node(clone().polygons);
		Node b = new Node(csg.clone().polygons);
		a.invert();
		a.clipTo(b);
		b.clipTo(a);
		b.invert();
		b.clipTo(a);
		b.invert();
		a.build(b.allPolygons());
		a.invert();
		return CSG.fromPolygons(a.allPolygons());
	}
	
	public CSG intersect(CSG csg) {
		Node a = new Node(this.clone().polygons);
	    Node b = new Node(csg.clone().polygons);
	    a.invert();
	    b.clipTo(a);
	    b.invert();
	    a.clipTo(b);
	    b.clipTo(a);
	    a.build(b.allPolygons());
	    a.invert();
	    return CSG.fromPolygons(a.allPolygons());
	}
	
	public CSG inverse() {
		CSG csg = clone();
		for (Polygon p : csg.polygons)
			p.flip();
		return csg;
	}
	@Override
	public int compareTo(CSG o) {
		if (this.getOrder() == o.getOrder()) {
			// CSG should be applied inner-level as follows: Additive -> Intersection -> Subtractive
			if (this.getType() == BrushType.SUBTRACTIVE) return 2;
			if (this.getType() == BrushType.INTERSECTION) return 1;
			return 0;
		}
		return this.getOrder() - o.getOrder();
	}
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule capsule = ex.getCapsule(this);
		capsule.write(order, "Order", order);
		capsule.write(type, "Type", BrushType.ADDITIVE);
		capsule.write(polygons, "Polygons", new Polygon[0]);
	}
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule in = im.getCapsule(this);
		this.order = in.readInt("Order", 0);
		this.type = in.readEnum("Type", BrushType.class, BrushType.ADDITIVE);
		this.polygons = (Polygon[]) in.readSavableArray("Polygons", new Polygon[0]);
	}
}
