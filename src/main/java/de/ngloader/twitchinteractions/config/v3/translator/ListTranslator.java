package de.ngloader.twitchinteractions.config.v3.translator;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import de.ngloader.twitchinteractions.config.v3.SimpleTranslator;
import de.ngloader.twitchinteractions.config.v3.SimpleTranslatorKey;

@SuppressWarnings("rawtypes")
public class ListTranslator implements SimpleTranslator<List, Annotation> {

	@Override
	public void serialize(ConfigurationSection config, SimpleTranslatorKey key, List value) {
		config.set(key.name(), value);
	}

	@Override
	public List<?> deserialize(ConfigurationSection config, SimpleTranslatorKey key, List defaultValue) {
		return config.getList(key.name(), defaultValue);
	}

	@Override
	public List<?> defaultValue(SimpleTranslatorKey key, List initialValue, Annotation requirement) {
		return initialValue != null ? initialValue : new ArrayList<>();
	}

	@Override
	public List<Class<? extends List>> types() {
		return List.of(List.class);
	}
}