package com.magicsweet.MafiaBot.Command.Entity;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.JDA;

public class Command {
	List<CommandPermission> permissions = new ArrayList<>();
	
	public Command(JDA jda) {
		jda.addEventListener(this);
	}
	
}
