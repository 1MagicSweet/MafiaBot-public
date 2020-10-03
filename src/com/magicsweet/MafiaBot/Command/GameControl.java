package com.magicsweet.MafiaBot.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.magicsweet.MafiaBot.Config.Config;
import com.magicsweet.MafiaBot.Check.Relays;
import com.magicsweet.MafiaBot.Entity.Game;
import com.magicsweet.MafiaBot.Entity.GameSettings;
import com.magicsweet.MafiaBot.Entity.Moderator;
import com.magicsweet.MafiaBot.Entity.Player;
import com.magicsweet.MafiaBot.Entity.RoleType;
import com.magicsweet.MafiaBot.Entity.Language.Language;
import com.magicsweet.MafiaBot.Entity.Language.LanguageRegion;
import com.magicsweet.MafiaBot.Entity.Language.LanguageType;
import com.magicsweet.MafiaBot.Event.GameManager;
import com.magicsweet.MafiaBot.Main.Main;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class GameControl {
	HashMap<Guild, GameSettings> gameSettingsList = new HashMap<Guild, GameSettings>();
	public static List<Game> games = new ArrayList<Game>();
	
	public static Member membertest = null;
	
	@SubscribeEvent
	public void main(GuildMessageReceivedEvent event) throws IOException, ContextException, InterruptedException {

		GameSettings gameSettings = null;
		if (gameSettingsList.get(event.getGuild()) == null) 
			gameSettingsList.put(event.getGuild(), new GameSettings(new Language(LanguageType.RUSSIAN, LanguageRegion.RUSSIA)));
		
			gameSettings = gameSettingsList.get(event.getGuild());
		
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;

		Message message = event.getMessage();
		if (!message.getContentRaw().startsWith("/")) return;
		String msg = message.getContentRaw().replace("/", "");
		
		
		boolean hasCommandAccess = false;
		
		if (event.getMessage().getContentRaw().equals("/returnmyname") || event.getMessage().getContentRaw().equals("/rmn")) {
			event.getMember().modifyNickname(event.getAuthor().getName()).queue();
			event.getMessage().addReaction("U+2705").queue();
			return;
		}
		
		
		for (Role role : event.getMember().getRoles()) {
			if (role.getName().equalsIgnoreCase("bot commands")) {
				hasCommandAccess = true;
				break;
			}
		}
		if (!hasCommandAccess) {
			event.getChannel().sendMessage(gameSettings.getLanguage().getString("no-perms-to-preform-command")).queue();
			return;
		}
		
		
		switch (msg) {
			case "mafia start":
				startGame(event, gameSettings);
				
				return;
			case "mafia stop":
				List<Game> gamelist = games;
				for (Game g : gamelist) {
					if (g.ifGameChannel((GuildChannel) event.getChannel())) {
						event.getChannel().sendMessage(gameSettings.getLanguage().getString("game-stopped")).queue();
						g.delete();
						games.remove(g);
						return;
					}
					
				}
				event.getChannel().sendMessage(gameSettings.getLanguage().getString("failed-to-stop-channel")).queue();
				return;
			default:
				break;
		}
		String[] cutMsg = msg.split(" ");
		//"open [int]" command
		if (cutMsg[0].equals("chat")) {
			
			if (cutMsg[1].equals("clear")) {
				for (Game g : games) {
					if (!g.ifChannelApplicable("moderator-chats", (GuildChannel) event.getChannel())) return;
					Relays.getByGuildChannel((GuildChannel) event.getChannel()).openPrivateChannel().complete().sendMessage(new String(gameSettings.getLanguage().getString("chat-channel-closed").replace("[entry]", g.getModeratorAsMember().getUser().getName()))).queue();
					event.getChannel().sendMessage(new String(gameSettings.getLanguage().getString("chat-channel-closed").replace("[entry]", Relays.getByGuildChannel((GuildChannel) event.getChannel()).getName()))).queue();
					Relays.remove((GuildChannel) event.getChannel());
				}
				return;
			} else {
				int num = 0;
				try {
					num = Integer.parseInt(cutMsg[1]) - 1;
				} catch (NumberFormatException e) {
					Game game = GameManager.getGameByChannel(event.getChannel());
					String roleId = cutMsg[1].toUpperCase();
					Player player = null;
					
					for (Player pl : game.getPlayers()) {
						if (pl.getRole().getType().equals(RoleType.valueOf(roleId))) {
						player = pl;
						break;
						}
					}
					
					num = game.getPlayers().indexOf(player);
					
				}
				
				if (!GameManager.getGameByChannel(event.getChannel()).getPlayers().get(num).alive) {
					event.getChannel().sendMessage(gameSettings.getLanguage().getString("player-is-dead")).queue();
					return;
				}
				
				for (Game g : games) {
					if (!g.ifChannelApplicable("moderator-chats", (GuildChannel) event.getChannel())) return;
					if (Relays.isRelayOpened(event.getChannel())) {
					User u = Relays.getByGuildChannel(event.getChannel());
					//
					TextChannel c = event.getChannel();
					u.openPrivateChannel().complete().sendMessage((gameSettings.getLanguage().getString("chat-channel-closed").replace("[entry]", event.getAuthor().getName()))).queue();
					c.sendMessage((gameSettings.getLanguage().getString("chat-channel-closed").replace("[entry]", u.getName()))).queue();
					Relays.remove(u);
					u = g.getPlayers().get(num).getMember().getUser();
					c = event.getChannel();
					Relays.add(u, c);
					u.openPrivateChannel().complete().sendMessage((gameSettings.getLanguage().getString("chat-channel-started").replace("[entry]", event.getAuthor().getName()))).queue();
					c.sendMessage((gameSettings.getLanguage().getString("chat-channel-started").replace("[entry]", u.getName()))).queue();
					return;
					}
					User u = g.getPlayers().get(num).getMember().getUser();
					TextChannel c = event.getChannel();
					Relays.add(u, c);
					u.openPrivateChannel().complete().sendMessage((gameSettings.getLanguage().getString("chat-channel-started").replace("[entry]", event.getAuthor().getName()))).queue();
					c.sendMessage((gameSettings.getLanguage().getString("chat-channel-started").replace("[entry]", u.getName()))).queue();
					return;
				}
				
			}
			
			
		} else if (cutMsg[0].equals("voting")) {
			
			Game game = GameManager.getGameByChannel(event.getChannel());
			if (game != null) {
				if (!game.ifChannelApplicable("control", event.getChannel())) {
					event.getChannel().sendMessage(gameSettings.getLanguage().getString("only-moderator-command-control").replace("[channel]", "#" + gameSettings.getLanguage().getString("control-channel-name"))).queue();
					return;
				}
				game.startVoting();

				event.getMessage().delete().complete();
				return;
			} else {
				event.getChannel().sendMessage(gameSettings.getLanguage().getString("only-moderator-command-control").replace("[channel]", "#" + gameSettings.getLanguage().getString("control-channel-name"))).queue();
				return;
			}

		}
		else if (cutMsg[0].equals("kick")) {
			Game game = GameManager.getGameByChannel(event.getChannel());
			if (game != null) {
				if (!game.ifChannelApplicable("control", event.getChannel())) {
					event.getChannel().sendMessage(gameSettings.getLanguage().getString("only-moderator-command-control").replace("[channel]", "#" + gameSettings.getLanguage().getString("control-channel-name"))).queue();
					return;
				}
			}
			try {
				int index = Integer.parseInt(cutMsg[1]) - 1;
				event.getMessage().delete().queue();
				game.getPlayers().get(index).kill();
				return;
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {}

		}
		else if (cutMsg[0].equals("revive")) {
			Game game = GameManager.getGameByChannel(event.getChannel());
			if (game != null) {
				if (!game.ifChannelApplicable("control", event.getChannel())) {
					event.getChannel().sendMessage(gameSettings.getLanguage().getString("only-moderator-command-control").replace("[channel]", "#" + gameSettings.getLanguage().getString("control-channel-name"))).queue();
					return;
				}
			}
			try {
				int index = Integer.parseInt(cutMsg[1]) - 1;
				event.getMessage().delete().queue();
				game.getPlayers().get(index).revive();
				return;
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {}

		}
		else if (cutMsg[0].equals("version")) {
			try {
				if (cutMsg[1].equals("ulog")) {
					event.getChannel().sendMessage(" **v" + Main.version + "**\n"
							+ Main.updatelog).queue();
					event.getMessage().delete().queue();
					return;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				event.getChannel().sendMessage("Mafia Bot **" + Main.version + "**\n"
						+ "MagicSweet (c) 2020").queue();
				return;
			}

		}
		else if (cutMsg[0].equals("botinsructions")) {
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(Main.instructions).queue();
			return;
		}
		
		else if (cutMsg[0].equals("mafia")) {
			if (cutMsg[1].equals("settings")) {
				Language l = gameSettings.getLanguage();
				if (cutMsg.length == 2) {
					MessageBuilder m = new MessageBuilder();
					
					StringBuffer roleOverride = new StringBuffer("\n");
					if (gameSettings.getRoleOverrides() != null) {
						for (String f : gameSettings.getRoleOverrides().split(",")) {
							String[] values = f.split(":");
							
							roleOverride.append(new com.magicsweet.MafiaBot.Entity.Role(RoleType.valueOf(values[0].toUpperCase()), l).getName() + ": ");
							roleOverride.append(values[1].replace("true", l.getString("on")).replace("false", l.getString("off")) + "\n");
						};
					}
					
					
					m.append(l.getString("current-settings-title") + "\n");
					m.append("\n" + l.getString("current-settings-language").replace("[language]", l.getType().getLanguageName()).replace("[region]", l.getRegion().getRegionName()) + "\n");
					m.append("\n"+ l.getString("current-settings-roles").replace("[roles]", roleOverride.toString()));
					
					event.getChannel().sendMessage(m.build()).queue();
					return;
				}
				if (cutMsg[2].equals("language")) {
					LanguageType type = null;
					LanguageRegion region = null;
						String[] str = cutMsg[3].split("_");
						for (LanguageType lt : LanguageType.values()) {
							if (lt.getCode().equals(str[0])) {
								type = lt;
							}
						}
						for (LanguageRegion lr : LanguageRegion.values()) {
							if (lr.getCode().equals(str[1])) {
								region = lr;
							}
						}
						gameSettings.setLanguage(new Language(type, region));
						event.getChannel().sendMessage((gameSettings.getLanguage().getString("language-set")).replace("[language]", gameSettings.getLanguage().getType().getLanguageName()).replace("[region]", gameSettings.getLanguage().getRegion().getRegionName())).queue();
						return;
				} else if (cutMsg[2].equals("roles")) {
					if (cutMsg[4].equals("default")) {
						gameSettings.removeRoleOverride(cutMsg[3]);
						event.getMessage().addReaction("U+2705").queue();
						return;
					}
					try {
					RoleType.valueOf(cutMsg[3].toUpperCase());
					
					gameSettings.addRoleOvverride(cutMsg[3] + ":" + Boolean.parseBoolean(cutMsg[4]));
					
					event.getMessage().addReaction("U+2705").queue();
					} catch (IllegalArgumentException e) {
						event.getMessage().addReaction("U+274C").queue();
					}
					return;
				}
			}
		} else if (cutMsg[0].equals("notify")) {
			String notificationMsg = event.getMessage().getContentRaw().replace("/notify ", "");
			TextChannel channel = null;
			List<User> users = null;
			for (GuildChannel c : event.getGuild().getChannels()) {
				if (c.getName().equals("notifications")) {
					channel = (TextChannel) c;
					break;
				}
			}
			for (Message m : channel.getHistoryFromBeginning(50).complete().getRetrievedHistory()) {
				try {
				users = m.getReactions().get(0).retrieveUsers().complete();
				final GameSettings gs = gameSettings;
				users.forEach((user) -> {
					user.openPrivateChannel().complete().sendMessage(gs.getLanguage().getString("notification-text").replace("[member]", event.getAuthor().getName()).replace("[server]", event.getGuild().getName()).replace("[text]", notificationMsg)).queue();
					
				});
				break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			event.getChannel().sendMessage(gameSettings.getLanguage().getString("notification-sent").replace("[number]", users.size() + "")).queue();
			return;
		}
		event.getChannel().sendMessage(gameSettings.getLanguage().getString("unknown-command")).queue();
	}
	void startGame(GenericEvent genericEvent, GameSettings gameSettings) throws IOException, InterruptedException {
		GuildMessageReceivedEvent event = (GuildMessageReceivedEvent) genericEvent;
		Collection<Member> playerCollection = new ArrayList<Member>();
		Config conf = new Config("config.yml");

		if (!event.getMember().getVoiceState().inVoiceChannel()) event.getChannel().sendMessage(new String(gameSettings.getLanguage().getString("you-are-not-in-voice").replace("[mention]", event.getMember().getAsMention()))).queue();
		if (!event.getMember().getVoiceState().inVoiceChannel()) return;
		Moderator moderator = null;
		
		for (Member mem : event.getGuild().getMembers()) {
			if (mem.getVoiceState().inVoiceChannel() && event.getMember().getVoiceState().getChannel().equals(mem.getVoiceState().getChannel())) {
				if (!event.getMember().equals(mem)) playerCollection.add(mem);
				if (event.getMember().equals(mem)) moderator = new Moderator(mem);
			}
		}
		
		if (playerCollection.size() < 4) event.getChannel().sendMessage(new String(gameSettings.getLanguage().getString("need-at-least-8-players-to-start"))).queue();
		if (playerCollection.size() < 4) return;
		
		//game settings
		gameSettings.setRoles(conf.getValue("default_roles_" + playerCollection.size()), playerCollection.size());
		gameSettings.setPlayerCount(playerCollection.size());
		gameSettings.build();
		//
		
		//game
		new Game(moderator, playerCollection, gameSettings, event.getChannel());
		//
		
		
	}
	//TODO
}
