/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.esdk.core;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * To obtain at least some degree of reproducible behavior, this simulation implements delta-cycles in a similar
 * way as HDLs do: A "batch" of events that are scheduled for "now" is taken off the queue as a whole, then processed
 * as a whole. If this schedules new events for "now", then the simulation still processes the whole "old" batch
 * before looking at those new events. When the batch is finished, a new batch is taken and processed, again not looking
 * at further events which get scheduled for "now" by the second batch.
 *
 * No batching is done for events scheduled for later. This means, for example, that an event scheduled by the second
 * "now"-batch for one second in the future may actually be processed *before* an event scheduled by the first
 * "now"-batch for one second in the future.
 */
final class Simulation {

	private final PriorityQueue<ScheduledEvent> eventQueue = new PriorityQueue<>();
	private long now = 0;
	private boolean stopped = false;

	public void fire(Runnable eventCallback, long ticks) {
		if (eventCallback == null) {
			throw new IllegalArgumentException("eventCallback cannot be null");
		}
		if (ticks < 0) {
			throw new IllegalArgumentException("ticks cannot be negative");
		}
		eventQueue.add(new ScheduledEvent(now + ticks, eventCallback));
	}

	public void run() {
		stopped = false;
		List<ScheduledEvent> batch = new ArrayList<>();
		while (!stopped && !eventQueue.isEmpty()) {
			now = eventQueue.peek().when;
			batch.clear();
			while (!eventQueue.isEmpty() && eventQueue.peek().when == now) {
				batch.add(eventQueue.remove());
			}
			for (ScheduledEvent event : batch) {
				event.callback.run();
			}
		}
	}

	public void stop() {
		stopped = true;
	}

	private static class ScheduledEvent implements Comparable<ScheduledEvent> {

		final long when;
		final Runnable callback;

		ScheduledEvent(long when, Runnable callback) {
			this.when = when;
			this.callback = callback;
		}

		@Override
		public int compareTo(ScheduledEvent o) {
			return Long.compare(when, o.when);
		}

	}

}
