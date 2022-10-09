package com.meteoritegames.duel;

import com.meteoritegames.duel.commands.Duel;
import com.meteoritepvp.api.MeteoritePlugin;

public class Main extends MeteoritePlugin {
	public static Main plugin;

	@Override
	protected void onInit() {
		super.onInit();
		plugin = this;

		print("Duel enabled.");

		registerCommandClass(Duel.class);
	}
}
