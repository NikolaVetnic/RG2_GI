package graphics3d.solids.voxelworld.d;

import graphics3d.Color;

public class VoxelData {

	private boolean populated;
	private Color color;
	
	public static final VoxelData UNPOPULATED = VoxelData.create(false, Color.BLACK);
	
	protected VoxelData(boolean populated, Color color) {
		this.populated = populated;
		this.color = color;
	}
	
	public static VoxelData create(boolean populated, Color color) {
		return new VoxelData(populated, color);
	}
	
	public static VoxelData create(Color color) {
		return new VoxelData(true, color);
	}
	
	public static VoxelData empty() {
		return new VoxelData(false, null);
	}

	public boolean isPopulated() 	{ return populated; }
	public Color color() 			{ return color; 	}
}
