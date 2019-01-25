package xyz.destr.survival.client;

import xyz.destr.survival.io.Properties;
import xyz.destr.survival.io.action.ActorActionWriter;
import xyz.destr.survival.io.view.ActorTypeView;
import xyz.destr.survival.io.view.ActorView;
import xyz.destr.survival.io.view.GameView;

public interface ActorListener {
	
	public void create(Properties propertys);
	
	public void process(GameView game, ActorTypeView actorType, ActorView actor, ActorActionWriter action);

}
