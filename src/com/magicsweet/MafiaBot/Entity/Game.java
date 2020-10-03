package com.magicsweet.MafiaBot.Entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Main.Main;
import com.magicsweet.MafiaBot.Util.RandomNumber;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;


public class Game {
	String gameId;
	GameSettings settings;
	HashMap<String, GuildChannel> channels = new HashMap<String, GuildChannel>();
	HashMap<String, net.dv8tion.jda.api.entities.Role> discordRoles = new HashMap<String, net.dv8tion.jda.api.entities.Role>();
	List<Player> players = new ArrayList<Player>();
	Moderator moderator;
	Voting voting;
	TextChannel startChannel;
	
	public Game(Moderator moderator, Collection<Member> players, GameSettings settings, TextChannel startChannel) throws InterruptedException {
		this.startChannel = startChannel;
		GameControl.games.add(this);
		
		List<Member> members = players.stream().collect(Collectors.toList());
		Role[] roleArray = settings.getRoles().stream().collect(Collectors.toList()).toArray(new Role[settings.getRoles().stream().collect(Collectors.toList()).size()]);
		List<Integer> intTemp = new RandomNumber().generateInts(0, players.size() - 1, players.size()).stream().collect(Collectors.toList());
		Integer[] integers = intTemp.toArray(new Integer[intTemp.size()]);
		for (int t = 0; t < players.size(); t++) {
			this.players.add(new Player(members.get(t), this, roleArray[integers[t]]));
		}
		
		MessageBuilder msg = new MessageBuilder();
		msg.append(settings.getLanguage().getString("game-started").replace("[roles]", ""));
		for (Role r : roleArray) {
			msg.append(r.getName() + ", ");
		}
		msg.replaceLast(", ", ".");
		
		Collections.shuffle(this.players);
		this.settings = settings;
		this.moderator = moderator;
		gameId = generateGameID();
		prepareChannels(moderator.getMember().getGuild());
		
		((TextChannel)channels.get("game-chat")).sendMessage(msg.build()).queue();
		
		moveWholeGameToVoice((VoiceChannel) channels.get("game-voice"));
		sendPlayersTheirRoles();
		nameAndPermPlayers();
		tellRolesToTextChannel((TextChannel) channels.get("control"));
		
		if (GameControl.games.size() == 1) {
			Main.jda.getPresence().setActivity(Activity.playing("Mafia in " + startChannel.getGuild().getName()));
		} else {
			Main.jda.getPresence().setActivity(Activity.playing(GameControl.games.size() + " games of Mafia"));
		}
		System.out.println("Game " + gameId + " started!");
		
	}
	
	String generateGameID() {
		StringBuffer ID = new StringBuffer();

		while (ID.length() < 16) {
			if (new RandomNumber().generateInt(1, 100) >= 50) {
				ID.append((char)(new Random().nextInt(26) + 'a'));
			} else {
				ID.append(new RandomNumber().generateInt(0, 9));
			}
		}
		return ID.toString();
	}
	
	public void startVoting() {
		voting = new Voting(channels.get("voting"), players);
		voting.start();
	}
	
	public void finishVoting() {
		voting.finish();
	}
	
	public void delete() {
		for (String str : discordRoles.keySet()) {
			discordRoles.get(str).delete().complete();
		}
		discordRoles.clear();
		for (String str : channels.keySet()) {
			channels.get(str).delete().complete();
		}
		channels.clear();
		
		MessageBuilder msg = new MessageBuilder(settings.getLanguage().getString("roles-in-this-game"));
		for (Player pl : players) {
			if (!pl.getRole().getType().equals(RoleType.INNOCENT)) {
				msg.append((players.indexOf(pl) + 1) + " " + pl.getMember().getUser().getName() + ": " + pl.getRole().getName() + "\n");
			}
		}
		
		startChannel.sendMessage(msg.build()).queue();
		GameControl.games.remove(this);
		
		if (GameControl.games.size() == 0) {
			Main.jda.getPresence().setActivity(null);
		} else if (GameControl.games.size() == 1) {
			Main.jda.getPresence().setActivity(Activity.playing("Mafia in " + startChannel.getGuild().getName()));
		} else {
			Main.jda.getPresence().setActivity(Activity.playing(GameControl.games.size() + " games of Mafia"));
		}
		
		System.out.println("Game " + gameId + " finished!");
	}
	
