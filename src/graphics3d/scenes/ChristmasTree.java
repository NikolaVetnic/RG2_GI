package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;
import mars.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

public class ChristmasTree extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble
		double s = 0.4;
		
		@GadgetDouble
		double phi = 0.1;
		
		@GadgetDouble
		double theta = 0;
		
		@GadgetLongRandomStream
		long seed = -7355785686003704397l;
		
		@Override
		public Scene at(Double time) {
			return new ChristmasTree(
					time,
					phi + 0.4 * (1 + Numeric.cosT(0.5+time)) / 2,
					theta + time,
					s,
					seed
			);
		}
	}
	
	
	public ChristmasTree(double time, double phi, double theta, double s, long seed) {
		Sampler sampler = new Sampler(seed);
		
		colorBackground = Color.BLACK;
		
		{ // Floor
			BSDF bsdfFloor =
					BSDF.glossy(Color.hsb(120, 0.5, 0.5), 0.5);
//					BSDF.diffuse(0.5);
			bodies.add(Body.textured(HalfSpace.pn(Vec3.xyz(0, -4, 0), Vec3.xyz(0, 1, 0)), new Material(bsdfFloor)));
		}
		
		{ // Lights
			double d = 100;
			bodies.addAll(List.of(
//					Body.uniform(Ball.cr(Vec3.xyz(-1, 2, -0.5).mul(d), d), Material.light(Color.hsb(60, 0.0, 0.06)))
			));
		}
		
		{ // ChristmasTree
			int n = 56;
			double r = 10;
			double h = 20;
			
			List<Vec3> centers = new ArrayList<>();

			while (centers.size() < n) {
				double u = sampler.uniform();
				double y = Math.pow(u, 1.0);
				Vec3 p = Vec3.py(Vector.polar(sampler.uniform(r) * Math.pow(y, 1), sampler.uniform()), h * (1-y));
				
				if (centers.stream().allMatch(c -> c.sub(p).length() > 3)) {
					centers.add(p);
					bodies.add(new Ornament(sampler, p));
				}
			}
		}

		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 6, -40)))
				.andThen(Transform.rotationAboutX(0.25 * phi))
				.andThen(Transform.rotationAboutY(theta))
		;
	}
}


class Ornament implements Body {
	
	private final Solid solid;
	private final Texture texture;
	
	
	public Ornament(Sampler sampler, Vec3 p) {
		solid = Ball.cr(p, 1);
		
		if (sampler.uniform() < 0.2) {
			Color color = Color.hsb(sampler.uniform(0), sampler.uniform(0.7), 1.0);
			Material mA = new Material(BSDF.TRANSMISIVE);//new Material(BSDF.glossy(color.mul(0.5), 1));
			Material mB = Material.light(color);
			Vector v = sampler.randomGaussian(4).roundAwayFromZero();
			texture = c -> Numeric.mod1(c.dot(v)) < 0.1 ? mB : mA;
		} else {
			Color color = Color.hsb(120, sampler.uniform(0.6, 0.8), 1);
			texture = new Material(BSDF.glossy(color, sampler.uniform(0.5, 1)));
		}
		
	}
	
	@Override
	public Solid solid() {
		return solid;
	}
	
	@Override
	public Material materialAt(Hit hit) {
		return texture.materialAt(hit.uv());
	}
}
