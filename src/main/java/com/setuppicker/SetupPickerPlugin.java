package com.setuppicker;

import com.google.inject.Binder;
import com.google.inject.Provides;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.PluginChanged;
import net.runelite.client.events.ProfileChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.banktags.BankTagsPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Bank Tag Picker",
	description = "Quickly search and open Bank Tags and their saved layouts",
	tags = {"bank", "tags", "layouts", "hotkey", "search", "quick", "switch", "picker"}
)
@PluginDependency(BankTagsPlugin.class)
public class SetupPickerPlugin extends Plugin
{
	private static final String BANK_TAGS_PLUGIN_NAME = "Bank Tags";

	@Inject
	private ClientThread clientThread;

	@Inject
	private ConfigManager configManager;

	@Inject
	private PluginManager pluginManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatboxKeyGuard chatboxKeyGuard;

	@Inject
	private SetupPickerConfig config;

	@Inject
	private SetupRepository repository;

	@Inject
	private PickerModel model;

	@Inject
	private SetupPickerOverlay overlay;

	@Inject
	private PickerInput input;

	private final AtomicBoolean refreshQueued = new AtomicBoolean();

	@Override
	public void configure(Binder binder)
	{
		binder.bind(PickerModel.class).asEagerSingleton();
	}

	@Provides
	SetupPickerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SetupPickerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlay.invalidateTheme();
		overlayManager.add(overlay);
		mouseManager.registerMouseListener(input);
		mouseManager.registerMouseWheelListener(input);
		keyManager.registerKeyListener(input);

		queueRefresh();
		clientThread.invokeLater(() -> model.setActiveSetup(repository.queryActiveSetup()));
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		mouseManager.unregisterMouseListener(input);
		mouseManager.unregisterMouseWheelListener(input);
		keyManager.unregisterKeyListener(input);
		clientThread.invoke(chatboxKeyGuard::restore);
		overlay.clearLayout();
		model.resetSearch();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		final boolean pickerConfig = SetupPickerConfig.GROUP.equals(event.getGroup());
		if (pickerConfig && event.getKey().endsWith("Color"))
		{
			overlay.invalidateTheme();
		}
		final boolean bankTagsEdited = SetupRepository.BANK_TAGS_CONFIG_GROUP.equals(event.getGroup())
			&& (SetupRepository.BANK_TAGS_TABS_KEY.equals(event.getKey())
			|| event.getKey().startsWith(SetupRepository.BANK_TAGS_ICON_PREFIX));
		final String key = event.getKey();
		final boolean searchConfig = pickerConfig && ("alphabetical".equals(key) || "recentCount".equals(key)
			|| "fuzzySearch".equals(key) || "startOnOpenSetup".equals(key) || SetupPickerConfig.KEY_RECENT.equals(key));
		if (bankTagsEdited || searchConfig)
		{
			queueRefresh();
		}
	}

	@Subscribe
	public void onProfileChanged(ProfileChanged event)
	{
		overlay.invalidateTheme();
		queueRefresh();
	}

	@Subscribe
	public void onPluginChanged(PluginChanged event)
	{
		if (BANK_TAGS_PLUGIN_NAME.equals(event.getPlugin().getName()))
		{
			queueRefresh();
		}
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.BANKMAIN)
		{
			queueRefresh();
		}
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed event)
	{
		// the popup is independent of the bank, so only a search in the list beside it ends here
		if (event.getGroupId() == InterfaceID.BANKMAIN && !model.view().isPaletteOpen())
		{
			model.resetSearch();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		chatboxKeyGuard.sync(model.view().isSearchFocused());
		model.setActiveSetup(repository.queryActiveSetup());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		// Nothing is drawn outside the game, so don't leave the keyboard captured by an invisible popup
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			model.resetSearch();
		}
	}

	/**
	 * Open the selected Bank Tag. Selecting the already-open tag is intentionally a no-op from the
	 * user's point of view, matching the normal Bank Tags tab behavior rather than toggling it closed.
	 */
	void activateSetup(SetupEntry setup)
	{
		openSetup(setup);
	}

	void openSetup(SetupEntry setup)
	{
		clientThread.invoke(() ->
		{
			repository.open(setup.getName());
			model.setActiveSetup(setup.getName());
			if (config.recentCount() > 0)
			{
				final List<String> recent = repository.loadRecent();
				final List<String> updated = SetupRepository.markUsed(recent, setup.getName());
				if (updated != recent)
				{
					repository.saveRecent(updated);
				}
			}
		});
	}

	void setCollapsed(boolean collapsed)
	{
		configManager.setConfiguration(SetupPickerConfig.GROUP, SetupPickerConfig.KEY_COLLAPSED, collapsed);
	}

	// Several triggers can fire together (for example during a profile switch), so
	// collapse them into one reload on the client thread.
	private void queueRefresh()
	{
		if (refreshQueued.compareAndSet(false, true))
		{
			clientThread.invokeLater(() ->
			{
				refreshQueued.set(false);
				final List<String> used = repository.loadRecent();
				final List<String> recent = used.subList(0, Math.max(0, Math.min(config.recentCount(), used.size())));
				final List<SetupEntry> setups = repository.loadSetups(config.alphabetical(), false);
				model.setSectionPages(false);
				model.setFuzzySearch(config.fuzzySearch());
				model.setStartOnActiveSetup(config.startOnOpenSetup());
				model.setRows(SetupRepository.group(setups, Collections.emptyList(), config.alphabetical(), false, recent), setups.size());
				overlay.setStatus(isBankTagsEnabled() ? "No bank tags yet" : "Bank Tags is off");
			});
		}
	}

	private boolean isBankTagsEnabled()
	{
		for (Plugin plugin : pluginManager.getPlugins())
		{
			if (BANK_TAGS_PLUGIN_NAME.equals(plugin.getName()))
			{
				return pluginManager.isPluginEnabled(plugin);
			}
		}
		return false;
	}
}
