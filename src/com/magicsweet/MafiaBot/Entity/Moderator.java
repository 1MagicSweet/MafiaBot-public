package com.magicsweet.MafiaBot.Entity;

import net.dv8tion.jda.api.entities.Member;

public class Moderator {
	Member member;
	public Moderator(Member member) {
		this.member = member;
	}
	
	public Member getMember() {
		return member;
	}
}
