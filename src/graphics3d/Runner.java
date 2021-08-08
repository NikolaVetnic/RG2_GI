package graphics3d;

import graphics3d.buffers.Buffering;
import graphics3d.buffers.MultiBuffering;
import graphics3d.scenes.VoxelWorld_TestF;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import mars.drawingx.application.DrawingApplication;
import mars.drawingx.application.Options;
import mars.drawingx.drawing.Drawing;
import mars.drawingx.drawing.DrawingUtils;
import mars.drawingx.drawing.View;
import mars.drawingx.gadgets.annotations.*;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.functions.interfaces.Function2;
import mars.geometry.Box;
import mars.geometry.Vector;
import mars.input.InputEvent;
import mars.input.InputState;
import mars.time.ProfilerPool;
import mars.time.Timer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;


public class Runner implements Drawing {
	
	static final Vector imageSize = Vector.xy(640, 640);
	static final PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbPreInstance();
	static final int PARALLELISM = ForkJoinPool.getCommonPoolParallelism();
	
	
	@GadgetBoolean
	@DoNotDetectChanges
	boolean showStats = false;
	
	@GadgetBoolean
	@DoNotDetectChanges
	boolean running = true;
	
	@GadgetBoolean
	boolean export = false;
	
	@GadgetString
	@DoNotDetectChanges
	String exportDirectory = "";
	
	@GadgetDouble(p = 0, q = 600)
	@DoNotDetectChanges
	double exportFramesPerSecond = 30;
	
	@GadgetDouble(p = 0, q = 1)
	double timeScene;
	
	
	@RecurseGadgets
//	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.Clamp.Factory();
//	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.SoftOld.Factory();
//	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.Soft2.Factory();
	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.Auto.Factory();
//	Function0<ToneMapper> fToneMapper = new graphics3d.tonemappers.SoftClamp.Factory();
	
	@RecurseGadgets
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.Sonar.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.IAmLight.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.DiffuseOnly.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.RayCaster.Factory();
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.RayTracer.Factory();
	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.raytracing.PathTracer.Factory();
	
//	Function2<Renderer, Scene, Vector> fRenderer = new graphics3d.renderers.adhoc.Oklab.Factory();
	
	@RecurseGadgets
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Test1.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.LongCorridor.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.RoomWithBalls.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Campfire.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestRoom.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Mirrors.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Glass.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.InfiniteCheckerboard.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.OrangesGlass.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.OrangesBowling.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestTransforms.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestCSG.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.SphereUV.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestCylinder.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.CandyPlanet.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Asteroids.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Boxes.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.CityOfLight.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.CityOfNight.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestGI.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestGlossy.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.FrostedGlass.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestIntegral.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.DiscoBall.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.Carvings.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.ChristmasTree.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestImageTextures.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestQuadrics.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.TestMesh.Factory();
//	Function1<Scene, Double> fScene = new graphics3d.scenes.VoxelWorld_TestScene.Factory();
	Function1<Scene, Double> fScene = new VoxelWorld_TestF.Factory();
	
	
	Renderer renderer;
	int iterationsRendering;
	int iterationsToneMapping;
	int nFramesExported = 0;
	
	Timer timer;
	double timePerIterationRendering = Double.NaN;
	boolean shouldResetRenderer = false;
	
	
	
	
	// Buffering
	
	Buffering<BufferRendering  > buffersRendering   = new MultiBuffering<>(() -> new BufferRendering  (imageSize), 3);
	Buffering<BufferToneMapping> buffersToneMapping = new MultiBuffering<>(() -> new BufferToneMapping(imageSize), 1);
	
	
	void resetRenderer() {
		if (export) {
			timeScene = nFramesExported / exportFramesPerSecond;
		}
		renderer = fRenderer.at(fScene.at(timeScene), imageSize);
		iterationsRendering = 0;
		iterationsToneMapping = 0;
		timer = new Timer();
		shouldResetRenderer = false;
	}
	
	
	private boolean initialized = false;
	private final Object toneMappingMonitor = new Object();
	
