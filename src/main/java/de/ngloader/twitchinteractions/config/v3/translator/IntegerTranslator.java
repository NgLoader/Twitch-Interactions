package de.ngloader.twitchinteractions.config.v3.translator;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.ngloader.twitchinteractions.config.v3.SimpleTranslator;
import de.ngloader.twitchinteractions.config.v3.SimpleTranslatorKey;
import de.ngloader.twitchinteractions.config.v3.require.SimpleInteger;

public class IntegerTranslator implements SimpleTranslator<Integer, SimpleInteger> {

	@Override
	public void serialize(ConfigurationSection config, SimpleTranslatorKey key, Integer value) {
		config.set(key.name(), value);
	}

	@Override
	public Integer deserialize(ConfigurationSection config, SimpleTranslatorKey key, Integer defaultValue) {
		return config.getInt(key.name(), defaultValue);
	}

	@Override
	public Integer requirement(SimpleTranslatorKey key, Integer value, SimpleInteger requirement) {
		return this.getOrDefault(requirement, value);
	}

	@Override
	public Integer defaultValue(SimpleTranslatorKey key, Integer initialValue, SimpleInteger requirement) {
		return requirement != null ? this.getOrDefault(requirement, null) : initialValue;
	}

	public int getOrDefault(SimpleInteger requirement, Integer value) {
		if (value == null) {
			value = requirement.defaultValue();
		}

		int minValue = requirement.min();
		int maxValue = requirement.max();
		return value >= minValue ? value <= maxValue ? value : maxValue : minValue;
	}

	@Override
	public Class<SimpleInteger> require() {
		return SimpleInteger.class;
	}

	@Override
	public List<Class<? extends Integer>> types() {
		return List.of(Integer.class, int.class);
	}
}