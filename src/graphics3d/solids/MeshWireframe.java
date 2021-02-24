package graphics3d.solids;

import java.util.ArrayList;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;

public class MeshWireframe implements Solid {
	
	
	private Line[] lines;
	
	
	private MeshWireframe(Mesh m) {
		
		ArrayList<Line> list = new ArrayList<Line>();
		
		for (int i = 0; i < m.faceCount(); i++) {
			for (int j = 0; j < 3; j++) {
				Line l = Line.pq(
						m.face(i).vertex( j			).pos(), 
						m.face(i).vertex((j + 1) % 3).pos());
				
				if (!list.contains(l)) list.add(l);
			}
		}
		
		this.lines = new Line[list.size()];
		
		if (list.size() != 0)
			for (int i = 0; i < list.size(); i++)
				lines[i] = list.get(i);
	}
	
	
	public static MeshWireframe m(Mesh m) {
		return new MeshWireframe(m);
	}
	

	@Override
	public Hit[] hits(Ray ray) {
		
		ArrayList<Hit> hits = new ArrayList<Hit>();
		
		for (Line l : lines) {
			
			Hit[] tmp = l.hits(ray);
			
			if (tmp.length == 0) continue;
			
			hits.add(tmp[0]);
			hits.add(tmp[1]);
		}
		
		if (hits.size() == 0) return Solid.NO_HITS;
		
		hits.sort((h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		Hit[] out = new Hit[hits.size()];
		for (int i = 0; i < out.length; i++) out[i] = hits.get(i);
		
		return out;
	}
}
