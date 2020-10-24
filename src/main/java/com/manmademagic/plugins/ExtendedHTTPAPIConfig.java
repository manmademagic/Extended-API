package com.manmademagic.plugins;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("example")
public interface ExtendedHTTPAPIConfig extends Config
{
	@ConfigItem(
		keyName = "exposedPort",
		name = "Port",
		description = "This is the port that the API will be exposed on - Disable/Enable the plugin to restart the server on the new port",
		position = -2

	)
	@Units("")
	default int exposedPort()
	{
		return 8080;
	}

	@ConfigItem(
			keyName = "exposeStats",
			name = "Stats",
			description = "Current Stats - Available at /stats"
	)
	default boolean exposeStats() {
		return true;
	}

	@ConfigItem(
			keyName = "exposeUsername",
			name = "Username",
			description = "Username - Available at /stats"
	)
	default boolean exposeUsername() {
		return true;
	}
	@ConfigItem(
			keyName = "exposeWorld",
			name = "World",
			description = "Currently logged in world - Available at /world"
	)
	default boolean exposeWorld() {
		return true;
	}
	@ConfigItem(
			keyName = "exposeInventory",
			name = "Inventory",
			description = "Information about inventory items - Available at /inventory"
	)
	default boolean exposeInventory() {
		return true;
	}
	@ConfigItem(
			keyName = "exposeEnergy",
			name = "Energy Level",
			description = "Current energy level - Available at /energy"
	)
	default boolean exposeEnergy() {
		return true;
	}
	@ConfigItem(
			keyName = "exposeWeight",
			name = "Current Weight",
			description = "Current weight - Available at /weight"
	)
	default boolean exposeWeight() {
		return true;
	}

	@ConfigItem(
			keyName = "exposeQuests",
			name = "Quest Status",
			description = "Quest status - Available at /quests"
	)
	default boolean exposeQuests() {
		return true;
	}
}
