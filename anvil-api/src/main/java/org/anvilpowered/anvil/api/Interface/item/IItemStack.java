package org.anvilpowered.anvil.api.Interface.item;

public interface IItemStack {

    Item getItem();

    int getQuantity();

    void setQuantity(int quantity);

    void setType(Item type);

    float getDurability();

    float getMaxDurability();

}
