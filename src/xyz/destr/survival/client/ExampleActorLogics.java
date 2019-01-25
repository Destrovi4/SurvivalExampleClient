package xyz.destr.survival.client;

import java.util.Random;

import xyz.destr.survival.io.Properties;
import xyz.destr.survival.io.action.ActorActionWriter;
import xyz.destr.survival.io.view.ActorTypeView;
import xyz.destr.survival.io.view.ActorView;
import xyz.destr.survival.io.view.GameView;

public class ExampleActorLogics implements ActorListener {
	
	protected Random random = new Random();
	
	@Override
	public void create(Properties propertys) {
		propertys.setString("name", "Живчики");
		propertys.setFloat("size", 1.2f);
	}
	
	@Override
	public void process(GameView game, ActorTypeView actorType, ActorView actor, ActorActionWriter action) {
		if(random.nextBoolean()) {
			action.move(random.nextBoolean() ? 1 : -1, 0);
		} else {
			action.move(0, random.nextBoolean() ? 1 : -1);
		}
		
		action.eatTileObject();
	}
	
}
