package com.magicsweet.MafiaBot.Event;

import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Entity.Game;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class JoinToSpectateEvent {
	
	@SubscribeEvent
	public void onLeave(GuildVoiceJoinEvent event) {
		
		if (!GameControl.games.isEmpty()) {
			boolean bool = false;
			Game game = null;
			for (Game g : GameControl.games) {
				bool = event.getMember().getRoles().contains(g.getDiscordRole("ingame"));
				if (g.ifChannelApplicable("join-to-spectate", (VoiceChannel) event.getChannelJoined())) game = g;
			}
			
			if (bool) {
				event.getGuild().moveVoiceMember(event.getMember(), (VoiceChannel) game.getChannel("game-voice")).queue();
			} else {
				event.getGuild().moveVoiceMember(event.getMember(), (VoiceChannel) game.getChannel("game-voice")).queue();
				event.getGuild().addRoleToMember(event.getMember(), game.getDiscordRole("spectator")).queue();
				event.getMember().modifyNickname("gamemode spectator").queue();
				event.getMember().mute(true).queue();
			}
		}
	}
	
	@SubscribeEvent
	public void onMove(GuildVoiceMoveEvent event) throws NullPointerException {
		try {
			if (!GameControl.games.isEmpty()) {
				boolean bool = false;
				Game game = null;
				for (Game g : GameControl.games) {
					bool = event.getMember().getRoles().contains(g.getDiscordRole("ingame"));
					if (g.ifChannelApplicable("join-to-spectate", (VoiceChannel) event.getChannelJoined())) game = g;
				}
				
				if (bool) {
					event.getGuild().moveVoiceMember(event.getMember(), (VoiceChannel) game.getChannel("game-voice")).queue();
				} else {
					event.getGuild().moveVoiceMember(event.getMember(), (VoiceChannel) game.getChannel("game-voice")).queue();
					event.getGuild().addRoleToMember(event.getMember(), game.getDiscordRole("spectator")).queue();
					event.getMember().modifyNickname("gamemode spectator").queue();
					event.getMember().mute(true).queue();
				}
			}
		} catch (NullPointerException e) {}
	}
}
