package com.magicsweet.MafiaBot.Event;

import com.magicsweet.MafiaBot.Check.Relays;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class RelayMessageUsedEvent {

	@SubscribeEvent
	public void pm(PrivateMessageReceivedEvent event) {
		if (Relays.isRelayOpened(event.getAuthor())) {
			TextChannel channel = (TextChannel) Relays.getByMember(event.getAuthor());
			channel.sendMessage(event.getMessage().getContentRaw()).queue();
		}
	}
	@SubscribeEvent
	public void guild(GuildMessageReceivedEvent event) throws NullPointerException {
		if (Relays.isRelayOpened((GuildChannel) event.getChannel()) 
		&& !event.getMember().getUser().equals(event.getJDA().getSelfUser())
		&& !event.getMessage().getContentRaw().startsWith("/")) {
			User channel = Relays.getByGuildChannel((GuildChannel) event.getChannel());
			
			channel.openPrivateChannel().complete().sendMessage(event.getMessage().getContentRaw()).queue();
			
		}
	}
}
