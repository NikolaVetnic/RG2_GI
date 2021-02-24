package graphics3d.textures;

import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Texture;
import mars.geometry.Vector;


public class Grid implements Texture {
	
	private final Vector size, sizeLine;
	private final Material material, materialLine;
	
	// transient
	private final Vector sizeLineHalf;
	
	
	
	public Grid(Vector size, Vector sizeLine, Material material, Material materialLine) {
		this.size = size;
		this.sizeLine = sizeLine;
		this.material = material;
		this.materialLine = materialLine;
		sizeLineHalf = sizeLine.div(2);
	}
	
	
	@Override
	public Material materialAt(Vector uv) {
		Vector p = uv.add(sizeLineHalf).mod(size);
		return (p.x() < sizeLine.x()) || (p.y() < sizeLine.y()) ? materialLine : material;
	}
	
	
	public static Grid standard(Color color) {
		return new Grid(
				Vector.xy(0.25, 0.25),
				Vector.xy(0.01, 0.01),
				Material.diffuse(color),
				Material.diffuse(color.mul(0.75))
		);
	}

	public static Grid standardUnit(Color color) {
		return new Grid(
				Vector.UNIT_DIAGONAL,
				Vector.xy(1.0/64),
				Material.diffuse(color),
				Material.diffuse(color.mul(0.75))
		);
	}
}
