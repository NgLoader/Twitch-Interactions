package de.ngloader.twitchinteractions.action;

import java.lang.reflect.Constructor;

import de.ngloader.twitchinteractions.TIPlugin;
import de.ngloader.twitchinteractions.action.type.ActionAnnoyingVillagerSounds;
import de.ngloader.twitchinteractions.action.type.ActionDuplicateEntityOnKill;
import de.ngloader.twitchinteractions.action.type.ActionFakeBurn;
import de.ngloader.twitchinteractions.action.type.ActionHideAllPlayers;
import de.ngloader.twitchinteractions.action.type.ActionInvertWalk;
import de.ngloader.twitchinteractions.action.type.ActionRickRoll;
import de.ngloader.twitchinteractions.action.type.ActionSlipperyhands;
import de.ngloader.twitchinteractions.action.type.ActionSpin;
import de.ngloader.twitchinteractions.action.type.ActionStarve;
import de.ngloader.twitchinteractions.action.type.ActionToxicRain;

public enum ActionType {

	TOXICRAIN(ActionToxicRain.class),
	SLIPPERYHANDS(ActionSlipperyhands.class),
	RICKROLL(ActionRickRoll.class),
	FAKEBURN(ActionFakeBurn.class),
	SPIN(ActionSpin.class),
	ANNOYING_VILLAGER_SOUNDS(ActionAnnoyingVillagerSounds.class),
	STARVE(ActionStarve.class),
	HIDE_ALL_PLAYERS(ActionHideAllPlayers.class),
	INVERT_WALK(ActionInvertWalk.class),
	DUPLICATE_ENTITY_ON_KILL(ActionDuplicateEntityOnKill.class);

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
