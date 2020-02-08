package org.anvilpowered.anvil.api.Interface.entity;

import org.anvilpowered.anvil.api.Interface.item.ItemStack;

public interface ArmoredEntity extends Entity {

    void setArmor(ItemStack[] items);

    void setHelmet(ItemStack item);

    void setChestplate(ItemStack chestplate);

    void setLeggings(ItemStack leggings);

    void setBoots(ItemStack boots);

}
