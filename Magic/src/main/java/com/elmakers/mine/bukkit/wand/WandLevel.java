package com.elmakers.mine.bukkit.wand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.elmakers.mine.bukkit.api.magic.MageController;
import com.elmakers.mine.bukkit.api.magic.Messages;
import com.elmakers.mine.bukkit.api.magic.Mage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import com.elmakers.mine.bukkit.api.spell.CastingCost;
import com.elmakers.mine.bukkit.api.spell.SpellTemplate;
import com.elmakers.mine.bukkit.block.MaterialBrush;
import com.elmakers.mine.bukkit.utility.RandomUtils;
import com.elmakers.mine.bukkit.utility.WeightedPair;

public class WandLevel {
    private final WandUpgradePath path;

	private LinkedList<WeightedPair<Integer>> spellCountProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Integer>> materialCountProbability = new LinkedList<>();
	private LinkedList<WeightedPair<String>> spellProbability = new LinkedList<>();
	private LinkedList<WeightedPair<String>> materialProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Integer>> useProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Integer>> addUseProbability = new LinkedList<>();

	private LinkedList<WeightedPair<Integer>> propertyCountProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> costReductionProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> powerProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionPhysicalProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionProjectilesProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionFallingProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionFireProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Float>> damageReductionExplosionsProbability = new LinkedList<>();
	
	private LinkedList<WeightedPair<Integer>> manaRegenerationProbability = new LinkedList<>();
	private LinkedList<WeightedPair<Integer>> manaMaxProbability = new LinkedList<>();

