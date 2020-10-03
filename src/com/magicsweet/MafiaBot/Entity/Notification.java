package com.magicsweet.MafiaBot.Entity;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

public class Notification {
	List<Member> members = new ArrayList<Member>();
	TextChannel channel;
	String message;
	public Notification(TextChannel channel, List<Member> members, String message) {
		this.members = members;
		this.channel = channel;
		this.message = message;
	}
}
