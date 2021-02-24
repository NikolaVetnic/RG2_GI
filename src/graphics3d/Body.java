package graphics3d;

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
	
}
