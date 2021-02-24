package graphics3d.buffers;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 Supports only one write at a time, and only one read at a time.
 */
public class NoBuffering<T> implements Buffering<T> {
	
	private final T buffer;
	
	
	
	public NoBuffering(T buffer) {
		this.buffer = buffer;
	}
	
	
	public NoBuffering(Supplier<T> bufferSupplier) {
		this(bufferSupplier.get());
	}
	
	
	public void read(Consumer<T> bufferAction) {
		bufferAction.accept(buffer);
	}
	
	
	public void write(Consumer<T> bufferAction) {
		bufferAction.accept(buffer);
	}
	
}
