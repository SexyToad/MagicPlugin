package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.api.action.EntityAction;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import com.elmakers.mine.bukkit.spell.BaseSpellAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class IgniteAction extends BaseSpellAction implements EntityAction
{
	@Override
	public SpellResult perform(ConfigurationSection parameters, Entity entity)
	{
        int duration = parameters.getInt("duration", 5000);
        int ticks = duration * 20 / 1000;

        registerModified(entity);
        entity.setFireTicks(ticks);

		return SpellResult.CAST;
	}

	@Override
	public boolean isUndoable()
	{
		return true;
	}
}