	void initialize() {
		initialized = true;
		resetRenderer();
		
		Runnable rendering = () -> {
			try {
				//noinspection InfiniteLoopStatement
				while (true) {
					while (!running) {
						//noinspection BusyWait
						Thread.sleep(100);
					}

					if (shouldResetRenderer) {
						resetRenderer();
					}
					boolean shouldExport = export;
					
					buffersRendering.write(bufferRendering -> {
						ProfilerPool.get("rendering").enter();
						renderer.renderIteration(bufferRendering.pixelColors());
						ProfilerPool.get("rendering").exit();
					});
					
					iterationsRendering++;
					timePerIterationRendering = timer.getTime() / iterationsRendering;
					
					if (shouldExport && renderer.goodEnough()) {
						exportRender();
						nFramesExported++;
						shouldResetRenderer = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		Runnable toneMapping = () -> {
			try {
				//noinspection InfiniteLoopStatement
				while (true) {
					try {
						synchronized (toneMappingMonitor) {
							toneMappingMonitor.wait();
						}
						
						//noinspection CodeBlock2Expr
						buffersRendering.read(bufferRendering -> {
							buffersToneMapping.write(bufferToneMapping -> {
								ProfilerPool.get("tone mapping").enter();
								fToneMapper.at().toneMap(bufferRendering.pixelColors(), bufferToneMapping.imageData());
								ProfilerPool.get("tone mapping").exit();
							});
						});
						
						iterationsToneMapping++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		ForkJoinPool pool = new ForkJoinPool(PARALLELISM);
		pool.submit(rendering);
		pool.submit(toneMapping);
	}
	
	
	BufferToneMapping exportingBuffer = new BufferToneMapping(imageSize);
	
	void exportRender() throws IOException {
		buffersRendering.read(bufferRendering -> {
			fToneMapper.at().toneMap(bufferRendering.pixelColors(), exportingBuffer.imageData());
		});
		
		WritableImage img = new WritableImage(imageSize.xInt(), imageSize.yInt());
		img.getPixelWriter().setPixels(0, 0, imageSize.xInt(), imageSize.yInt(), pixelFormat, exportingBuffer.imageData(), imageSize.areaInt() - imageSize.xInt(), -imageSize.xInt());
		
		String fileName = String.format("%08d.%s", nFramesExported, "png");
		File file = new File(exportDirectory, fileName);
		ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
	}
	
	
	@Override
	public void draw(View view, InputState inputState) {
		if (!initialized) {
			initialize();
		}
		double time = timer.getTime();
		
		DrawingUtils.clear(view, javafx.scene.paint.Color.BLACK);
		
		Vector p = inputState.getMousePosition().transform(view.transformation().inverse()).add(imageSize.div(2));
		
		Color[] colorPointer = new Color[1];
		
		buffersToneMapping.read(bufferToneMapping -> {
			WritableImage img = new WritableImage(imageSize.xInt(), imageSize.yInt());
			img.getPixelWriter().setPixels(0, 0, imageSize.xInt(), imageSize.yInt(), pixelFormat, bufferToneMapping.imageData(), 0, imageSize.xInt());
			view.drawImageCentered(Vector.ZERO, img);
		});
		
		buffersRendering.read(bufferRendering -> {
			colorPointer[0] = Box.d(imageSize).contains(p) ?
					bufferRendering.pixelColors[p.yInt()][p.xInt()] :
					Color.BLACK;
		});

//		buffersToneMapping.readingDone();
//		BufferToneMapping bufferToneMapping = buffersToneMapping.readingStart();
//		bufferToneMapping.pixelBuffer().updateBuffer(pb -> null);
//		view.drawImageCentered(Vector.ZERO, bufferToneMapping.image());
		
		synchronized (toneMappingMonitor) {
			toneMappingMonitor.notifyAll();
		}
		
		if (showStats) {
			DrawingUtils.drawInfoText(view,
					String.format("Renderer color at pointer: %s", colorPointer[0]),
					String.format("Iterations rendering:     %5d", iterationsRendering),
					String.format("Iterations tone mapping : %5d", iterationsToneMapping),
					String.format("Time elapsed:                 %9.3f", time),
					String.format("Time per iteration rendering: %9.3f", timePerIterationRendering)
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
		options.drawingSize = Runner.imageSize;
		DrawingApplication.launch(options);
	}
	
	
	// Buffer Classes
	
	
	private static class BufferRendering {
		private final Color[][] pixelColors;
		
		
		public BufferRendering(Vector size) {
			pixelColors = new Color[size.yInt()][size.xInt()];
			IntStream.range(0, size.yInt()).parallel().forEach(y -> {
				for (int x = 0; x < size.xInt(); x++) {
					pixelColors[y][x] = Color.BLACK;
				}
			});
		}
		
		public Color[][] pixelColors() {
			return pixelColors;
		}
	}
	
	
	private static class BufferToneMapping {
		private final int[] imageData;
		//	private final PixelBuffer<IntBuffer> pixelBuffer;
		//	private final ImageTexture image;
		
		
		public BufferToneMapping(Vector size) {
			imageData = new int[(int) size.area()];
			//		pixelBuffer = new PixelBuffer<>(size.xInt(), size.yInt(), IntBuffer.wrap(imageData), Runner.pixelFormat);
			//		image = new WritableImage(pixelBuffer);
		}
		
		public int[] imageData() {
			return imageData;
		}
		
		//	public PixelBuffer<IntBuffer> pixelBuffer() {
		//		return pixelBuffer;
		//	}
		
		//	public ImageTexture image() {
		//		return image;
		//	}
	}
	
}