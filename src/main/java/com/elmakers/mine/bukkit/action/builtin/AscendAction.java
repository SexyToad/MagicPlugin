package com.elmakers.mine.bukkit.action.builtin;

import com.elmakers.mine.bukkit.action.BaseSpellAction;
import com.elmakers.mine.bukkit.api.action.CastContext;
import com.elmakers.mine.bukkit.api.spell.SpellResult;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class AscendAction extends BaseSpellAction
{
    @Override
    public SpellResult perform(CastContext context)
	{
        Entity entity = context.getEntity();
        if (entity == null) {
            return SpellResult.ENTITY_REQUIRED;
        }
		Location targetLocation = context.getLocation();
		for (int i = 0; i < 2; i++) {
			if (!context.allowPassThrough(targetLocation.getBlock().getType())) return SpellResult.NO_TARGET;
			targetLocation.setY(targetLocation.getY() + 1);
		}
		Location location = context.findPlaceToStand(targetLocation, true);
		if (location != null) 
		{
            context.teleport(entity, location);
			return SpellResult.CAST;
		}
		return SpellResult.NO_TARGET;
	}
}