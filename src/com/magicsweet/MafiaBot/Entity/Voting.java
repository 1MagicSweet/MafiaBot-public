package com.magicsweet.MafiaBot.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.magicsweet.MafiaBot.Entity.Language.Language;
import com.magicsweet.MafiaBot.Event.GameManager;
import com.magicsweet.MafiaBot.Main.Main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Voting {
	TextChannel channel;
	HashMap<Member, Integer> players;
	Game game;
	Message message;
	
	public Voting(GuildChannel channel, List<Player> playerList) {
		this.channel = (TextChannel) channel;
		
		this.game = GameManager.getGameByChannel(channel);
		
		players = new HashMap<Member, Integer>();
		for (Player player : playerList) {
			if (player.alive) this.players.put(player.getMember(), -1);
		}
	}

	public Voting start() {
		Main.jda.addEventListener(this);
		TextChannel channel = (TextChannel) game.getChannel("control");
		message = channel.sendMessage(buildVoteEmbed()).complete();
		
		message.addReaction("U+2705").queue();
		openDiscordChannel();
		return this;
	}
	
	void openDiscordChannel() {
		game.getChannel("voting").getPermissionOverride(game.getDiscordRole("ingame")).getManager().grant(Permission.MESSAGE_WRITE).complete();
		TextChannel voting = (TextChannel) game.getChannel("voting");
		
		voting.sendMessage(game.getSettings().getLanguage().getString("voting-started")).queue();
	}
	
	void closeDiscordChannel() {
		game.getChannel("voting").getPermissionOverride(game.getDiscordRole("ingame")).getManager().deny(Permission.MESSAGE_WRITE).complete();
		TextChannel voting = (TextChannel) game.getChannel("voting");
		
		voting.sendMessage(game.getSettings().getLanguage().getString("voting-over")).queue();
	}
	
	public void finish() {
		Main.jda.removeEventListener(this);
		closeDiscordChannel();
	}
	
	private MessageEmbed buildVoteEmbed() {
		Language lang = game.getSettings().getLanguage();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(lang.getString("ingame-role-channel-name").replace("[game_id]", game.getId()), null, game.getModeratorAsMember().getUser().getEffectiveAvatarUrl());
		builder.setTitle(lang.getString("voting-embed-title"));
		builder.setDescription(generateVotelist());
		builder.addField(generateVoteField());
		
		return builder.build();
	}
	
	private MessageEmbed buildVoteEndEmbed() {
		Language lang = game.getSettings().getLanguage();
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setAuthor(lang.getString("ingame-role-channel-name").replace("[game_id]", game.getId()), null, game.getModeratorAsMember().getUser().getEffectiveAvatarUrl());
		builder.setTitle(lang.getString("voting-embed-title"));
		builder.setDescription(generateVotelist());
		builder.addField(generateVoteEndField());
		
		return builder.build();
	}
	
	private void update(MessageEmbed embed) {
		message.editMessage(embed).queue();
	}
	
	private Field generateVoteField() {
		Language lang = game.getSettings().getLanguage();
		
		StringBuilder string = new StringBuilder();
		players.forEach((member, vote) -> {
			if (vote == -1) {
				string.append("**" + member.getUser().getName() + "**" + ", ");
			} else {
				string.append(member.getUser().getName() + ", ");
			}
		});
		
		string.replace(string.length() - 2, string.length(), "");
		
		return new Field(lang.getString("voting-embed-vote-members"), string.toString(), false);
	}
	
	private Member temp = null;
	int highestVoteTemp = 0;
	private Field generateVoteEndField() {
		Language lang = game.getSettings().getLanguage();
		HashMap<Member, List<Member>> votes = getVotingResults();
		


		List<Member> list = new ArrayList<Member>();
		
		votes.forEach((member, memlist) -> {
			if (memlist.size() != highestVoteTemp) {
				if (memlist.size() > highestVoteTemp) {
					temp = member;
					highestVoteTemp = memlist.size();	
				}
			} else {
				if (!list.contains(temp)) list.add(temp);
				if (!list.contains(member)) list.add(member);
				
			}
		});
		
		
		String string;
		if (list.isEmpty()) {
			string = lang.getString("voting-finished-embed-field-desc-death").replace("[player]", "**" + temp.getNickname() + "**");
		} else {
			StringBuilder builder = new StringBuilder();
			
			for (Member mem : list) {
				builder.append("**" + mem.getNickname() + "**" + ", ");
			}
			builder.replace(builder.length() - 2, builder.length(), "");
			
			string = lang.getString("voting-finished-embed-field-desc-equal").replace("[players]", builder.toString());
		}
		
		temp = null;
		highestVoteTemp = 0;
		return new Field(lang.getString("voting-finished-embed-field-name"), string, false);
	}
	
	private String generateVotelist() {
		HashMap<Member, List<Member>> members = new HashMap<>();
		
		players.forEach((member, vote) -> {
			if (vote != -1) {
				List<Member> players = game.getPlayersAsMembers();
				if (members.get(players.get(vote - 1)) == null) {
					members.put(players.get(vote - 1), new ArrayList<>());
				}
				
				members.get(players.get(vote - 1)).add(member);
			}
		});
		
		StringBuilder string = new StringBuilder();
		members.forEach((member, whoVoted) -> { 
			string.append(member.getNickname() + ": " + whoVoted.size() + "\n");
		});
		
		
		return string.toString();
	}
	
	public HashMap<Member, List<Member>> getVotingResults() {
		HashMap<Member, List<Member>> members = new HashMap<>();
		
		players.forEach((member, vote) -> {
			if (vote != -1) {
				List<Member> players = game.getPlayersAsMembers();
				if (members.get(players.get(vote - 1)) == null) {
					members.put(players.get(vote - 1), new ArrayList<>());
				}
				
				members.get(players.get(vote - 1)).add(member);
			}
		});
		
		return members;
	}
	
	@SubscribeEvent
	private void onMessage(GuildMessageReceivedEvent event) {
		if (!event.getChannel().equals(channel)) return;
		if (players.get(event.getMember()) == null) return;
		
		if (players.get(event.getMember()) == -1) {
			try {
				if (!channel.equals(event.getChannel())) return;
				if (!players.containsKey(event.getMember())) return;
				
				if (Integer.parseInt(event.getMessage().getContentRaw()) > game.getPlayers().size()) {
					event.getMessage().addReaction("U+274C").queue();
				} else {
					players.put(event.getMember(), Integer.parseInt(event.getMessage().getContentRaw()));
				}
				
			} catch (NumberFormatException e) {
				event.getMessage().addReaction("U+274C").queue();
			}
		} else {
			event.getMessage().addReaction("U+1F504").queue();
			pendingRepeatMessages.add(event.getMessage());
		}
		
		update(buildVoteEmbed());
	}
	
	List<Message> pendingRepeatMessages = new ArrayList<>();
	@SubscribeEvent
	public void onEmote(GuildMessageReactionAddEvent event) throws IllegalStateException {
		Message message = null;
		
		for (Message msg : pendingRepeatMessages) {
			if (msg.getId().equals(event.getMessageId())) message = msg;
		}

		if (event.getMember().getUser().equals(Main.jda.getSelfUser())) return;
		try {
			if (!event.getReactionEmote().getAsCodepoints().toUpperCase().equals("U+1F504")) return;
		} catch (IllegalStateException e) {
			return;
		}
		if (!game.getModeratorAsMember().equals(event.getMember())) return;
		if (message == null) return;
		
		players.put(message.getMember(), Integer.parseInt(message.getContentRaw()));
		update(buildVoteEmbed());
		
		message.addReaction("U+2705").queue();
		
		pendingRepeatMessages.remove(message);
	}
	
	@SubscribeEvent
	public void onFinishEmote(GuildMessageReactionAddEvent event) {
		if (event.getMember().getUser().equals(Main.jda.getSelfUser())) return;
		if (!message.getId().equals(event.getMessageId())) return;
		if (!game.getModeratorAsMember().equals(event.getMember())) return;
		if (!event.getReactionEmote().getAsCodepoints().toUpperCase().equals("U+2705")) return;
		
		update(buildVoteEndEmbed());
		finish();
		
		message.removeReaction("U+2705").queue();
		message.removeReaction("U+2705", game.getModeratorAsMember().getUser()).queue();
		
	}
}
