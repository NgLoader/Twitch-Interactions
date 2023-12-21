package de.ngloader.twitchinteractions.action.type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.Action;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionRickRoll extends Action implements Runnable {

	private BukkitTask currentTask;

	private int procedure = 0;

	private TextComponent[] rickRollText;
	private int currentLine = 0;

	public ActionRickRoll(TIPlugin plugin) {
		super(plugin);

		this.readRickRollText();
	}

	public void readRickRollText() {
		try (InputStream inputStream = ActionRickRoll.class.getResourceAsStream("/nevergonnagiveyouup.txt");
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
			this.rickRollText = bufferedReader.lines()
					.filter(line -> line != null)
					.filter(line -> !line.isBlank())
					.map(line -> new ComponentBuilder()
							.append(line)
							.color(ChatColor.GRAY)
							.italic(true)
							.create()[0])
					.toArray(TextComponent[]::new);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onEnable() {
		if (this.currentTask == null) {
			this.currentTask = Bukkit.getScheduler().runTaskTimer(this.getPlugin(), this, 5, 3);
		}
	}

	@Override
	protected void onDisable() {
		if (this.currentTask != null) {
			this.currentTask.cancel();
			this.currentTask = null;
		}
	}

	@Override
	protected void onPlayerEnter(Player player) {
	}

	@Override
	protected void onPlayerLeave(Player player) {
	}

	@Override
	public void run() {
		if (this.rickRollText != null && this.procedure % 20 == 0) {
			TextComponent text = this.rickRollText[this.currentLine];
			for (Player player : this.getActivePlayers()) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
			}

			this.currentLine++;
			if (this.rickRollText.length <= this.currentLine) {
				this.currentLine = 0;
			}
		}
		
		/**
		 * Rickroll sounds from:
		 * Source: https://github.com/iangry0/TrollingFreedom/blob/main/src/main/java/me/iangry/trollingfreedom/commands/RickRoll.java
		 */
		switch (this.procedure % 56) {
		case 0:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 2:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 3:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.G));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 4:
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.A));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 5:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.G));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.C));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.C));
			break;
		case 6:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.A));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 8:
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.F));
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.C));
			this.playSound(Instrument.FLUTE, Note.natural(0, Tone.F));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 10:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 11:
			this.playSound(Instrument.BASS_DRUM, Note.sharp(0, Tone.D));
			this.playSound(Instrument.FLUTE, Note.natural(0, Tone.F));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 12:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 13:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.C));
			break;
		case 14:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.D));
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.C));
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.G));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.D));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 15:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 16:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 18:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 19:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 20:
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 21:
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			break;
		case 22:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.G));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 23:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.G));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 24:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.D));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 26:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 27:
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 28:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 29:
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.A));
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.PIANO, Note.natural(1, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 30:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 31:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 32:
			this.playSound(Instrument.FLUTE, Note.natural(0, Tone.C));
			break;
		case 33:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 34:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 35:
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.G));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 36:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.C));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			break;
		case 37:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.PIANO, Note.natural(1, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.C));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 38:
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.C));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			break;
		case 39:
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.F));
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 40:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(1, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 41:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 42:
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 43:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.C));
			break;
		case 44:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.D));
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.C));
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.A));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.D));
			this.playSound(Instrument.FLUTE, Note.natural(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 45:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 46:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 47:
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.A));
			break;
		case 48:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.G));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 49:
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 50:
			this.playSound(Instrument.PIANO, Note.sharp(1, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		case 51:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.G));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.sharp(0, Tone.G));
			break;
		case 52:
			this.playSound(Instrument.BASS_GUITAR, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 53:
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.G));
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.D));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.BASS_DRUM, Note.natural(0, Tone.C));
			break;
		case 54:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			break;
		case 55:
			this.playSound(Instrument.BASS_GUITAR, Note.natural(0, Tone.F));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.STICKS, Note.natural(0, Tone.D));
			break;
		case 56:
			this.playSound(Instrument.PIANO, Note.natural(0, Tone.F));
			this.playSound(Instrument.PIANO, Note.sharp(0, Tone.C));
			this.playSound(Instrument.FLUTE, Note.sharp(0, Tone.C));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(1, Tone.E));
			this.playSound(Instrument.SNARE_DRUM, Note.natural(0, Tone.D));
			break;
		}

		this.procedure++;
		if (this.procedure == Integer.MAX_VALUE) {
			this.procedure = 0;
		}
	}

	public void playSound(Instrument instrument, Note note) {
		for (Player player : this.getActivePlayers()) {
			player.playNote(player.getLocation(), instrument, note);
		}
	}
}
