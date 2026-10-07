package com.setuppicker;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.runelite.api.events.ClientTick;
import org.junit.Test;
import static org.junit.Assert.*;

public class NativeRecentTagsTest
{
	@Test
	public void clientTicksSaveNativeSelectionsWithoutRepeatedReadsOrWrites() throws Exception
	{
		FakeRepository repository = new FakeRepository();
		PickerModel model = new PickerModel();
		SetupPickerPlugin plugin = new SetupPickerPlugin();
		inject(plugin, "repository", repository);
		inject(plugin, "model", model);
		inject(plugin, "chatboxKeyGuard", new ChatboxKeyGuard(null));
		inject(plugin, "config", new SetupPickerConfig()
		{
			@Override public int recentCount() { return 5; }
		});
		plugin.onClientTick(new ClientTick());
		repository.active = "Zulrah";
		plugin.onClientTick(new ClientTick());
		for (int i = 0; i < 100; i++)
		{
			plugin.onClientTick(new ClientTick());
		}
		assertEquals(1, repository.reads);
		assertEquals(1, repository.saves);
		repository.active = "Vorkath";
		plugin.onClientTick(new ClientTick());
		repository.active = "Zulrah";
		plugin.onClientTick(new ClientTick());
		assertEquals(Arrays.asList("Zulrah", "Vorkath"), repository.recent);
		assertEquals(3, repository.saves);
		repository.active = "";
		plugin.onClientTick(new ClientTick());
		assertEquals(3, repository.saves);
		inject(plugin, "config", new SetupPickerConfig() {});
		repository.active = "Cerberus";
		plugin.onClientTick(new ClientTick());
		assertEquals(3, repository.reads);
		assertEquals(3, repository.saves);
	}

	private static void inject(SetupPickerPlugin plugin, String name, Object value) throws Exception
	{
		Field field = SetupPickerPlugin.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(plugin, value);
	}

	private static final class FakeRepository extends SetupRepository
	{
		String active = "";
		List<String> recent = Collections.emptyList();
		int reads;
		int saves;

		FakeRepository()
		{
			super(null, null, null);
		}

		@Override public String queryActiveSetup() { return active; }
		@Override public List<String> loadRecent() { reads++; return recent; }
		@Override public void saveRecent(List<String> updated) { saves++; recent = updated; }
	}
}
