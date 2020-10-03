package com.magicsweet.MafiaBot.Main;


import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.magicsweet.MafiaBot.Config.Config;
import com.magicsweet.MafiaBot.Command.GameControl;
import com.magicsweet.MafiaBot.Event.JoinToSpectateEvent;
import com.magicsweet.MafiaBot.Event.NoOnePlayingEvent;
import com.magicsweet.MafiaBot.Event.RelayMessageUsedEvent;
import com.magicsweet.MafiaBot.Event.UnmuteRequestEvent;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;

public class Main {
	
	public static final String version = "1.0.7.6";
	
	public static final String updatelog = String.join("\n",			

"- Исправлена ошибка, когда команда /mafia stop не работала, и более того, ломала бота.",
"- Теперь бот пишет у себя в статусе о том, что кто-то играет в мафию с его помощью."

);
	
	static int branch = 1;
	public static JDA jda;
	public static void main(String[] args) throws LoginException, IOException {
		JDABuilder builder = null;
		

		if (branch == 0) 
			//nightly
			builder = JDABuilder.createDefault(new Config("config.yml").getValue("nightly-token"));
		else if (branch == 1) 
			//release
			builder = JDABuilder.createDefault(new Config("config.yml").getValue("release-token"));
		
	    builder.setEventManager(new AnnotatedEventManager());
	    builder.addEventListeners(new GameControl());
	    builder.addEventListeners(new RelayMessageUsedEvent());
	    builder.addEventListeners(new NoOnePlayingEvent());
	    builder.addEventListeners(new JoinToSpectateEvent());
	    builder.addEventListeners(new UnmuteRequestEvent());
	    builder.setStatus(OnlineStatus.ONLINE);
	    jda = builder.build();
	    
	}
	
	public static final String instructions = String.join("\n", 
			"Инструкция по использованию команд Mafia Bot.",
			"",
			"**Команды, которые можно использовать только с правами:**",
			"**-** Общие:",
			"/botinsructions - показать эту инструкцию",
			"/mafia start - начать игру",
			"/mafia stop - принудительно остановить игру (не факт, что команда работает, кстати). Игра останавливается автоматически, если в голосовом канале с оной никого нет.",
			"/mafia setting - показать текущие настройки игры",
			"/mafia settings language <code> - установить язык бота на <code (напр. en_us)>",
			"/mafia settings roles <role id> <true/false/default> - установить роль <role id (ID роли)> в состояние true/false/default. true - принудительно включить, false - принудительно выключить, default - значение по умолчанию",
			"/version - показать текущую версию бота.",
			"/version ulog - показать последний список изменений.",
			"**-** Игровые (может использовать только ведущий):",
			"/chat <role id/player/clear> - открыть канал с игроком, у которого есть роль \"role id\"/с игроком под номером. clear - очистить текущий чат.",
			"/voting - начать голосование. :white_check_mark: под сообщением - завершить его, :arrows_counterclockwise: - пересчитать заново голос уже голосовавшего ранее игрока, :x: - бот не понял, что игрок имел в виду.",
			"/kick <player> - кикнуть игрока <player> из игры. После убийства на голосовании или ночью, например.",
			"/revive <player> - возродить игрока <player>. Для внештатных ситуаций.",
			"",
			"**Команды, доступные для всех:**",
			"/returnmyname - вернуть себе свой ник");
}
		
	


	


