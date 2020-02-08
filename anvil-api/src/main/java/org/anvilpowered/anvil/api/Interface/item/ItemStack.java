package org.anvilpowered.anvil.api.Interface.item;

import org.anvilpowered.anvil.api.Anvil;

public class ItemStack {

    private int quantity;
    private IItemStack real;
    private Item item;

    /**
     * <strong>NOT FOR PACK DEVELOPMENT (internal use only)</strong>
     * @param stack IItemStack
     */
    @Deprecated
    public ItemStack(IItemStack stack) {
        this.real = stack;
        this.item = stack.getItem();
        this.quantity = stack.getQuantity();
    }

    public ItemStack(String key, int amount) {
        this(new Item(key), amount);
    }

    public ItemStack(Item item, int amount) {
        this.item = item;
        this.quantity = amount;

        //this.real = OSMC.getFactory().newIItemStack(this.item, this.quantity);
        this.real = Anvil.provide(IItemStack.class);
        this.real.setQuantity(this.quantity);
        this.real.setType(this.item);
    }

    public Item getItem() {
        return this.item;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public float getDurability() {
        return this.real.getDurability();
    }

    public float getMaxDurability() {
        return this.real.getMaxDurability();
    }

}
