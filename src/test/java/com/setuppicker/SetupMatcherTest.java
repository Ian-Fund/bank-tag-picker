package com.setuppicker;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SetupMatcherTest
{
	@Test
	public void reusedWorkspacePreservesDistancesAcrossDifferentLengths()
	{
		final Random random = new Random(20261006);
		final SetupMatcher.Workspace workspace = new SetupMatcher.Workspace();
		for (int sample = 0; sample < 10000; sample++)
		{
			final String name = word(random, random.nextInt(36));
			final String query = word(random, random.nextInt(18));
			assertEquals(name + " / " + query, originalDistance(name, query),
				SetupMatcher.closestSubstringDistance(name, query, workspace));
		}
		for (String[] pair : new String[][]{{"slayer", "slyaer"}, {"zulrah", "zulah"}, {"vorkath", "vorkahh"},
			{"toa 300", "300"}, {"équipement", "équip"}, {"", ""}})
		{
			assertEquals(originalDistance(pair[0], pair[1]),
				SetupMatcher.closestSubstringDistance(pair[0], pair[1], workspace));
		}
	}

	private static String word(Random random, int length)
	{
		final StringBuilder word = new StringBuilder();
		for (int i = 0; i < length; i++)
		{
			word.append("abcde 12".charAt(random.nextInt(8)));
		}
		return word.toString();
	}

	// v0.2 reference implementation: independent full matrix, including adjacent transpositions.
	static int originalDistance(String name, String needle)
	{
		int n = needle.length(), m = name.length();
		int[][] distance = new int[n + 1][m + 1];
		for (int i = 1; i <= n; i++)
		{
			distance[i][0] = i;
			for (int j = 1; j <= m; j++)
			{
				int cost = needle.charAt(i - 1) == name.charAt(j - 1) ? 0 : 1;
				int best = Math.min(distance[i - 1][j - 1] + cost,
					Math.min(distance[i - 1][j] + 1, distance[i][j - 1] + 1));
				if (i > 1 && j > 1 && needle.charAt(i - 1) == name.charAt(j - 2)
					&& needle.charAt(i - 2) == name.charAt(j - 1))
				{
					best = Math.min(best, distance[i - 2][j - 2] + 1);
				}
				distance[i][j] = best;
			}
		}
		int closest = n;
		for (int j = 0; j <= m; j++)
		{
			closest = Math.min(closest, distance[n][j]);
		}
		return closest;
	}
}
