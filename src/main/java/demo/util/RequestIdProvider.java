package demo.util;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestIdProvider {
	private static final AtomicInteger nextId = new AtomicInteger(1);

	private static final ThreadLocal<Integer> threadId = new ThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			return nextId.getAndIncrement();
		}
	};

	public static void update() {
		threadId.set(nextId.getAndIncrement());
	}

	public static int get() {
		return threadId.get();
	}
}
