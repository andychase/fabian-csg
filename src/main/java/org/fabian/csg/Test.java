package org.fabian.csg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.fabian.csg.shapes.CubeBrush;
import org.fabian.csg.shapes.SphereBrush;

import com.jme3.math.Vector3f;

public class Test {

	public static void main(String[] args) throws Exception {
		CSG a = new CubeBrush(new Vector3f(-0.25f, -0.25f, -0.25f), new Vector3f(1f, 1f, 1f));
		CSG b = new SphereBrush(new Vector3f(0.25f, 0.25f, 0.25f), 1.3f, 16, 8);
		CSG boxOut = a.subtract(b);
		// Now let's get our polygons
		Polygon[] polys = boxOut.getPolygons();
		System.out.println("No. Polygons: "+polys.length);
		// Let's output it to obj!
		BufferedWriter bw = new BufferedWriter(new FileWriter("CSG.obj"));
		for (int p = 0; p < polys.length; p++) {
			if (polys[p].getVertices().length > 3) {
				System.out.println("Poly "+p+" length: "+polys[p].getVertices().length);
			}
		}
		for (int p = 0; p < polys.length; p++) {
			for (int v = 0; v < polys[p].getVertices().length; v++) {
				Vertex vert = polys[p].getVertices()[v];
				bw.write("v "+vert.getPosition().x+" "+vert.getPosition().y+" "+vert.getPosition().z);
				bw.newLine();
				bw.write("vn "+vert.getNormal().x+" "+vert.getNormal().y+" "+vert.getNormal().z);
				bw.newLine();
			}
		}
		int vertexPointer = 1;
		for (int p = 0; p < polys.length; p++) {
			ArrayList<Integer> idx = new ArrayList<Integer>();
			for (int v = 0; v < polys[p].getVertices().length; v++) {
				idx.add(vertexPointer++);
			}
			int ptr0 = idx.get(0);
			for (int v = 2; v < polys[p].getVertices().length; v++) {
				int ptr1 = idx.get(v-1);
				int ptr2 = idx.get(v);
				bw.write("f "+ptr0+"//"+ptr0+" "+(ptr1)+"//"+(ptr1)+" "+ptr2+"//"+ptr2);
				bw.newLine();
			}
			/*
			bw.write("f ");
			int startPtr = vertexPointer;
			for (int v = 2; v < polys[p].getVertices().length; v++) {
				bw.write(vertexPointer+"//"+vertexPointer+" ");
				vertexPointer++;
			}
			bw.newLine();*/
		}
		bw.flush();
		bw.close();
	}

}