	void prepareChannels(Guild guild) {
		//Roles
		discordRoles.put("ingame", guild.createRole().setName(new String(settings.getLanguage().getString("ingame-role-channel-name").replace("[game_id]", gameId))).setColor(new Color(30, 130, 10)).complete());
		discordRoles.put("spectator", guild.createRole().setName(new String(settings.getLanguage().getString("spectating-role-channel-name").replace("[game_id]", gameId))).complete());
		discordRoles.put("moderator", guild.createRole().setName(new String(settings.getLanguage().getString("moderator-role-channel-name").replace("[game_id]", gameId))).setColor(new Color(220, 200, 0)).complete());
		
		String channelName;
		//Channels
		Category cat = guild.createCategory(new String(settings.getLanguage().getString("category-channel-name").replace("[id]", gameId))).complete();
		channels.put("category", cat);
		
		channelName = "yakuzas-chat";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("yakuzas-chat-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("moderator")).grant(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).grant(Permission.MANAGE_ROLES).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).complete();
		
		channelName = "mafia-chat";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("mafia-chat-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("moderator")).grant(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).grant(Permission.MANAGE_ROLES).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).complete();
		
		channelName = "control";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("control-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(discordRoles.get("moderator")).grant(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		
		channelName = "moderator-chats";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("moderator-chats-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(discordRoles.get("moderator")).grant(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_READ).grant(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		
		channelName = "game-chat";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("game-chat-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("ingame")).grant(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		
		channelName = "voting";
		channels.put(channelName, cat.createTextChannel(settings.getLanguage().getString("voting-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.MESSAGE_WRITE).deny(Permission.MESSAGE_ADD_REACTION).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("ingame")).deny(Permission.MESSAGE_WRITE).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("moderator")).grant(Permission.MESSAGE_WRITE).grant(Permission.MESSAGE_ADD_REACTION).complete();
		channels.get(channelName).createPermissionOverride(discordRoles.get("spectator")).grant(Permission.MESSAGE_READ).deny(Permission.MESSAGE_WRITE).complete();
		
		channelName = "game-voice";
		channels.put(channelName, cat.createVoiceChannel(settings.getLanguage().getString("game-voice-channel-name")).complete());
		channels.get(channelName).createPermissionOverride(guild.getPublicRole()).deny(Permission.VOICE_CONNECT).deny(Permission.VOICE_STREAM).complete();
		
		channels.put("spectators", cat.createVoiceChannel(settings.getLanguage().getString("spectators-channel-name")).complete());
		channels.put("join-to-spectate", cat.createVoiceChannel(settings.getLanguage().getString("join-to-spectate-channel-name")).complete());
		//TODO "try" separated
		
	}
	
	public GameSettings getSettings() {
		return settings;
	}
	
	void moveWholeGameToVoice(VoiceChannel channel) throws InterruptedException {
		Guild server = channel.getGuild();
		for (Player pl : players) {
			try {
			server.moveVoiceMember(pl.getMember(), channel).queue();
			TimeUnit.MILLISECONDS.sleep(700);
			} catch (HierarchyException e) {
			}
			try {
			server.moveVoiceMember(moderator.getMember(), channel).queue();
			TimeUnit.MILLISECONDS.sleep(700);
			} catch (HierarchyException e) {
			}
		}
	}
	void nameAndPermPlayers() {
		@SuppressWarnings("unused")
		Member failedMember = null;
		for (Player pl : players) {
			if (!pl.getMember().isOwner()) {
				if (players.indexOf(pl) + 1 < 10) {
					pl.getMember().modifyNickname("0"+ (players.indexOf(pl) + 1) + " " + pl.getMember().getUser().getName()).queue();
				} else {
					pl.getMember().modifyNickname((players.indexOf(pl)+1) + " " + pl.getMember().getUser().getName()).queue();
				}
				pl.getMember().getGuild().addRoleToMember(pl.getMember(), discordRoles.get("ingame")).queue();
			} else {
				failedMember = pl.getMember();
			}

		}

		if (!moderator.getMember().isOwner()) {
			moderator.getMember().getGuild().addRoleToMember(moderator.getMember(), discordRoles.get("moderator")).queue();
			moderator.getMember().modifyNickname(settings.getLanguage().getString("moderator-name-channel-name")).queue();
		} else {
		failedMember = moderator.getMember();
		}
		}
	
	void sendPlayersTheirRoles() {
		for (Player pl : players) {
			pl.getMember().getUser().openPrivateChannel().complete().sendMessage(new String(settings.getLanguage().getString("your-role-start").replace("[role]", pl.getRole().getName()))).queue();
			if (pl.getRole().getType().equals(RoleType.MAFIA) || pl.getRole().getType().equals(RoleType.MAFIA_DON)) {
				channels.get("mafia-chat").createPermissionOverride(pl.getMember()).grant(Permission.MESSAGE_WRITE).grant(Permission.MESSAGE_READ).complete();

			} else if (pl.getRole().getType().equals(RoleType.YAKUZA)) {
				channels.get("yakuzas-chat").createPermissionOverride(pl.getMember()).grant(Permission.MESSAGE_WRITE).complete();
			}
		}

	}
	void tellRolesToTextChannel(TextChannel channel) {
		MessageBuilder msg = new MessageBuilder(settings.getLanguage().getString("roles-in-this-game"));
		for (Player pl : players) {
			if (!pl.getRole().getType().equals(RoleType.INNOCENT)) {
				msg.append((players.indexOf(pl) + 1) + " " + pl.getMember().getUser().getName() + ": " + pl.getRole().getName() + "\n");
			}
		}
		channel.sendMessage(msg.build()).queue();
	}
	public Member getModeratorAsMember() {
		return moderator.getMember();
	}
	public GuildChannel getChannel(String id) {
		return channels.get(id);
	}
	public boolean ifGameChannel(GuildChannel channel) {
		if (channels.containsValue(channel)) return true;
		return false;
	}
	public boolean ifChannelApplicable(String channelName, GuildChannel channel) {
		if (channels.get(channelName).equals(channel)) return true;
		return false;
	}
	public List<Player> getPlayers() {
		return players;
	}
	
	public List<Member> getPlayersAsMembers() {
		List<Member> list = new ArrayList<>();
		
		players.forEach((pl) -> {
			list.add(pl.getMember());
		});
		
		return list;
	}
	
	public net.dv8tion.jda.api.entities.Role getDiscordRole(String roleId) {
		return discordRoles.get(roleId);
	}
	
	public String getId() {
		return gameId;
	}
}
