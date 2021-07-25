package graphics3d.solids.voxelworld;

import graphics3d.Color;

public class ModelData {
	
	
	/*
	 * A convenient data structure for grouping boolean[][][][] and Co-
	 * lor[][][] arrays in transit. To be expanded as more materials a-
	 * re added.
	 */

	
	private boolean[][][][] model;
	private Color[][][] diffuse;
	
	
	private ModelData(boolean[][][][] model, Color[][][] diffuse) {
		this.model = model;
		this.diffuse = diffuse;
	}
	
	
	public static ModelData arr(boolean[][][][] model, Color[][][] diffuse) {
		return new ModelData(model, diffuse);
	}


	public boolean[][][][] model() 	{ return model; 	}
	public Color[][][] diffuse() 	{ return diffuse; 	}
}
