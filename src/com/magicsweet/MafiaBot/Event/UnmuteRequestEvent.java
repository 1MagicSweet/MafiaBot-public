package com.magicsweet.MafiaBot.Event;

import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Entity.Game;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class UnmuteRequestEvent {

		@SubscribeEvent
		public void onJoin(GuildVoiceJoinEvent event) {
		if (event.getChannelJoined().getName().equalsIgnoreCase("Unmute me")) {
			event.getMember().mute(false).queue();
			event.getGuild().moveVoiceMember(event.getMember(), null).queue();
			event.getMember().modifyNickname(event.getMember().getUser().getName());
		}
		}
		@SubscribeEvent
		public void onMove(GuildVoiceMoveEvent event) throws NullPointerException {
			if (event.getChannelJoined().getName().equalsIgnoreCase("Unmute me")) {
				boolean bool = false;
				for (Game g : GameControl.games) {
					if (g.ifGameChannel(event.getChannelLeft())) 
						bool = true;
				}
				
				if (!bool) event.getMember().mute(false).queue();
				if (!bool) event.getMember().modifyNickname(event.getMember().getUser().getName());
				event.getGuild().moveVoiceMember(event.getMember(), event.getChannelLeft()).queue();
				
			}
		}

	
}
