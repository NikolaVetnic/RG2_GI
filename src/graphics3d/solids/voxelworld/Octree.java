package graphics3d.solids.voxelworld;

public class Octree {
	
	
	/*
	 * Implementation of octree data structure using an array of 3D bo-
	 * olean arrays representing subsequent levels of the tree.
	 */

	
	public static final int MAX_NODES = 8;
	
	
	private final boolean[][][][] data;
	private final int treeDepth;
	
	
	private Octree(int treeDepth) {
		
		this.treeDepth = treeDepth;
		
		data = new boolean[treeDepth][][][];
		
		for (int i = 0; i < treeDepth; i++) {
			int dim = (int) Math.pow(2, treeDepth - i - 1);							// data[0] is lowest level cube, i.e. the model itself
			data[i] = new boolean[dim][dim][dim];									// subsequent cubes are of dim one less power of two
		}
	}
	
	
	private static int treeDepthFromModel(boolean[][][] model) {
		return (int) (Math.log(model.length) / Math.log(2)) + 1;					// calculate octree depth from dim of given model
	}
	
	
	public static Octree fromModel(boolean[][][] model) {
		
        Octree o = new Octree(treeDepthFromModel(model));
        
        o.copyModel(model);															// data[0] is filled first by copying the model

        for (int l = 0; l < o.treeDepth - 1; l++) {									// for each subsequent cube [l + 1] previous cube is observed...
            for (int i = 0; i < o.data[l].length; i++) {							// ...hence the loop goes to penultimate array element
                for (int j = 0; j < o.data[l].length; j++) {						// a group of eight voxels in curr cube are used to determine one voxel in subsequent one
                    for (int k = 0; k < o.data[l].length; k++) {					// |= is used in order to make the subsequent cube's voxel true...
                        o.data[l + 1][i / 2][j / 2][k / 2] |= o.data[l][i][j][k];	// ...even if only one of the current cube's eight is true
                    }
                }
            }
        }
        
        return o;
    }
	
	
	private void copyModel(boolean[][][] model) {
		
		for (int i = 0; i < model.length; i++) {
			for (int j = 0; j < model.length; j++) {
				for (int k = 0; k < model.length; k++) {
					data[0][i][j][k] = model[i][j][k];
				}
			}
		}
	}
	
	
	public boolean[][][][] data() { return data; }
	public int treeDepth() { return treeDepth; }
}
