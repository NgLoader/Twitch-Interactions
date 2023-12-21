package de.ngloader.twitchinteractions.config.v3.translator;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import de.ngloader.twitchinteractions.config.v3.SimpleTranslator;
import de.ngloader.twitchinteractions.config.v3.SimpleTranslatorKey;

@SuppressWarnings("rawtypes")
public class MapTranslator implements SimpleTranslator<Map, Annotation> {

	@Override
	public void serialize(ConfigurationSection config, SimpleTranslatorKey key, Map value) {
		config.set(key.name(), value);
	}

	@Override
	public Map<?, ?> deserialize(ConfigurationSection config, SimpleTranslatorKey key, Map defaultValue) {
		ConfigurationSection section = config.getConfigurationSection(key.name());
		return section != null ? section.getValues(true) : defaultValue;
	}

	@Override
	public Map<?, ?> defaultValue(SimpleTranslatorKey key, Map initialValue, Annotation requirement) {
		return initialValue != null ? initialValue : new HashMap<>();
	}

	@Override
	public List<Class<? extends Map>> types() {
		return List.of(Map.class);
	}
}