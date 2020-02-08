package org.anvilpowered.anvil.api.Interface.inventory;

import org.anvilpowered.anvil.api.Interface.item.ItemStack;

import java.util.Collection;

public interface ArmoredInventory extends Inventory {

    public Collection<ItemStack> getArmorSlots();

    public ItemStack getHelmet();

    public ItemStack getChestplate();

    public ItemStack getLeggings();

    public ItemStack getBoots();

    public void setHelmet(ItemStack helmet);

    public void setChestplate(ItemStack chestplate);

    public void setLeggings(ItemStack leggings);

    public void setBoots(ItemStack boots);

}
