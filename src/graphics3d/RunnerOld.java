package graphics3d;

import graphics3d.renderers.raytracing.RayTracer;
import graphics3d.scenes.LongCorridor;
import graphics3d.tonemappers.SoftOld;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import mars.drawingx.application.DrawingApplication;
import mars.drawingx.application.Options;
import mars.drawingx.drawing.Drawing;
import mars.drawingx.drawing.DrawingUtils;
import mars.drawingx.drawing.View;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetBoolean;
import mars.drawingx.gadgets.annotations.Properties;
import mars.drawingx.gadgets.annotations.RecurseGadgets;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.input.InputEvent;
import mars.input.InputState;
import mars.time.ProfilerPool;
import mars.time.Timer;

import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;

/*

Runner (TripleBuffering and 2 threads)
				tone map
				fast	slow	example
render  fast	0.002	0.005
		slow	0.418	0.506	0.470
		example	        		0.049
			
			
RunnerOld
				tone map
				fast	slow	example
render	fast	0.003	0.121
		slow	0.371	0.510	0.376
		example                 0.048


 */

public class RunnerOld implements Drawing {

	static final Vector imageSize = Vector.xy(640, 640);
	static final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	
	@GadgetBoolean
	@DoNotDetectChanges
	@Properties(name = "Show stats")
	boolean showStats = false;
	
	@RecurseGadgets
//	Function0<ToneMapper> sToneMapper = new graphics3d.tonemappers.Clamp.Factory();
	Function0<ToneMapper> sToneMapper = new SoftOld.Factory();

	@RecurseGadgets
//	Function2<Renderer, Scene, Vector> sRenderer = new graphics3d.renderers.raytracing.Sonar.Factory();
//	Function2<Renderer, Scene, Vector> sRenderer = new graphics3d.renderers.raytracing.IAmLight.Factory();
//	Function2<Renderer, Scene, Vector> sRenderer = new graphics3d.renderers.raytracing.RayCaster.Factory();
	Function2<Renderer, Scene, Vector> sRenderer = new RayTracer.Factory();
	
	@RecurseGadgets
//	Function1<Scene, Double> sScene = new graphics3d.scenes.Test1.Factory();
	Function1<Scene, Double> sScene = new LongCorridor.Factory();

	
	
	Renderer renderer;
	int renderIterations;
	
	Timer timer;
	boolean shouldResetRenderer = false;
	boolean shouldToneMap = true;
	
	int[] imageData;

	
	
	void resetRenderer() {
		renderer = sRenderer.at(sScene.at(0.0), imageSize);
		renderIterations = 0;
		timer = new Timer();
		shouldResetRenderer = false;
	}
	
	
	@Override
	public void init() {
		resetRenderer();
		
		Runnable rendering = () -> {
			Color[][] pixelColors = new Color[imageSize.yInt()][imageSize.xInt()];
			
			while (true) {
				if (shouldResetRenderer) {
					resetRenderer();
				}
				
				ProfilerPool.get("rendering").enter();
				renderer.renderIteration(pixelColors);
				ProfilerPool.get("rendering").exit();
				renderIterations++;
				
				if (shouldToneMap) {
					ProfilerPool.get("tone mapping").enter();
					imageData = sToneMapper.at().toneMap(pixelColors);
					ProfilerPool.get("tone mapping").exit();
					shouldToneMap = false;
				}
			}
		};
		
		ForkJoinPool pool = new ForkJoinPool(); // Set parallelism to 4 when screen sharing
		pool.submit(rendering);
	}
	
	
	Image generateImage() {
		WritableImage image = new WritableImage(imageSize.yInt(), imageSize.xInt());
		PixelWriter pw = image.getPixelWriter();
		pw.setPixels(0, 0, imageSize.xInt(), imageSize.yInt(), pixelFormat, imageData, 0, imageSize.yInt());
		return image;
	}
	
	
	@Override
	public void draw(View view, InputState inputState) {
		double time = timer.getTime();
		
		DrawingUtils.clear(view, javafx.scene.paint.Color.BLACK);
		
		if (imageData != null) {
			view.drawImageCentered(Vector.ZERO, generateImage());
			shouldToneMap = true;
		}
		
		if (showStats) {
			DrawingUtils.drawInfoText(view,
					String.format("Number of iterations: %d", renderIterations),
					String.format("Time elapsed: %.3f", time),
					String.format("Time per iteration: %.3f", time / renderIterations)
			);
		}
	}
	
	
	@Override
	public void receiveEvent(View view, InputEvent event, InputState state, Vector pointerWorld, Vector pointerViewBase) {
		if (event.isKeyPress(KeyCode.ENTER)) {
			shouldResetRenderer = true;
		}
	}
	
	
	@Override
	public void valuesChanged() {
		shouldResetRenderer = true;
	}
	
	
	public static void main(String[] args) {
		Options options = new Options();
		options.drawingSize = RunnerOld.imageSize;
		DrawingApplication.launch(options);
	}
	
}
