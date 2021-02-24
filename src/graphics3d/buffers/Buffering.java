package graphics3d.buffers;

import java.util.function.Consumer;

public interface Buffering<T> {
	
	void read(Consumer<T> bufferAction);
	
	void write(Consumer<T> bufferAction);
	
}