	protected WandLevel(WandUpgradePath path, MageController controller, ConfigurationSection template, int levelIndex, int nextLevelIndex, float distance) {
        this.path = path;

		// Fetch spell probabilities, and filter out invalid/unknown spells
        LinkedList<WeightedPair<String>> spells = new LinkedList<>();
		com.elmakers.mine.bukkit.utility.RandomUtils.populateStringProbabilityMap(spells, template, "spells", levelIndex, nextLevelIndex, distance);

        for (WeightedPair<String> spellValue : spells) {
            if (controller.getSpellTemplate(spellValue.getValue()) != null) {
                spellProbability.add(spellValue);
            }
        }

		// Fetch spell count probabilities
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(spellCountProbability, template, "spell_count", levelIndex, nextLevelIndex, distance);
		
		// Fetch material probabilities
		com.elmakers.mine.bukkit.utility.RandomUtils.populateStringProbabilityMap(materialProbability, template, "materials", levelIndex, nextLevelIndex, distance);
		
		// Fetch material count probabilities
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(materialCountProbability, template.getConfigurationSection("material_count"), levelIndex, nextLevelIndex, distance);
		
		// Fetch uses
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(useProbability, template, "uses", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(addUseProbability, template, "add_uses", levelIndex, nextLevelIndex, distance);
		
		// Fetch property count probability
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(propertyCountProbability, template, "property_count", levelIndex, nextLevelIndex, distance);
		
		// Fetch cost and damage reduction
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(costReductionProbability, template, "cost_reduction", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionProbability, template, "protection", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionPhysicalProbability, template, "protection_physical", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionFallingProbability, template, "protection_falling", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionProjectilesProbability, template, "protection_projectiles", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionFireProbability, template, "protection_fire", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(damageReductionExplosionsProbability, template, "protection_explosions", levelIndex, nextLevelIndex, distance);

		// Fetch regeneration
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(manaRegenerationProbability, template, "mana_regeneration", levelIndex, nextLevelIndex, distance);
		com.elmakers.mine.bukkit.utility.RandomUtils.populateIntegerProbabilityMap(manaMaxProbability, template, "mana_max", levelIndex, nextLevelIndex, distance);
		
		// Fetch power
		com.elmakers.mine.bukkit.utility.RandomUtils.populateFloatProbabilityMap(powerProbability, template, "power", levelIndex, nextLevelIndex, distance);
	}

    public void add(WandLevel other) {
        spellProbability = RandomUtils.merge(spellProbability, other.spellProbability);
        materialProbability = RandomUtils.merge(materialProbability, other.materialProbability);

        this.materialCountProbability = materialCountProbability.isEmpty() ? other.materialCountProbability : materialCountProbability;
        this.spellCountProbability = spellCountProbability.isEmpty() ? other.spellCountProbability : spellCountProbability;
        this.useProbability = useProbability.isEmpty() ? other.useProbability : useProbability;
        this.addUseProbability = addUseProbability.isEmpty() ? other.addUseProbability : addUseProbability;
        this.propertyCountProbability = propertyCountProbability.isEmpty() ? other.propertyCountProbability : propertyCountProbability;
        this.costReductionProbability = costReductionProbability.isEmpty() ? other.costReductionProbability : costReductionProbability;
        this.powerProbability = powerProbability.isEmpty() ? other.powerProbability : powerProbability;
        this.damageReductionProbability = damageReductionProbability.isEmpty() ? other.damageReductionProbability : damageReductionProbability;
        this.damageReductionPhysicalProbability = damageReductionPhysicalProbability.isEmpty() ? other.damageReductionPhysicalProbability : damageReductionPhysicalProbability;
        this.damageReductionProjectilesProbability = damageReductionProjectilesProbability.isEmpty() ? other.damageReductionProjectilesProbability : damageReductionProjectilesProbability;
        this.damageReductionFallingProbability = damageReductionFallingProbability.isEmpty() ? other.damageReductionFallingProbability : damageReductionFallingProbability;
        this.damageReductionFireProbability = damageReductionFireProbability.isEmpty() ? other.damageReductionFireProbability : damageReductionFireProbability;
        this.damageReductionExplosionsProbability = damageReductionExplosionsProbability.isEmpty() ? other.damageReductionExplosionsProbability : damageReductionExplosionsProbability;
        this.manaRegenerationProbability = manaRegenerationProbability.isEmpty() ? other.manaRegenerationProbability : manaRegenerationProbability;
        this.manaMaxProbability = manaMaxProbability.isEmpty() ? other.manaMaxProbability : manaMaxProbability;
    }

    protected void sendAddMessage(Mage mage,  Wand wand, String messageKey, String nameParam) {
        if (mage == null) return;

        String message = mage.getController().getMessages().get(messageKey)
            .replace("$name", nameParam)
            .replace("$wand", wand.getName());
        mage.sendMessage(message);
    }

    public int getSpellCount() {
        int count = 0;
        for (WeightedPair<Integer> spellCount : spellCountProbability) {
            if (spellCount.getValue() > count) {
                count = spellCount.getValue();
            }
        }

        return count;
    }

    public int getMaterialCount() {
        int count = 0;
        for (WeightedPair<Integer> materialCount : materialCountProbability) {
            if (materialCount.getValue() > count) {
                count = materialCount.getValue();
            }
        }

        return count;
    }

    public LinkedList<WeightedPair<String>> getRemainingSpells(Wand wand) {
        LinkedList<WeightedPair<String>> remainingSpells = new LinkedList<>();
        for (WeightedPair<String> spell : spellProbability) {
            if (spell.getRawThreshold() >= 1 && !wand.hasSpell(spell.getValue())) {
                remainingSpells.add(spell);
            }
        }

        return remainingSpells;
    }

    public LinkedList<WeightedPair<String>> getRemainingMaterials(Wand wand) {
        LinkedList<WeightedPair<String>> remainingMaterials = new LinkedList<>();
        for (WeightedPair<String> material : materialProbability) {
            String materialKey = material.getValue();
            // Fixup |'s to :'s .... kinda hacky, but I didn't think this through unfortunately. :\
            // TODO: escape the keys as strings with '', which is probably the right way to do it.
            materialKey = materialKey.replace("|", ":");
            if (!wand.hasBrush(materialKey) && MaterialBrush.isValidMaterial(materialKey, false)) {
                remainingMaterials.add(material);
            }
        }

        return remainingMaterials;
    }
	
	public boolean randomizeWand(Mage mage, Wand wand, boolean additive, boolean hasUpgrade, boolean addSpells) {
		// Add random spells to the wand
        if (mage == null) {
            mage = wand.getActivePlayer();
        }
        Messages messages = wand.getController().getMessages();
		boolean addedSpells = false;
        LinkedList<WeightedPair<String>> remainingSpells = getRemainingSpells(wand);

        if (addSpells) {
            SpellTemplate firstSpell = null;
            if (remainingSpells.size() > 0) {
                Integer spellCount = RandomUtils.weightedRandom(spellCountProbability);
                for (int i = 0; spellCount != null && i < spellCount; i++) {
                    String spellKey = RandomUtils.weightedRandom(remainingSpells);
                    SpellTemplate currentSpell = wand.getBaseSpell(spellKey);
                    if (wand.addSpell(spellKey)) {
                        SpellTemplate spell = wand.getMaster().getSpellTemplate(spellKey);
                        if (mage != null && spell != null) {
                            if (currentSpell != null) {
                                String levelDescription = spell.getLevelDescription();
                                if (levelDescription == null || levelDescription.isEmpty()) {
                                    levelDescription = spell.getName();
                                }
                                mage.sendMessage(messages.get("wand.spell_upgraded").replace("$name", currentSpell.getName()).replace("$wand", wand.getName()).replace("$level", levelDescription));
                                mage.sendMessage(spell.getUpgradeDescription().replace("$name", currentSpell.getName()));
                            } else {
                                mage.sendMessage(messages.get("wand.spell_added").replace("$name", spell.getName()).replace("$wand", wand.getName()));
                            }
                        }
                        if (firstSpell == null) {
                            firstSpell = spell;
                        }
                        addedSpells = true;
                    }
                }
            }
        }
		
		// Look through all spells for the max mana casting cost
		// Also look for any material-using spells
		boolean needsMaterials = false;
		int maxManaCost = 0;
		Set<String> spells = wand.getSpells();
		for (String spellName : spells) {
			SpellTemplate spell = wand.getMaster().getSpellTemplate(spellName);
			if (spell != null) {
				needsMaterials = needsMaterials || spell.usesBrush();
				Collection<CastingCost> costs = spell.getCosts();
				if (costs != null) {
					for (CastingCost cost : costs) {
						maxManaCost = Math.max(maxManaCost, cost.getMana());
					}
				}
			}
		}
		
		// Add random materials
		boolean addedMaterials = false;
        LinkedList<WeightedPair<String>> remainingMaterials = getRemainingMaterials(wand);
		if (needsMaterials && remainingMaterials.size() > 0) {
			int currentMaterialCount = wand.getBrushes().size();
			Integer materialCount = RandomUtils.weightedRandom(materialCountProbability);
			
			// Make sure the wand has at least one material.
            if (materialCount == null) {
                materialCount = 0;
            }
			if (currentMaterialCount == 0) {
				materialCount = Math.max(1, materialCount);
			}
			int retries = 100;
			for (int i = 0; i < materialCount; i++) {
				String materialKey = RandomUtils.weightedRandom(remainingMaterials);
				materialKey = materialKey.replace("|", ":");
				if (!wand.addBrush(materialKey)) {
					// Try again up to a certain number if we picked one the wand already had.
					if (retries-- > 0) i--;
				} else {
					addedMaterials = true;
                    if (mage != null)
                    {
                        String materialName = MaterialBrush.getMaterialName(messages,materialKey);
                        if (materialName == null)
                        {
                            mage.getController().getLogger().warning("Invalid material in enchanting configs: " + materialKey);
                            materialName = materialKey;
                        }

                        mage.sendMessage(messages.get("wand.brush_added").replace("$wand", wand.getName()).replace("$name", materialName));
                    }
				}
			}
		}

        // Let them upgrade if they aren't getting any new spells or brushes
        if (hasUpgrade && addSpells && !(addedMaterials && needsMaterials) && !addedSpells && ((getSpellCount() > 0 && spellProbability.size() > 0) || (getMaterialCount() > 0 && materialProbability.size() > 0)))
        {
            if (mage != null && mage.getDebugLevel() > 0) {
                mage.sendDebugMessage("Has upgrade: " + hasUpgrade);
                mage.sendDebugMessage("Added spells: " + addedSpells + " (" + addSpells + ")");
                mage.sendDebugMessage("Spells per enchant: " + getSpellCount());
                mage.sendDebugMessage("Spells in list: " + spellProbability.size());
                mage.sendDebugMessage("Added brushes: " +  addedMaterials + ", needed: " + needsMaterials);
            }
            return false;
        }
		
		// Add random wand properties
		boolean addedProperties = false;
        Integer propertyCount = propertyCountProbability.size() == 0 ? Integer.valueOf(0) : RandomUtils.weightedRandom(propertyCountProbability);
		ConfigurationSection wandProperties = new MemoryConfiguration();
		double costReduction = wand.getCostReduction();

        List<Integer> propertiesAvailable = new ArrayList<>();

        double power = wand.getPower();
        double damageReduction = wand.damageReduction;
        double damageReductionPhysical = wand.damageReductionPhysical;
        double damageReductionProjectiles = wand.damageReductionProjectiles;
        double damageReductionFalling = wand.damageReductionFalling;
        double damageReductionFire = wand.damageReductionFire;
        double damageReductionExplosions = wand.damageReductionExplosions;

        if (costReductionProbability.size() > 0 && costReduction < path.getMaxCostReduction()) {
            propertiesAvailable.add(0);
        }
        if (powerProbability.size() > 0 && power < path.getMaxPower()) {
            propertiesAvailable.add(1);
        }
        if (damageReductionProbability.size() > 0 && damageReduction < path.getMaxDamageReduction()) {
            propertiesAvailable.add(2);
        }
        if (damageReductionPhysicalProbability.size() > 0 && damageReductionPhysical < path.getMaxDamageReductionPhysical()) {
            propertiesAvailable.add(3);
        }
        if (damageReductionProjectilesProbability.size() > 0 && damageReductionProjectiles < path.getMaxDamageReductionProjectiles()) {
            propertiesAvailable.add(4);
        }
        if (damageReductionFallingProbability.size() > 0 && damageReductionFalling < path.getMaxDamageReductionFalling()) {
            propertiesAvailable.add(5);
        }
        if (damageReductionFireProbability.size() > 0 && damageReductionFire < path.getMaxDamageReductionFire()) {
            propertiesAvailable.add(6);
        }
        if (damageReductionExplosionsProbability.size() > 0 && damageReductionExplosions < path.getMaxDamageReductionExplosions()) {
            propertiesAvailable.add(7);
        }

        // Make sure we give them *something* if something is available
        if (propertiesAvailable.size() > 0 && !addedMaterials && !addedSpells && propertyCount == 0) {
            propertyCount = 1;
        }

        while (propertyCount != null && propertyCount-- > 0 && propertiesAvailable.size() > 0)
        {
			int randomPropertyIndex = (int)(Math.random() * propertiesAvailable.size());
            int randomProperty = propertiesAvailable.get(randomPropertyIndex);
			switch (randomProperty) {
			case 0: 
				if (costReductionProbability.size() > 0 && costReduction < path.getMaxCostReduction()) {
					addedProperties = true;
					costReduction = Math.min(path.getMaxCostReduction(), costReduction + RandomUtils.weightedRandom(costReductionProbability));
					wandProperties.set("cost_reduction", costReduction);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.cost_reduction", (float)costReduction));
				}
				break;
			case 1:
				if (powerProbability.size() > 0 && power < path.getMaxPower()) {
					addedProperties = true;
                    power = Math.min(path.getMaxPower(), power + RandomUtils.weightedRandom(powerProbability));
					wandProperties.set("power", power);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.power", (float)power));
				}
				break;
			case 2:
				if (damageReductionProbability.size() > 0 && damageReduction < path.getMaxDamageReduction()) {
					addedProperties = true;
                    damageReduction = Math.min(path.getMaxDamageReduction(), damageReduction + RandomUtils.weightedRandom(damageReductionProbability));
					wandProperties.set("protection", damageReduction);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection", (float)damageReduction));
				}
				break;
			case 3:
				if (damageReductionPhysicalProbability.size() > 0 && damageReductionPhysical < path.getMaxDamageReductionPhysical()) {
					addedProperties = true;
                    damageReductionPhysical = Math.min(path.getMaxDamageReductionPhysical(), damageReductionPhysical + RandomUtils.weightedRandom(damageReductionPhysicalProbability));
					wandProperties.set("protection_physical", damageReductionPhysical);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection_physical", (float)damageReductionPhysical));
                }
				break;
			case 4:
				if (damageReductionProjectilesProbability.size() > 0 && damageReductionProjectiles < path.getMaxDamageReductionProjectiles()) {
					addedProperties = true;
                    damageReductionProjectiles = Math.min(path.getMaxDamageReductionProjectiles(), damageReductionProjectiles + RandomUtils.weightedRandom(damageReductionProjectilesProbability));
					wandProperties.set("protection_projectiles", damageReductionProjectiles);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection_projectile", (float)damageReductionProjectiles));
				}
				break;
			case 5:
				if (damageReductionFallingProbability.size() > 0 && damageReductionFalling < path.getMaxDamageReductionFalling()) {
					addedProperties = true;
                    damageReductionFalling = Math.min(path.getMaxDamageReductionFalling(), damageReductionFalling + RandomUtils.weightedRandom(damageReductionFallingProbability));
					wandProperties.set("protection_falling", damageReductionFalling);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection_fall", (float)damageReductionFalling));
				}
				break;
			case 6:
				if (damageReductionFireProbability.size() > 0 && damageReductionFire < path.getMaxDamageReductionFire()) {
					addedProperties = true;
                    damageReductionFire = Math.min(path.getMaxDamageReductionFire(), damageReductionFire + RandomUtils.weightedRandom(damageReductionFireProbability));
					wandProperties.set("protection_fire", damageReductionFire);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection_fire", (float)damageReductionFire));
				}
				break;
			case 7:
				if (damageReductionExplosionsProbability.size() > 0 && damageReductionExplosions < path.getMaxDamageReductionExplosions()) {
					addedProperties = true;
                    damageReductionExplosions = Math.min(path.getMaxDamageReductionExplosions(), damageReductionExplosions + RandomUtils.weightedRandom(damageReductionExplosionsProbability));
					wandProperties.set("protection_explosions", damageReductionExplosions);
                    sendAddMessage(mage, wand, "wand.upgraded_property", Wand.getLevelString(messages, "wand.protection_blast", (float)damageReductionExplosions));
				}
				break;
			}
		}
		
		// The mana system is considered separate from other properties

		if (costReduction >= 1) {
			// Cost-Free wands don't need mana.
			wandProperties.set("mana_regeneration", 0);
			wandProperties.set("mana_max", 0);
			wandProperties.set("mana", 0);
		} else {
			int manaRegeneration = wand.getManaRegeneration();
			if (manaRegenerationProbability.size() > 0 && manaRegeneration < path.getMaxManaRegeneration()) {
                addedProperties = true;
                manaRegeneration = Math.min(path.getMaxManaRegeneration(), manaRegeneration + RandomUtils.weightedRandom(manaRegenerationProbability));
                wandProperties.set("mana_regeneration", manaRegeneration);

                String updateString = messages.get("wand.mana_regeneration");
                updateString = updateString.replace("$amount", Integer.toString(manaRegeneration));
                sendAddMessage(mage, wand, "wand.upgraded_property", updateString);
			}
			int manaMax = wand.getManaMax();
			if (manaMaxProbability.size() > 0 && manaMax < path.getMaxMaxMana()) {
				manaMax = Math.min(path.getMaxMaxMana(), manaMax + RandomUtils.weightedRandom(manaMaxProbability));
                if (path.getMatchSpellMana()) {
                    // Make sure the wand has at least enough mana to cast the highest costing spell it has.
                    manaMax = Math.max(maxManaCost, manaMax);
                }
				wandProperties.set("mana_max", manaMax);
                addedProperties = true;

                String updateString = messages.get("wand.mana_amount");
                updateString = updateString.replace("$amount", Integer.toString(manaMax));
                sendAddMessage(mage, wand, "wand.upgraded_property", updateString);
			}
			
			// Refill the wand's mana, why not
			wandProperties.set("mana", manaMax);
		}
		
		// Add or set uses to the wand
		if (additive) {
			// Only add uses to a wand if it already has some.
			int wandUses = wand.getRemainingUses();
			if (wandUses > 0 && wandUses < path.getMaxUses() && addUseProbability.size() > 0) {
				wandProperties.set("uses", Math.min(path.getMaxUses(), wandUses + RandomUtils.weightedRandom(addUseProbability)));
				addedProperties = true;
			}
		} else if (useProbability.size() > 0) {
			wandProperties.set("uses", Math.min(path.getMaxUses(), RandomUtils.weightedRandom(useProbability)));
		}

        // Set properties.
		wand.loadProperties(wandProperties);
		return addedMaterials || addedSpells || addedProperties;
	}

    public int getSpellProbabilityCount() {
        return spellProbability.size();
    }

    public int getMaterialProbabilityCount() {
        return materialProbability.size();
    }
}
