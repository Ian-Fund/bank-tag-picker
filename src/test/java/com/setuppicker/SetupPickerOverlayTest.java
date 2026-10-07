package com.setuppicker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import net.runelite.api.Client;
import net.runelite.client.events.ConfigChanged;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SetupPickerOverlayTest
{
	private Color header = Color.RED;
	private int canvasWidth = 800;
	private int popupRows = 10;

	@Test
	public void liveColorsAndGeometryChangeWithoutReloadingTagData() throws Exception
	{
		Client client = (Client) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{Client.class},
			(proxy, method, args) ->
			{
				switch (method.getName())
				{
					case "getCanvasWidth": return canvasWidth;
					case "getCanvasHeight": return 600;
					case "getMouseCanvasPosition": return null;
					default: throw new UnsupportedOperationException(method.getName());
				}
			});
		SetupPickerConfig config = new SetupPickerConfig()
		{
			@Override public boolean showIcons() { return false; }
			@Override public Color headerColor() { return header; }
			@Override public int popupRows() { return popupRows; }
		};
		PickerModel model = new PickerModel();
		model.openPalette();
		SetupPickerOverlay overlay = new SetupPickerOverlay(client, config, model, null);
		SetupPickerPlugin plugin = new SetupPickerPlugin();
		Field overlayField = SetupPickerPlugin.class.getDeclaredField("overlay");
		overlayField.setAccessible(true);
		overlayField.set(plugin, overlay);
		BufferedImage image = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			overlay.render(graphics);
			assertHeader(image, overlay, Color.RED);
			Rectangle first = new Rectangle(overlay.getLayout().getBounds());
			header = Color.BLUE;
			ConfigChanged event = new ConfigChanged();
			event.setGroup(SetupPickerConfig.GROUP);
			event.setKey("headerColor");
			// No ClientThread is supplied: appearance changes must not request a repository reload.
			plugin.onConfigChanged(event);
			overlay.render(graphics);
			assertHeader(image, overlay, Color.BLUE);
			assertEquals(first, overlay.getLayout().getBounds());
			canvasWidth = 1000;
			popupRows = 3;
			event.setKey("popupRows");
			plugin.onConfigChanged(event);
			overlay.render(graphics);
			PickerLayout expected = PickerLayout.computePalette(new Rectangle(1000, 600), 240, 0, 16, 3);
			assertEquals(expected.getBounds(), overlay.getLayout().getBounds());
			assertHeader(image, overlay, Color.BLUE);
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static void assertHeader(BufferedImage image, SetupPickerOverlay overlay, Color expected)
	{
		Rectangle header = overlay.getLayout().getHeader();
		assertEquals(expected.getRGB(), image.getRGB(header.x + 5, header.y + 3));
	}
}
