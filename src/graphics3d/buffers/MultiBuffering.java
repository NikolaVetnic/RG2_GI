package graphics3d.buffers;

import com.google.common.collect.ImmutableList;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 Supports only one write at a time, and only one read at a time.
 */
public class MultiBuffering<T> implements Buffering<T> {
	
	private final ImmutableList<T> buffers;
	private final int[] nReads;
	private int iReadNext, iWriting;
	private int nReadsTotal;
	
	
	/**
	 * @param bufferSupplier A supplier of buffers.
	 * @param maxConcurrentReads The maximum number of concurrent reads that will be supported. The number of allocated
	 *                            buffers equals maxConcurrentReads + 2.
	 */
	public MultiBuffering(Supplier<T> bufferSupplier, int maxConcurrentReads) {
		int n = maxConcurrentReads + 2;
		
		//noinspection UnstableApiUsage
		ImmutableList.Builder<T> builder = ImmutableList.builderWithExpectedSize(n);
		for (int i = 0; i < n; i++) {
			builder.add(bufferSupplier.get());
		}
		buffers = builder.build();
		
		nReads = new int[n];
		iWriting = -1;
		nReadsTotal = 0;
		iReadNext = 0;
	}
	
	
	@Override
	public void read(Consumer<T> bufferAction) {
		int i;
		
		synchronized (this) {
			if (nReadsTotal >= buffers.size() - 2) {
				throw new IllegalStateException("Cannot have more than " + (buffers.size() - 2) + " of concurrent reads.");
			}
			i = iReadNext;
			nReadsTotal++;
			nReads[i]++;
		}
		
		bufferAction.accept(buffers.get(i));
		
		synchronized (this) {
			nReads[i]--;
			nReadsTotal--;
		}
	}
	
	
	@Override
	public void write(Consumer<T> bufferAction) {
		synchronized (this) {
			if (iWriting != -1) {
				throw new IllegalStateException("Only one writing at a time allowed.");
			}
			for (int b = 0; b < buffers.size(); b++) {   // Not efficient for a large number of buffers.
				if (nReads[b] == 0 && b != iReadNext) {
					iWriting = b;
					break;
				}
			}
		}
		
		bufferAction.accept(buffers.get(iWriting));
		
		synchronized (this) {
			iReadNext = iWriting;
			iWriting = -1;
		}
	}
	
}
