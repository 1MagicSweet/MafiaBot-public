package com.magicsweet.MafiaBot.Event;

import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Entity.Game;

import net.dv8tion.jda.api.entities.GuildChannel;

public interface GameManager {

	public static Game getGameByChannel(GuildChannel channel) {
		Game g = null;
		for (Game game : GameControl.games) {
			if (game.ifGameChannel(channel)) {
			g = game;
			break;
			}
		}
		
		return g;
	}
}
