package com.setuppicker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecentTagTrackerTest
{
	@Test
	public void nativeTagChangesUpdateHistoryOnlyOnce()
	{
		RecentTagTracker tracker = new RecentTagTracker();
		List<String> recent = Collections.emptyList();
		int saves = 0;
		for (String active : new String[]{"", "Zulrah", "Zulrah", "Zulrah", "Vorkath", "Vorkath", "Zulrah"})
		{
			if (tracker.observe(active, true))
			{
				recent = SetupRepository.markUsed(recent, active);
				saves++;
			}
		}
		assertEquals(Arrays.asList("Zulrah", "Vorkath"), recent);
		assertEquals(3, saves);
	}

	@Test
	public void closingTagDoesNotRecordEmptyNameAndReopeningIsDetected()
	{
		RecentTagTracker tracker = new RecentTagTracker();
		assertFalse(tracker.observe("", true));
		assertTrue(tracker.observe("Zulrah", true));
		assertFalse(tracker.observe(null, true));
		assertFalse(tracker.observe("", true));
		assertTrue(tracker.observe("Zulrah", true));
	}

	@Test
	public void enablingHistoryDoesNotRecordAnUnchangedTag()
	{
		RecentTagTracker tracker = new RecentTagTracker();
		assertFalse(tracker.observe("", false));
		assertFalse(tracker.observe("Zulrah", false));
		assertFalse(tracker.observe("Zulrah", true));
		assertTrue(tracker.observe("Vorkath", true));
	}

	@Test
	public void restoredTagsAndProfileResetsEstablishBaseline()
	{
		RecentTagTracker tracker = new RecentTagTracker();
		assertFalse(tracker.observe("Zulrah", true));
		assertTrue(tracker.observe("Vorkath", true));
		tracker.reset();
		assertFalse(tracker.observe("Cerberus", true));
		assertFalse(tracker.observe("Cerberus", true));
		assertTrue(tracker.observe("Zulrah", true));
	}

	@Test
	public void pickerOpenCanEstablishBaselineWithoutDoubleRecording()
	{
		RecentTagTracker tracker = new RecentTagTracker();
		tracker.observe("", true);
		assertFalse(tracker.observe("Zulrah", false));
		assertFalse(tracker.observe("Zulrah", true));
		assertTrue(tracker.observe("Vorkath", true));
	}
}
