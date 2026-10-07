package com.setuppicker;

import java.util.Arrays;

/**
 * Decides whether a name matches what has been typed in the search box.
 *
 * <p>There are three ways to match, from strict to loose. The search uses the strictest one that finds
 * anything, so the looser ones only come into play when a search would otherwise come up empty.
 */
public final class SetupMatcher
{
	public enum Tier
	{
		/**
		 * The name contains the search text.
		 */
		EXACT,
		/**
		 * The name contains the search text give or take a slip: a wrong, missing or extra letter, or two
		 * letters swapped.
		 */
		TYPO,
		/**
		 * The name contains the search text's letters in order, with anything in between ("vkdh" for
		 * "Vorkath (dhcb)").
		 */
		SUBSEQUENCE
	}

	// Short searches are too easy to match by accident to allow slips in
	private static final int MIN_LENGTH_FOR_TYPO = 4;
	private static final int MIN_LENGTH_FOR_TWO_TYPOS = 8;
	private static final int MIN_LENGTH_FOR_SUBSEQUENCE = 3;

	private SetupMatcher()
	{
	}

	/**
	 * @param name   lower case
	 * @param needle the search text, lower case and not empty
	 */
	public static boolean matches(String name, String needle, Tier tier)
	{
		return matches(name, needle, tier, new Workspace());
	}

	// Owned by a synchronized PickerModel; never shared between concurrent searches.
	static final class Workspace
	{
		private int[] older = new int[0];
		private int[] previous = new int[0];
		private int[] current = new int[0];

		private void ensureCapacity(int length)
		{
			if (previous.length < length)
			{
				older = new int[length];
				previous = new int[length];
				current = new int[length];
			}
		}
	}

	static boolean matches(String name, String needle, Tier tier, Workspace workspace)
	{
		switch (tier)
		{
			case EXACT:
				return name.contains(needle);
			case TYPO:
				if (needle.length() < MIN_LENGTH_FOR_TYPO)
				{
					return false;
				}
				return closestSubstringDistance(name, needle, workspace) <= (needle.length() < MIN_LENGTH_FOR_TWO_TYPOS ? 1 : 2);
			default:
				return needle.length() >= MIN_LENGTH_FOR_SUBSEQUENCE && isSubsequence(name, needle);
		}
	}

	private static boolean isSubsequence(String name, String needle)
	{
		int matched = 0;
		for (int i = 0; i < name.length() && matched < needle.length(); i++)
		{
			if (name.charAt(i) == needle.charAt(matched))
			{
				matched++;
			}
		}
		return matched == needle.length();
	}

	/**
	 * The fewest single-letter edits (replace, insert, delete, or swap two neighbours) that turn the needle
	 * into some stretch of the name.
	 */
	static int closestSubstringDistance(String name, String needle)
	{
		return closestSubstringDistance(name, needle, new Workspace());
	}

	static int closestSubstringDistance(String name, String needle, Workspace workspace)
	{
		final int n = needle.length();
		final int m = name.length();
		workspace.ensureCapacity(m + 1);
		int[] older = workspace.older;
		int[] previous = workspace.previous;
		int[] current = workspace.current;
		// Row zero allows a match to start anywhere in the name for free.
		Arrays.fill(previous, 0, m + 1, 0);
		for (int i = 1; i <= n; i++)
		{
			current[0] = i;
			for (int j = 1; j <= m; j++)
			{
				final int replace = needle.charAt(i - 1) == name.charAt(j - 1) ? 0 : 1;
				int best = Math.min(previous[j - 1] + replace,
					Math.min(previous[j] + 1, current[j - 1] + 1));
				if (i > 1 && j > 1 && needle.charAt(i - 1) == name.charAt(j - 2) && needle.charAt(i - 2) == name.charAt(j - 1))
				{
					best = Math.min(best, older[j - 2] + 1);
				}
				current[j] = best;
			}
			final int[] spare = older;
			older = previous;
			previous = current;
			current = spare;
		}
		int closest = n;
		for (int j = 0; j <= m; j++)
		{
			closest = Math.min(closest, previous[j]);
		}
		return closest;
	}
}
