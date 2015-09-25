package com.ldsg.battle.event;

import java.util.List;

import com.ldsg.battle.Context;
import com.ldsg.battle.role.Hero;

public class RoundStartEvent extends BaseEvent implements IEvent {

	public RoundStartEvent(Context context, Hero hero) {
		super(context, hero);
	}

	public RoundStartEvent(Context context, List<Hero> heroList) {
		super(context, heroList);
	}

}