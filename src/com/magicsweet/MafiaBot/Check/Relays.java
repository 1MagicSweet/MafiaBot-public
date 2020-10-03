package com.magicsweet.MafiaBot.Check;

import java.util.HashMap;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.User;

public interface Relays {
	HashMap<GuildChannel, User> openedRelaysGC = new HashMap<GuildChannel, User>();
	HashMap<User, GuildChannel> openedRelaysM = new HashMap<User, GuildChannel>();
	
	public static boolean isRelayOpened(User member) {
		if (openedRelaysGC.containsValue(member)) return true;
		return false;
	}
	public static boolean isRelayOpened(GuildChannel channel) {
		if (openedRelaysGC.containsKey(channel)) return true;
		return false;
	}
	
	public static void add(GuildChannel channel, User member) {
		openedRelaysGC.put(channel, member);
		openedRelaysM.put(member, channel);
	}
	public static void add(User member, GuildChannel channel) {
		openedRelaysGC.put(channel, member);
		openedRelaysM.put(member, channel);
	}
	
	public static void remove(GuildChannel channel) {
		openedRelaysGC.remove(channel);
	}
	public static void remove(User member) {
		openedRelaysM.remove(member);
	}
	
	public static User getByGuildChannel(GuildChannel channel) {
		return openedRelaysGC.get(channel);
	}
	
	public static GuildChannel getByMember(User member) {
		return openedRelaysM.get(member);
	}
}
