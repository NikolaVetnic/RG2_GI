package graphics3d.solids.voxelworld.d;

public class OctreeRef {
	
	
	/*
	 * Referential implementation of octree data structure using Nodes.
	 */
	
	
	public static final int MAX_NODES = 8;

	
	class Node {
		
		boolean value;		
		Node[] nodes;
		
		public Node() {
			this.value = false;
			this.nodes = new Node[MAX_NODES];
		}
	}
	
	
	private Node root;
	private final int treeDepth;
	
	
	private OctreeRef(int treeDepth) {
		
		this.root = new Node();
		this.treeDepth = treeDepth;
		
		addDepth(root, treeDepth);
	}


	private void addDepth(Node node, int n) {
		
		if (n == 0) {
			return;
		} else {
			for (int i = 0; i < MAX_NODES; i++) {
				node.nodes[i] = new Node();
				addDepth(node.nodes[i], n - 1);
			}
		}
	}
	
	
	public static OctreeRef fromModel(boolean[][][] model) {
		
		OctreeRef o = new OctreeRef(treeDepthFromModel(model));
		
		o.root.value = true;													// root value is always true
		
		for (int i = 0; i < model.length; i++) {
			byte[] xx = intToBitArray(i, o.treeDepth);							// convert current X value to byte array of treeDepth length
			
			for (int j = 0; j < model.length; j++) {
				byte[] yy = intToBitArray(j, o.treeDepth);						// convert current Y value...
				
				for (int k = 0; k < model.length; k++) {
					if (model[i][j][k]) {
						
						byte[] zz = intToBitArray(k, o.treeDepth);				// convert current Z value...
						Node curr = o.root;										// starting from the root...
						
						for (int l = 0; l < o.treeDepth; l++) {
							
							int idx = (zz[l] << 2) + (yy[l] << 1) + xx[l];		// index of the next node is calculated from current bits of XYZ values
							
							curr.nodes[idx].value = true;						// target node's value is set to true
							curr = curr.nodes[idx];								// target node becomes the current node
						}
					}
				}
			}
		}
		
		return o;
	}
	
	
	private static int treeDepthFromModel(boolean[][][] model) {
		return (int) (Math.log(model.length) / Math.log(2)) + 1;
	}
	
	
	private static byte[] intToBitArray(int n, int treeDepth) {
		
		byte[] arr = new byte[treeDepth];
		int i = treeDepth - 1;
		
		while (i >= 0 && n > 0) {
			
			arr[i] = (byte) (n & 1);
			n = n >> 1;
							
			i--;
		}
		
		return arr;
	}
}
