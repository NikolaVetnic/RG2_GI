package graphics3d.renderers;

import graphics3d.*;
import graphics3d.cameras.ThinLens;
import graphics3d.colliders.BruteForce;
import graphics3d.colliders.BruteForceFirstHit;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;
import mars.utils.Hashing;

import java.util.stream.IntStream;

public abstract class RaytracingBase implements Renderer {
	
	protected final Scene scene;
	private final Vector imageSize;
	
	protected final Collider collider;
	private final Camera camera;
	
	private final Color[][] pixelColorTotals;
	private final Sampler[] samplers;
	
	private int renderIterations = 0;
	
	
	
	@Override
	public boolean goodEnough() {
		return renderIterations() >= 360;
	}
	
	
	public RaytracingBase(Scene scene, Vector imageSize, double focalDistance, double focalBlurR) {
		this.scene = scene;
		this.imageSize = imageSize;

//		collider = new BruteForce(scene.bodies());
		collider = new BruteForceFirstHit(scene.bodies());
		camera = new ThinLens(focalDistance, focalBlurR);

		samplers = new Sampler[imageSize.yInt()];
		
		pixelColorTotals = new Color[imageSize.yInt()][imageSize.xInt()];
		IntStream.range(0, imageSize.yInt()).parallel().forEach(y -> {
			for (int x = 0; x < imageSize.xInt(); x++) {
				pixelColorTotals[y][x] = Color.BLACK;
			}

			samplers[y] = new Sampler(Hashing.mix64(y));
		});
	}
	
	
	@Override
	public Vector imageSize() {
		return imageSize;
	}
	
	
	/**
	 * Returns the color sampled by tracing the ray r. The ray r should be normalized.
	 */
	protected abstract Color radiance(Ray ray, Sampler sampler);
	
	
	@Override
	public void renderIteration(Color[][] pixelColors) {
		double m = imageSize().min();
		renderIterations++;
		
		IntStream.range(0, imageSize().yInt()).parallel().forEach(y -> {
			Sampler sampler = samplers[y];

			for (int x = 0; x < imageSize().xInt(); x++) {
				Vector pixel = Vector.xy(x, y);
				Vector pixelS = pixel.add(sampler.randomInBox()).sub(imageSize().div(2)).div(m);
				Ray r = camera.sampleExitingRay(pixelS);
				Ray rCamera = scene.cameraTransform().applyTo(r);
				Color sample = radiance(rCamera, sampler);
				pixelColorTotals[y][x] = pixelColorTotals[y][x].add(sample);
				pixelColors[y][x] = pixelColorTotals[y][x].div(renderIterations);
			}
		});
	}
	
	public int renderIterations() {
		return renderIterations;
	}
}
