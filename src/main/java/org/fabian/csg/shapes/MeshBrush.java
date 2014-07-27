package org.fabian.csg.shapes;

import java.nio.FloatBuffer;

import org.fabian.csg.CSG;
import org.fabian.csg.Polygon;
import org.fabian.csg.Vertex;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;

public class MeshBrush extends CSG {

	public MeshBrush(Mesh m) {
		IndexBuffer idxBuffer = m.getIndexBuffer();
		FloatBuffer posBuffer = m.getFloatBuffer(Type.Position);
		FloatBuffer normBuffer = m.getFloatBuffer(Type.Normal);
		FloatBuffer uvBuffer = m.getFloatBuffer(Type.TexCoord);
		Polygon[] polygons = new Polygon[idxBuffer.size() / 3];
		for (int i = 0; i < idxBuffer.size(); i += 3) {
			int idx1 = idxBuffer.get(i);
			int idx2 = idxBuffer.get(i + 1);
			int idx3 = idxBuffer.get(i + 2);
			Vector3f pos1 = new Vector3f(posBuffer.get(idx1 * 3), posBuffer.get((idx1 * 3) + 1), posBuffer.get((idx1 * 3) + 2));
			Vector3f pos2 = new Vector3f(posBuffer.get(idx2 * 3), posBuffer.get((idx2 * 3) + 1), posBuffer.get((idx2 * 3) + 2));
			Vector3f pos3 = new Vector3f(posBuffer.get(idx3 * 3), posBuffer.get((idx3 * 3) + 1), posBuffer.get((idx3 * 3) + 2));
			Vector3f norm1 = new Vector3f(normBuffer.get(idx1 * 3), normBuffer.get((idx1 * 3) + 1), normBuffer.get((idx1 * 3) + 2));
			Vector3f norm2 = new Vector3f(normBuffer.get(idx2 * 3), normBuffer.get((idx2 * 3) + 1), normBuffer.get((idx2 * 3) + 2));
			Vector3f norm3 = new Vector3f(normBuffer.get(idx3 * 3), normBuffer.get((idx3 * 3) + 1), normBuffer.get((idx3 * 3) + 2));
			Vector2f uv1 = new Vector2f(uvBuffer.get(idx1 * 2), uvBuffer.get((idx1 * 2) + 1));
			Vector2f uv2 = new Vector2f(uvBuffer.get(idx2 * 2), uvBuffer.get((idx2 * 2) + 1));
			Vector2f uv3 = new Vector2f(uvBuffer.get(idx3 * 2), uvBuffer.get((idx3 * 2) + 1));
			
			Vertex vertex1 = new Vertex(pos1, norm1, uv1);
			Vertex vertex2 = new Vertex(pos2, norm2, uv2);
			Vertex vertex3 = new Vertex(pos3, norm3, uv3);
			Polygon polygon = new Polygon(new Vertex[] {vertex1, vertex2, vertex3}, null);
			polygons[i / 3] = polygon;
		}
		setPolygons(polygons);
	}

}
