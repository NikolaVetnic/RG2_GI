package graphics3d.solids.voxelworld;

import graphics3d.Color;

public class ModelData4 {
	
	
	/*
	 * A convenient data structure for grouping boolean[][][][] and Co-
	 * lor[][][] arrays in transit. To be expanded as more materials a-
	 * re added.
	 */

	
	private boolean[][][][] model;
	private Color[][][] diffuse;
	
	
	private ModelData4(boolean[][][][] model, Color[][][] diffuse) {
		this.model = model;
		this.diffuse = diffuse;
	}
	
	
	public static ModelData4 arr(boolean[][][][] model, Color[][][] diffuse) {
		return new ModelData4(model, diffuse);
	}


	public boolean[][][][] model() 	{ return model; 	}
	public Color[][][] diffuse() 	{ return diffuse; 	}
}
