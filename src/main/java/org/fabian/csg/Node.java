package org.fabian.csg;

import java.util.ArrayList;

import com.jme3.math.Vector3f;

public class Node {
	private Plane plane;
	private Node front;
	private Node back;
	private ArrayList<Polygon> polygons;
	public Node(Polygon[] polygons) {
		this.plane = null;
		this.front = null;
		this.back = null;
		this.polygons = new ArrayList<Polygon>();
		if (polygons != null) {
			build(polygons);
		}
	}
	public ArrayList<Polygon> clipPolygons(ArrayList<Polygon> polygons) {
		if (plane == null) return new ArrayList<Polygon>(polygons);
		ArrayList<Polygon> front = new ArrayList<Polygon>();
		ArrayList<Polygon> back = new ArrayList<Polygon>();
		for (int i = 0; i < polygons.size(); i++) {
			this.plane.splitPolygon(polygons.get(i), front, back, front, back);
		}
		if (this.front != null) front = this.front.clipPolygons(front);
		if (this.back != null) back = this.back.clipPolygons(back);
		else back = new ArrayList<Polygon>();
		front.addAll(back);
		return front;
	}
	public Polygon[] allPolygons() {
		ArrayList<Polygon> polys = new ArrayList<Polygon>(polygons);
		if (front != null) for (Polygon p : front.allPolygons()) polys.add(p);
		if (back != null) for (Polygon p : back.allPolygons()) polys.add(p);
		return polys.toArray(new Polygon[0]);
	}
	public void clipTo(Node bsp) {
		this.polygons = bsp.clipPolygons(this.polygons);
		if (front != null) front.clipTo(bsp);
		if (back != null) back.clipTo(bsp);
	}
	public void invert() {
		for (Polygon p : polygons)
			p.flip();
		plane.flip();
		if (front != null) front.invert();
		if (back != null) back.invert();
		Node temp = this.front;
		this.front = this.back;
		this.back = temp;
	}
	public void build(Polygon[] polygons) {
		if (polygons.length == 0) return;
		if (plane == null) plane = polygons[0].getPlane().clone();
		ArrayList<Polygon> front = new ArrayList<Polygon>();
		ArrayList<Polygon> back = new ArrayList<Polygon>();
		for (int i = 0; i < polygons.length; i++)
			plane.splitPolygon(polygons[i], this.polygons, this.polygons, front, back);
		if (!front.isEmpty()) {
			if (this.front == null) this.front = new Node(null);
			this.front.build(front.toArray(new Polygon[0]));
		}
		if (!back.isEmpty()) {
			if (this.back == null) this.back = new Node(null);
			this.back.build(back.toArray(new Polygon[0]));
		}
	}

}
