package graphics3d;

import mars.geometry.Vector;

public interface Body {
	
	/**
	 * Geometric solid representing the shape of the body.
	 */
	Solid solid();
	
	/**
	 * Material at the specified hit on the surface of the body.
	 */
	Material materialAt(Hit hit);
	
	
	default Body transformed(Transform transform) {
		return new Body() {
			private final Solid solid = Body.this.solid().transformed(transform);
			
			@Override
			public Solid solid() {
				return solid;
			}
			@Override
			public Material materialAt(Hit hit) {
				return Body.this.materialAt(hit);
			}
		};
	}
	
	/**
	 * Returns a body having the given material at each surface point.
	 */
	static Body uniform(Solid solid, Material material) {
		return new Body() {
			@Override
			public Solid solid() {
				return solid;
			}
			public Material materialAt(Hit hit) {
				return material;
			}
		};
	}

	
	/**
	 * Returns a body having the default material at each surface point.
	 */
	static Body uniform(Solid solid) {
		return uniform(solid, Material.DIFFUSE);
	}
	
	/**
	 * Returns a body having the specified texture.
	 */
	static Body textured(Solid solid, Texture texture) {
		return new Body() {
			@Override
			public Solid solid() {
				return solid;
			}
			@Override
			public Material materialAt(Hit hit) {
				return texture.materialAt(hit.uv());
			}
		};
	}
	
	
	static Body v(Solid solid, graphics3d.Color[][][] model) {
		return new Body() {
			@Override
			public Solid solid() {
				return solid;
			}
			
			public graphics3d.Color voxel(int i, int j, int k) {
				return model[i][j][k];
			}
			
			@Override
			public Material materialAt(Hit hit) {
				
				Vector uv = hit.uv();
				
				int p = uv.xInt(), t = uv.yInt();
				
				int q = (t >> 4) & 15;
				int r =  t 	     & 15;
				
				graphics3d.Color v = voxel(p, q, r);
				
				if (v != null)
					return Material.diffuse(v);
				else
					return Material.diffuse(graphics3d.Color.WHITE);
			}
		};
	}
}
