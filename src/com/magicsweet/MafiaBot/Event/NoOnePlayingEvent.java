package com.magicsweet.MafiaBot.Event;

import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Entity.Game;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class NoOnePlayingEvent {

	@SubscribeEvent
	public void onLeave(GuildVoiceLeaveEvent event) {
		if (!GameControl.games.isEmpty()) {
			for (Game g : GameControl.games) {
				VoiceChannel c = (VoiceChannel) g.getChannel("game-voice");
				if (c == null) return;
				if (!c.getMembers().isEmpty()) return;
				g.delete();
				return;
			}
		}
	}
	@SubscribeEvent
	public void onMove(GuildVoiceMoveEvent event) throws NullPointerException {
		if (!GameControl.games.isEmpty()) {
			for (Game g : GameControl.games) {
				VoiceChannel c = (VoiceChannel) g.getChannel("game-voice");
				if (c == null) return;
				if (!c.getMembers().isEmpty()) return;
				g.delete();
				return;
			}
		}
	}
}
