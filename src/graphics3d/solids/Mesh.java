
package graphics3d.solids;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Mesh implements Solid {
	
	
	private final String name;
	private final Face[] faces;
	
	
	public Vec3 center;
	
	
	protected static class Vertex {
		
		private final Vec3 		vp;
		private final Vector 	vt;
		private final Vec3 		vn;
		
		private Vertex(Vec3 vp, Vector vt, Vec3 vn) {
			this.vp = vp;
			this.vt = vt;
			this.vn = vn;
		}

		public static Vertex v(Vec3 vp) {
			return new Vertex(vp, null, null);
		}
		
		public static Vertex vt(Vec3 vp, Vector vt) {
			return new Vertex(vp, vt, null);
		}
		
		public static Vertex vtn(Vec3 vp, Vector vt, Vec3 vn) {
			return new Vertex(vp, vt, vn);
		}
		
		public String toString() {
			return "v : " + vp + " / vt : " + vt + " / vn : " + vn;
		}
		
		public Vec3 	pos() 	{ return vp; 	}
		public Vector 	tex() 	{ return vt; 	}
		public Vec3 	nrm() 	{ return vn; 	}
	}
	
	
	protected static class Face {
		
		private final Vertex v0, v1, v2;
		
		private Face(Vertex v0, Vertex v1, Vertex v2) {
			this.v0 = v0;
			this.v1 = v1;
			this.v2 = v2;
		}
		
		public String toString() {
			return 	"VERT_0 : [" + v0 + "] " + 
					"VERT_1 : [" + v1 + "] " +
					"VERT_2 : [" + v2 + "] " ;
		}
		
		public Vertex vertex(int i)	{ 
			return switch (i) {
				case 0 -> v0;
				case 1 -> v1;
				case 2 -> v2;
				default -> throw new IllegalArgumentException();
			};
		}
		
		public Vertex[] vertices() {
			return new Vertex[] { v0, v1, v2 };
		}
		
		public Vec3[] pos() {
			return new Vec3[] { v0.vp, v1.vp, v2.vp }; 	
		} 
	}
	
	
	private Mesh(String input, String name, String mode) throws IOException {
		
		this.name = name;
		
		BufferedReader br = new BufferedReader(
				new FileReader("mesh//" + input + ".obj"));
		
		if 		("vtn".equals(mode))	// mode : vertices + textures + normals
			this.faces = loadFaces(br, loadVertPos(br, true), loadVertNrm(br), loadVertTex(br));
		else if ("vt".equals(mode))		// mode : vertices + textures
			this.faces = loadFaces(br, loadVertPos(br, true), null, 		   loadVertTex(br));
		else							// mode : vertices
			this.faces = loadFaces(br, loadVertPos(br, true), null, 		   null			  );
		
		br.close();
	}
		
	public static Mesh loadVTN(String input, String name) throws IOException {
		return new Mesh(input, name, "vtn");
	}
		
	public static Mesh loadVTN(String input) throws IOException {
		return new Mesh(input, input, "vtn");
	}
	
	public static Mesh loadVT(String input, String name) throws IOException {
		return new Mesh(input, name, "vt");
	}
	
	public static Mesh loadVT(String input) throws IOException {
		return new Mesh(input, input, "vt");
	}
	
	public static Mesh loadV(String input, String name) throws IOException {
		return new Mesh(input, name, "v");
	}
	
	public static Mesh loadV(String input) throws IOException {
		return new Mesh(input, input, "v");
	}

	private Face[] loadFaces(BufferedReader br, Vec3[] vPos, Vec3[] vNrm, Vector[] vTex) throws NumberFormatException, IOException {
		
		String line;
		
		// loading faces, face UV points and face normals
		ArrayList<Integer[]> load3 = new ArrayList<>();
		ArrayList<Integer[]> load4 = new ArrayList<>();
		ArrayList<Integer[]> load5 = new ArrayList<>();
		
		while ((line = br.readLine()) != null && line.length() > 5) {
			
			String[] tokens = line.split("\\s+");
			
			String[] p0 = tokens[1].split("/");
			String[] p1 = tokens[2].split("/");
			String[] p2 = tokens[3].split("/");
			
			int v1 = Integer.parseInt(p0[0].trim()) - 1;	// .obj format counts verts starting from 1
			int v2 = Integer.parseInt(p1[0].trim()) - 1;
			int v3 = Integer.parseInt(p2[0].trim()) - 1;
			
			load3.add(new Integer[] { v1, v2, v3 } );

			if (vTex != null) {
				int t1 = Integer.parseInt(p0[1].trim()) - 1;
				int t2 = Integer.parseInt(p1[1].trim()) - 1;
				int t3 = Integer.parseInt(p2[1].trim()) - 1;
				
				load4.add(new Integer[] { t1, t2, t3 } );
			}
			
			if (vNrm != null) {
				int n1 = Integer.parseInt(p0[2].trim()) - 1;
				int n2 = Integer.parseInt(p1[2].trim()) - 1;
				int n3 = Integer.parseInt(p2[2].trim()) - 1;

				load5.add(new Integer[] { n1, n2, n3 } );
			}
		}
		
		Face[] out = new Face[load3.size()];
		
		for (int i = 0; i < out.length; i++)
			if (vNrm != null && vTex != null)
				out[i] = new Face(
						Vertex.vtn(vPos[load3.get(i)[0]], vTex[load4.get(i)[0]], vNrm[load5.get(i)[0]]), 
						Vertex.vtn(vPos[load3.get(i)[1]], vTex[load4.get(i)[1]], vNrm[load5.get(i)[1]]), 
						Vertex.vtn(vPos[load3.get(i)[2]], vTex[load4.get(i)[2]], vNrm[load5.get(i)[2]]));
			else if (vTex != null)
				out[i] = new Face(
						Vertex.vt(vPos[load3.get(i)[0]], vTex[load4.get(i)[0]]), 
						Vertex.vt(vPos[load3.get(i)[1]], vTex[load4.get(i)[1]]), 
						Vertex.vt(vPos[load3.get(i)[2]], vTex[load4.get(i)[2]]));
			else
				out[i] = new Face(
						Vertex.v(vPos[load3.get(i)[0]]), 
						Vertex.v(vPos[load3.get(i)[1]]), 
						Vertex.v(vPos[load3.get(i)[2]]));
		
		return out;
	}
	
	
	private Vec3[] loadVertPos(BufferedReader br, boolean centerAtOrigin) throws NumberFormatException, IOException {
		
		/*
		 * Reads vertices from .obj file and loads them in Vec3 array, whi-
		 * ch is then passed as output. 
		 */ 
		
		String line;
		
		ArrayList<Vec3> load0 = new ArrayList<>();

		while ((line = br.readLine()).length() > 5) {

			String[] tokens = line.split("\\s+");		// splits regardless of the amount of whitespace between tokens
			
			load0.add(Vec3.xyz(
					Double.parseDouble(tokens[1].trim()), 
					Double.parseDouble(tokens[2].trim()),
					Double.parseDouble(tokens[3].trim())));
		};
		
		if (centerAtOrigin)
			load0 = centerAtZero(load0);				// calculates center of mass and subtracts it from all vertices
		
		Vec3[] out = new Vec3[load0.size()];
		for (int i = 0; i < load0.size(); i++) out[i] = load0.get(i);
		
		return out;
	}
		
	
	private Vec3[] loadVertNrm(BufferedReader br) throws NumberFormatException, IOException {
		
		/*
		 * Reads vertex normals from .obj file and loads them in Vec3 array
		 * and returned as output. 
		 */ 
		
		String line;
		
		ArrayList<Vec3> load1 = new ArrayList<>();
		
		while ((line = br.readLine()).length() > 5) {
			
			String[] tokens = line.split("\\s+");		// splits regardless of the amount of whitespace between tokens
			
			load1.add(Vec3.xyz(
					Double.parseDouble(tokens[1].trim()), 
					Double.parseDouble(tokens[2].trim()),
					Double.parseDouble(tokens[3].trim())));
		};
		
		Vec3[] out = new Vec3[load1.size()];
		for (int i = 0; i < load1.size(); i++) out[i] = load1.get(i);
		
		return out;
	}
	

	private Vector[] loadVertTex(BufferedReader br) throws NumberFormatException, IOException {
		
		/*
		 * Reads vertex texture coordinates from .obj file, loads them in a
		 * Vec3 array and passes it as output.
		 */ 
		
		ArrayList<Vector> load2 = new ArrayList<>();

		String line;
		
		while ((line = br.readLine()).length() > 5) {

			String[] tokens = line.split("\\s+");
			
			load2.add(Vector.xy(
					Double.parseDouble(tokens[1].trim()), 
					Double.parseDouble(tokens[2].trim())));
		};

		Vector[] out = new Vector[load2.size()];
		for (int i = 0; i < load2.size(); i++) out[i] = load2.get(i);
		
		return out;
	}
	
	
	private static ArrayList<Vec3> centerAtZero(ArrayList<Vec3> verts) {
		
		Vec3 sum = Vec3.ZERO;
		for (Vec3 v : verts) sum = sum.add(v);
		
		ArrayList<Vec3> out = new ArrayList<>();
		
		Vec3 c = sum.div(verts.size());
		for (Vec3 v : verts) out.add(v.sub(c));
		
		return out;
	}
	
		
	public String name()					{ return name;					}
	public Face face(int i)					{ return faces[i];				}
	public int faceCount()					{ return faces.length;			}
	public Vertex vertex(int f, int v)		{ return faces[f].vertex(v);	}
	public Vertex vertex(Face f, int v)		{ return f.vertex(v);			}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		
		ArrayList<Hit> hits = new ArrayList<>();

		for (Face f : faces) {
			
			Hit[] tmpHits;

			Triangle tmp = Triangle.pqr(f.pos());
			
			if ((tmpHits = tmp.hits(ray)).length != 0) {
				
				double t = tmpHits[0].t() != Double.NEGATIVE_INFINITY ? tmpHits[0].t() : tmpHits[1].t();
				
				double v = tmp.uv(ray.at(t)).x();
				double w = tmp.uv(ray.at(t)).y();
				double u = 1.0 - tmp.uv(ray.at(t)).x() - tmp.uv(ray.at(t)).y();
				
				Vec3 norm = null;
				
				if (f.vertex(0).nrm() != null)
					norm = f.vertex(0).nrm().mul(u).add(f.vertex(1).nrm().mul(v)).add(f.vertex(2).nrm().mul(w));
				else
					norm = tmp.n_();
				
				// yes smootihng
				hits.add(HitData.tn(tmpHits[0].t(), norm));
				hits.add(HitData.tn(tmpHits[1].t(), norm));
				
				// no smoothing
//				hits.add(tmpHits[0]);
//				hits.add(tmpHits[1]);
			}
		}
		
		if (hits.size() == 0) return Solid.NO_HITS;
		
		hits.sort((h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		Hit[] out = new Hit[hits.size()];
		for (int i = 0; i < out.length; i++) out[i] = hits.get(i);
		
		return out;
	}
	
	
	private boolean isPointOnPlane(Vec3 p, Vec3[] triangle) {
		
		// Naravno, vektorski možeš rešiti lako
		// uzmeš vektor AP i uradiš skalarni proizvod sa vektorom AB x AC
		// To je proporcionalno dužini projekcije AP na normalu na ravan
		
		double x1 = triangle[0].x();
		double y1 = triangle[0].y();
		double z1 = triangle[0].z();
		
		double x2 = triangle[1].x();
		double y2 = triangle[1].y();
		double z2 = triangle[1].z();
		
		double x3 = triangle[2].x();
		double y3 = triangle[2].y();
		double z3 = triangle[2].z();
		
		double x4 = p.x();
		double y4 = p.y();
		double z4 = p.z();
		
		double r = 	x4*y3*z2 + x2*y4*z3 + x3*y2*z4 - x2*y3*z4 - x3*y4*z2 - x4*y2*z3 +
					x1*y3*z4 + x3*y4*z1 + x4*y1*z3 - x4*y3*z1 - x1*y4*z3 - x3*y1*z4 -
					x1*y2*z4 - x2*y4*z1 - x4*y1*z2 + x4*y2*z1 + x1*y4*z2 + x2*y1*z4 +
					x1*y2*z3 + x2*y3*z1 + x3*y1*z2 - x3*y2*z1 - x1*y3*z2 - x2*y1*z3 ;
		
		return Math.abs(r) < 1e-10;
	}
	
	
	public Vec3 normalAtPoint_(Vec3 p) {
		
		for (int i = 0; i < faces.length; i++) {
			if (isPointOnPlane(p, faces[i].pos())) {
				return Triangle.pqr(faces[i].pos()).n_();
			}
		}
		
		return null;
	}
	
	
//	@Override
//	public Vector uv(Vec3 p) {
//		
//		double u = 0, v = 0, w = 0;
//		
//		for (int i = 0; i < faces.length; i++) {
//			if (isPointOnPlane(p, faces[i].pos())) {
//				
//				Vec3 	v0 = faces[i].vertex(0).pos(),
//						v1 = faces[i].vertex(1).pos(),
//						v2 = faces[i].vertex(2).pos();
//				
//				double t = v1.sub(v0).cross(v2.sub(v0)).length();
//				
//				u = v2.sub(v0).cross(p.sub(v0)).length() / t;
//				v = v0.sub(v1).cross(p.sub(v1)).length() / t;
//				w = v1.sub(v2).cross(p.sub(v2)).length() / t;
//				
//				if (0 <= u && u <= 1 &&
//					0 <= v && v <= 1 &&
//					0 <= w && w <= 1 &&
//					Math.abs(1 - u - v - w) <= 1e-10) {
//					
//					Vector vt0 = faces[i].vertex(0).tex().mul(u);
//					Vector vt1 = faces[i].vertex(1).tex().mul(v);
//					Vector vt2 = faces[i].vertex(2).tex().mul(w);
//					
//					return vt0.add(vt1).add(vt2);
//				}
//			}
//		}
//		
//		return Vector.ZERO;
//	}
	

	public void print() {
		
		if (name != null)
			System.out.println("MODEL : " + name);
		
		for (int i = 0; i < faceCount(); i++) {
			
			System.out.println("FACE #" + i + " : ");
			
			for (Vertex v : faces[i].vertices())
				System.out.println("\t" + v);
		}
	}
}
