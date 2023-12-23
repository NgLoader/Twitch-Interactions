package de.ngloader.twitchinteractions.util;

import java.util.List;

import org.bukkit.entity.Player;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;

public class WeatherUtil {

	/*
	 * Rain values by thunder 80 (sun is in the middle)
	 * DARK: 0.065
	 * DARK BLUE: 0.193
	 * 
	 * BLUE: 0.2
	 * CYAN: 0.3
	 * 
	 * BloodRain:
	 *   - Rain 3
	 *   - Thunder 180
	 */

	public static ClientboundGameEventPacket createRainPacket(float rainStrength) {
		return new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, rainStrength);
	}

	public static ClientboundGameEventPacket createThunderPacket(float thunderStrength) {
		return new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, thunderStrength);
	}

	public static void updateWeatherStrength(float thunderStrength, ClientboundGameEventPacket rainPacket, Player... players) {
		WeatherUtil.updateWeatherStrength(WeatherUtil.createThunderPacket(thunderStrength), rainPacket, players);
	}
	public static void updateWeatherStrength(float thunderStrength, ClientboundGameEventPacket rainPacket, List<Player> players) {
		WeatherUtil.updateWeatherStrength(WeatherUtil.createThunderPacket(thunderStrength), rainPacket, players);
	}

	public static void updateWeatherStrength(ClientboundGameEventPacket thunderPacket, float rainStrength, Player... players) {
		WeatherUtil.updateWeatherStrength(thunderPacket, WeatherUtil.createRainPacket(rainStrength), players);
	}

	public static void updateWeatherStrength(ClientboundGameEventPacket thunderPacket, float rainStrength, List<Player> players) {
		WeatherUtil.updateWeatherStrength(thunderPacket, WeatherUtil.createRainPacket(rainStrength), players);
	}

	public static void updateWeatherStrength(float thunderStrength, float rainStrength, Player... players) {
		WeatherUtil.updateWeatherStrength(WeatherUtil.createThunderPacket(thunderStrength), WeatherUtil.createRainPacket(rainStrength), players);
	}

	public static void updateWeatherStrength(float thunderStrength, float rainStrength, List<Player> players) {
		WeatherUtil.updateWeatherStrength(WeatherUtil.createThunderPacket(thunderStrength), WeatherUtil.createRainPacket(rainStrength), players);
	}

	public static void updateWeatherStrength(ClientboundGameEventPacket thunderPacket, ClientboundGameEventPacket rainPacket, Player... players) {
		CraftBukkitUtil.sendPacket(players, rainPacket, thunderPacket);
	}

	public static void updateWeatherStrength(ClientboundGameEventPacket thunderPacket, ClientboundGameEventPacket rainPacket, List<Player> players) {
		CraftBukkitUtil.sendPacket(players, rainPacket, thunderPacket);
	}

	private ClientboundGameEventPacket rainPacket;
	private ClientboundGameEventPacket thunderPacket;

	private float rainStrength;
	private float thunderStrength;

	public WeatherUtil() { }

	public WeatherUtil(float rainStrength, float thunderStrength) {
		this.setRainStrength(rainStrength);
		this.setThunderStrength(thunderStrength);
	}

	public void setRainStrength(float rainStrength) {
		this.rainStrength = rainStrength;
		this.rainPacket = WeatherUtil.createRainPacket(this.rainStrength);
	}

	public void setThunderStrength(float thunderStrength) {
		this.thunderStrength = thunderStrength;
		this.thunderPacket = WeatherUtil.createThunderPacket(this.thunderStrength);
	}

	public void addRainStrength(float rainStrength) {
		this.setRainStrength(this.rainStrength + rainStrength);
	}

	public void addThunderStrength(float thunderStrength) {
		this.setThunderStrength(this.thunderStrength + thunderStrength);
	}

	public float getRainStrength() {
		return this.rainStrength;
	}

	public float getThunderStrength() {
		return this.thunderStrength;
	}

	public void sendRainStrength(Player... players) {
		CraftBukkitUtil.sendPacket(players, this.rainPacket, this.thunderPacket);
	}

	public void sendRainStrength(List<Player> players) {
		CraftBukkitUtil.sendPacket(players, this.rainPacket, this.thunderPacket);
	}

	public void resetPlayer(Player... players) {
		for (Player player : players) {
			player.resetPlayerTime();
			player.resetPlayerWeather();
		}
	}

	public void resetPlayer(List<Player> players) {
		for (Player player : players) {
			player.resetPlayerTime();
			player.resetPlayerWeather();
		}
	}
}
