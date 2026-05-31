/**
 * 
 */
package server.model.npcs.combat;

import java.util.Map;
import java.util.Objects;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import server.Config;
import server.model.npcs.NPCDefinitions;
import server.util.Misc;

/**
 * @author ReverendDread
 * Mar 9, 2019
 */
@Slf4j
public class CombatScriptHandler {

	@Getter
	private static final Map<Object, CombatScript> scripts = Maps.newConcurrentMap();

	/**
	 * Initializes the combat scripts.
	 */
	@SuppressWarnings("deprecation")
	public static void init() {
		try {
			Misc.getClasses("server.model.npcs.combat.impl")
			.stream()
			.filter(Objects::nonNull)
			.filter(c -> !c.isAnonymousClass())
			//.filter(c -> c.equals(CombatScript.class))
			.forEach(c -> {
				if(!c.isAnnotationPresent(ScriptSettings.class)) {
					System.out.println("{} is missing ScriptSettings annotation!"+c.getCanonicalName());
					return;
				}				
				try {
					CombatScript script = (CombatScript) c.newInstance();
					ScriptSettings settings = script.getClass().getAnnotation(ScriptSettings.class);

					//log.info("Loading {}", script.getClass().getName());
					
					for (String key : settings.npcNames())
						scripts.put(key.toUpperCase(), script);

					for (int key : settings.npcIds())
						scripts.put(key, script);
					
				} catch(Exception ex) {
					System.out.println("Failed to initialize "+ c.getName());
				}
			});

			//log.info("Loaded " + scripts.size() + " combat scripts.");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets a combat script for an npc.
	 * @param npc
	 * 			the npc.
	 * @return
	 */
	public static CombatScript getScript(final NPCDefinitions definition) {
		CombatScript script = null;
		if(definition != null) {
			script = scripts.get(definition.getNpcId());
			if (script == null && definition.getNpcName() != null) {
				script = scripts.get(definition.getNpcName().toUpperCase());
				if(script != null) {
				}
			}
		}
		if(script != null) {
			try {
				return script.newInstance();
			} catch (IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
			return null;
		}
		return script;
	}

}
