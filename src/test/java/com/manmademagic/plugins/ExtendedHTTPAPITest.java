package com.manmademagic.plugins;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExtendedHTTPAPITest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ExtendedHTTPAPIPlugin.class);
		RuneLite.main(args);
	}
}