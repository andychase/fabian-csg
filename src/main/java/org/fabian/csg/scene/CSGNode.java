package org.fabian.csg.scene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.fabian.csg.CSG;
import org.fabian.csg.Polygon;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Geometry;

public class CSGNode extends Geometry {
	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule capsule = ex.getCapsule(this);
		capsule.writeSavableArrayList(brushes, "Brushes", brushes);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule in = im.getCapsule(this);
		brushes = in.readSavableArrayList("Brushes", new ArrayList<CSG>());
	}
	private ArrayList<CSG> brushes = new ArrayList<CSG>();
	public CSGNode() {
		this("CSG Geometry");
	}
	
	public CSGNode(String name) {
		super(name);
	}
	
	public void addBrush(CSG brush) {
		brushes.add(brush);
	}
	
	public ArrayList<CSG> getBrushes() {
		return brushes;
	}
	
	public void removeBrush(CSG brush) {
		brushes.remove(brush);
	}
	
	public boolean hasBrush(CSG brush) {
		return brushes.contains(brush);
	}
	public void regenerate() {
		ArrayList<CSG> tempBrushes = new ArrayList<>(brushes);
		Collections.sort(tempBrushes);
		CSG product = CSG.fromPolygons(new Polygon[0]);
		for (CSG brush : tempBrushes) {
			switch (brush.getType()) {
			case ADDITIVE:
				product = product.union(brush);
				break;
			case SUBTRACTIVE:
				product = product.subtract(brush);
				break;
			case INTERSECTION:
				product = product.intersect(brush);
				break;
			}
		}
		this.setMesh(product.toMesh());
	}
}
