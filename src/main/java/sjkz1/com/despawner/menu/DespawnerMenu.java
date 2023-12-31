package sjkz1.com.despawner.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import sjkz1.com.despawner.Despawner;

public class DespawnerMenu extends AbstractContainerMenu {

    private final PaymentSlot paymentSlot;

    private final ContainerLevelAccess access;
    private final ContainerData despawnerData;


    public DespawnerMenu(int i, Container container) {
        this(i, container, new SimpleContainerData(2), ContainerLevelAccess.NULL);
    }
    private final Container despawmer = new SimpleContainer(1) {

        @Override
        public boolean canPlaceItem(int i, ItemStack itemStack) {
            return itemStack.is(Items.DIAMOND_SWORD);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };

    public DespawnerMenu(int i, Container container, ContainerData containerData, ContainerLevelAccess containerLevelAccess) {
        super(Despawner.DESPAWNER_MENU, i);
        int l;
        this.access = containerLevelAccess;
        this.despawnerData = containerData;
        this.paymentSlot = new PaymentSlot(despawmer, 0, 180, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots(containerData);

        for (l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(container, m + l * 9 + 9, 36 + m * 18, 137 + l * 18));
            }
        }
        for (l = 0; l < 9; ++l) {
            this.addSlot(new Slot(container, l, 36 + l * 18, 195));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int i) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(i);
        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (i == 0) {
                if (!this.moveItemStackTo(itemStack2, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemStack2, itemStack);
            } else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(itemStack2) && itemStack2.getCount() == 1 ? !this.moveItemStackTo(itemStack2, 0, 1, false) : (i >= 1 && i < 28 ? !this.moveItemStackTo(itemStack2, 28, 37, false) : (i >= 28 && i < 37 ? !this.moveItemStackTo(itemStack2, 1, 28, false) : !this.moveItemStackTo(itemStack2, 1, 37, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, itemStack2);
        }
        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return DespawnerMenu.stillValid(this.access, player, Despawner.DESPAWNER_BLOCK);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide) {
            return;
        }
        ItemStack itemStack = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
        if (!itemStack.isEmpty()) {
            player.drop(itemStack, false);
        }
    }

    @Override
    public void setData(int i, int j) {
        super.setData(i, j);
        this.broadcastChanges();
    }


    public int getLevels() {
        return this.despawnerData.get(0);
    }

    public int getCoolDown() {
        return this.despawnerData.get(1);
    }

    public void setCoolDown(int coolDown)
    {
        this.despawnerData.set(1,coolDown);
    }
    public void updateLevel() {
        if (this.paymentSlot.hasItem() && !this.paymentSlot.getItem().isEnchanted()) {
            this.despawnerData.set(0, 10);
            this.paymentSlot.remove(1);
            this.access.execute(Level::blockEntityChanged);
        }
    }

    class PaymentSlot
            extends Slot {
        public PaymentSlot(Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return itemStack.is(Items.DIAMOND_SWORD);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}
