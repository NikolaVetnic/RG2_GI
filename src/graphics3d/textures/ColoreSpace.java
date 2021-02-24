package graphics3d.textures;

import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Texture;
import mars.geometry.Vector;

public class ColoreSpace implements Texture {
	@Override
	public Material materialAt(Vector uv) {
		return Material.diffuse(Color.rgb(uv.x(), uv.y(), 1 - uv.x() - uv.y()));
	}
}
