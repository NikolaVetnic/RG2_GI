package graphics3d.textures;

import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Texture;
import javafx.scene.image.Image;
import javafx.scene.image.WritablePixelFormat;
import mars.geometry.Box;
import mars.geometry.Vector;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;


public class ImageTexture implements Texture {
	
	private final Material[][] matrix;
	private final Vector size;
	
	
	
	public ImageTexture(Image img) {
		size = mars.graphics.javafx.Graphics.imageSize(img);
		matrix = new Material[size.yInt()][size.xInt()];
		
		int[] buffer = new int[size.areaInt()];
		
		img.getPixelReader().getPixels(
				0, 0,
				size.xInt(), size.yInt(),
				WritablePixelFormat.getIntArgbPreInstance(),
				buffer,
				0, size.xInt()
		);
		
		IntStream.range(0, size.yInt()).parallel().forEach(y -> {
			for (int x = 0; x < size.xInt(); x++) {
				int k = y * size.xInt() + x;
				Color c = Color.code(buffer[k]);
				matrix[y][x] = Material.diffuse(c);
			}
		});
	}
	
	
	public ImageTexture(InputStream is) {
		this(new Image(is));
	}
	
	
	public static ImageTexture fromFile(String fileName) {
		try {
			return new ImageTexture(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	@Override
	public Material materialAt(Vector uv) {
		Vector p = uv.mod(Box.UNIT).mul(size);
		return matrix[p.yInt()][p.xInt()];
	}
}
