package org.anvilpowered.anvil.api.Interface.inventory;

import org.anvilpowered.anvil.api.Interface.item.ItemStack;

import java.util.Collection;

public interface Inventory {

    public Collection<ItemStack> getItems();

    public ItemStack getItemAt(int slot);

    public int getSize();

}
