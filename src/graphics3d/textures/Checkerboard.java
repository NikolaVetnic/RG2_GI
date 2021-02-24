package graphics3d.textures;

import graphics3d.Material;
import graphics3d.Texture;
import mars.geometry.Vector;


public class Checkerboard implements Texture {
	
	private final Vector size;
	private final Material materialA, materialB;
	
	
	
	public Checkerboard(Vector size, Material materialA, Material materialB) {
		this.size = size;
		this.materialA = materialA;
		this.materialB = materialB;
	}
	
	
	@Override
	public Material materialAt(Vector uv) {
		Vector p = uv.div(size).floor();
		return ((p.xInt() & 1) == 0) ^ ((p.yInt() & 1) == 0) ? materialB : materialA;
	}
}
