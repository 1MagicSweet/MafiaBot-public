package com.magicsweet.MafiaBot.Entity;

import net.dv8tion.jda.api.entities.Member;

public class Player {
	Role role;
	Member member;
	public boolean alive = true;
	Game game;
	public Player (Member member, Game game, Role role) {
	this.member = member;
	this.role = role;
	this.game = game;
	}
	
	public Member getMember() {
		return member;
	}
	
	public Role getRole() {
		return role;
	}
	
	public void kill() {
		alive = false;
		member.getGuild().removeRoleFromMember(member, game.getDiscordRole("ingame")).queue();
		
		member.modifyNickname(member.getUser().getName()).queue();
		member.mute(true).queue();
	}
	
	public void revive() {
		alive = true;
		member.getGuild().addRoleToMember(member, game.getDiscordRole("ingame")).queue();
		
		member.mute(false).queue();
	}
}
