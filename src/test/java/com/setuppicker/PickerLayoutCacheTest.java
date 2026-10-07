package com.setuppicker;

import java.awt.Rectangle;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PickerLayoutCacheTest
{
	@Test
	public void resizingAndChangingSettingsPreservesGeometryAndHitTesting()
	{
		PickerLayoutCache cache = new PickerLayoutCache();
		for (int cycle = 0; cycle < 80; cycle++)
		{
			int canvasWidth = 600 + cycle * 7;
			int canvasHeight = 400 + cycle * 3;
			int count = cycle % 23;
			int rowHeight = cycle % 2 == 0 ? 16 : 20;
			int rows = 3 + cycle % 23;
			for (int repeat = 0; repeat < 2; repeat++)
			{
				assertLayout(PickerLayout.computePalette(new Rectangle(canvasWidth, canvasHeight), 240, count, rowHeight, rows),
					cache.palette(canvasWidth, canvasHeight, 240, count, rowHeight, rows));
			}
			Rectangle bank = new Rectangle(cycle * 5, cycle * 2, 400 + cycle, 300 + cycle);
			boolean left = cycle % 2 == 0, collapsed = cycle % 3 == 0, upright = cycle % 4 == 0;
			int width = 110 + cycle;
			for (int repeat = 0; repeat < 2; repeat++)
			{
				assertLayout(PickerLayout.compute(bank, canvasWidth, left, width, collapsed, upright, count, rowHeight),
					cache.bank(bank, canvasWidth, left, width, collapsed, upright, count, rowHeight));
			}
		}
	}

	private static void assertLayout(PickerLayout expected, PickerLayout actual)
	{
		assertEquals(expected.getBounds(), actual.getBounds());
		assertEquals(expected.getHeader(), actual.getHeader());
		assertEquals(expected.getSearch(), actual.getSearch());
		assertEquals(expected.getList(), actual.getList());
		assertEquals(expected.getFooter(), actual.getFooter());
		assertEquals(expected.isPalette(), actual.isPalette());
		assertEquals(expected.isCollapsed(), actual.isCollapsed());
		assertEquals(expected.isUprightTab(), actual.isUprightTab());
		assertEquals(expected.getVisibleRows(), actual.getVisibleRows());
		for (int row = 0; row < actual.getVisibleRows(); row++)
		{
			assertEquals(expected.getRow(row), actual.getRow(row));
			assertEquals(row, actual.rowAt(actual.getRow(row).getLocation()));
		}
	}
}
