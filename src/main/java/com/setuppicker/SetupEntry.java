package com.setuppicker;

import java.awt.Color;
import java.util.Locale;

/**
 * One row in the picker: the bits of an inventory setup needed to list it.
 */
public final class SetupEntry
{
	private final String name;
	private final String searchName;
	private final boolean favorite;
	private final Color displayColor;
	private final int iconItemId;

	public SetupEntry(String name, boolean favorite, Color displayColor, int iconItemId)
	{
		this.name = name;
		this.searchName = name.toLowerCase(Locale.ROOT);
		this.favorite = favorite;
		this.displayColor = displayColor;
		this.iconItemId = iconItemId;
	}

	public String getName()
	{
		return name;
	}

	public boolean isFavorite()
	{
		return favorite;
	}

	String getSearchName()
	{
		return searchName;
	}

	/**
	 * The color the user gave the setup in Inventory Setups, or null for none.
	 */
	public Color getDisplayColor()
	{
		return displayColor;
	}

	public int getIconItemId()
	{
		return iconItemId;
	}
}
