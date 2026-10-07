package com.setuppicker;

import java.awt.Rectangle;

/** Reuses geometry until a dimension, display mode, row count, or layout setting changes. */
final class PickerLayoutCache
{
	private PickerLayout layout;
	private boolean palette;
	private int x, y, areaWidth, areaHeight, canvasWidth, width, count, rowHeight, maxRows;
	private boolean left, collapsed, upright;

	PickerLayout palette(int canvasWidth, int canvasHeight, int width, int count, int rowHeight, int maxRows)
	{
		if (layout == null || !palette || areaWidth != canvasWidth || areaHeight != canvasHeight
			|| this.width != width || this.count != count || this.rowHeight != rowHeight || this.maxRows != maxRows)
		{
			layout = PickerLayout.computePalette(new Rectangle(canvasWidth, canvasHeight), width, count, rowHeight, maxRows);
			palette = true;
			areaWidth = canvasWidth;
			areaHeight = canvasHeight;
			this.width = width;
			this.count = count;
			this.rowHeight = rowHeight;
			this.maxRows = maxRows;
		}
		return layout;
	}

	PickerLayout bank(Rectangle bank, int canvasWidth, boolean left, int width, boolean collapsed,
		boolean upright, int count, int rowHeight)
	{
		if (layout == null || palette || x != bank.x || y != bank.y || areaWidth != bank.width
			|| areaHeight != bank.height || this.canvasWidth != canvasWidth || this.left != left
			|| this.width != width || this.collapsed != collapsed || this.upright != upright
			|| this.count != count || this.rowHeight != rowHeight)
		{
			layout = PickerLayout.compute(bank, canvasWidth, left, width, collapsed, upright, count, rowHeight);
			palette = false;
			x = bank.x;
			y = bank.y;
			areaWidth = bank.width;
			areaHeight = bank.height;
			this.canvasWidth = canvasWidth;
			this.left = left;
			this.width = width;
			this.collapsed = collapsed;
			this.upright = upright;
			this.count = count;
			this.rowHeight = rowHeight;
		}
		return layout;
	}
}
