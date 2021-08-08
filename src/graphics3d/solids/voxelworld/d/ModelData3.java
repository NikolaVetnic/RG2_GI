package graphics3d.solids.voxelworld.d;

import graphics3d.Color;

public class ModelData3 {


	/*
	 * A convenient data structure for grouping boolean[][][] and Color
	 * [][][] arrays in transit. To be expanded as materials are added.
	 */


	private boolean[][][] model;
	private Color[][][] diffuse;


	private ModelData3(boolean[][][] model, Color[][][] diffuse) {
		this.model = model;
		this.diffuse = diffuse;
	}
	
	
	public static ModelData3 arr(boolean[][][] model, Color[][][] diffuse) {
		return new ModelData3(model, diffuse);
	}


	public boolean[][][] model() 	{ return model; 	}
	public Color[][][] diffuse() 	{ return diffuse; 	}
}
