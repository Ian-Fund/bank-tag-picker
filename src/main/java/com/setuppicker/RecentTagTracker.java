package com.setuppicker;

/** Tracks active-tag transitions on the client thread without retaining a previous profile's state. */
final class RecentTagTracker
{
	private String previous;

	boolean observe(String active, boolean enabled)
	{
		final String current = active == null ? "" : active;
		final boolean opened = previous != null && !current.isEmpty() && !current.equals(previous);
		previous = current;
		return enabled && opened;
	}

	void reset()
	{
		// The first observation establishes a baseline, including a restored active tag.
		previous = null;
	}
}
