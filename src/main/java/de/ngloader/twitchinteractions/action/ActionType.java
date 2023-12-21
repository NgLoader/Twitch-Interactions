package de.ngloader.twitchinteractions.action;

import java.lang.reflect.Constructor;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.type.ActionRickRoll;
import de.ngloader.twitchinteractions.action.type.ActionSlipperyhands;
import de.ngloader.twitchinteractions.action.type.ActionToxicRain;

public enum ActionType {

	TOXICRAIN(ActionToxicRain.class),
	SLIPPERYHANDS(ActionSlipperyhands.class),
	RICKROLL(ActionRickRoll.class);

	private final Class<? extends Action> actionClass;

	private ActionType(Class<? extends Action> actionClass) {
		this.actionClass = actionClass;
	}

	public Action newInstance(TIPlugin plugin) throws Exception {
		try {	
			Constructor<? extends Action> constructor = this.actionClass.getConstructor(TIPlugin.class);
			return constructor.newInstance(plugin);
		} catch (Exception e) {
			throw e;
		}
	}

	public Class<? extends Action> getActionClass() {
		return this.actionClass;
	}
}
