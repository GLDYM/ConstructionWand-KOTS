// package dev.polaris_light.constructionwand.containers.handlers;

// import dev.xkmc.l2backpack.content.capability.PickupModeCap;
// import dev.xkmc.l2backpack.content.capability.InvPickupCap;
// import dev.xkmc.l2backpack.content.capability.PickupTrace;
// import dev.xkmc.l2backpack.init.registrate.LBMisc;
// import net.minecraft.server.level.ServerPlayer;
// import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.ItemStack;
// import net.neoforged.neoforge.items.IItemHandlerModifiable;
// import dev.polaris_light.constructionwand.ConstructionWand;
// import dev.polaris_light.constructionwand.api.IContainerHandler;
// import dev.polaris_light.constructionwand.basics.WandUtil;
// import dev.polaris_light.constructionwand.containers.ContainerTrace;

// public class HandlerLightland implements IContainerHandler {

//     @Override
//     public boolean matches(Player player, ItemStack itemStack, ItemStack inventoryStack) {
//         return resolveInvCap(inventoryStack) != null;
//     }

//     // Ender Chest is 0
//     @Override
//     public int getSignature(Player player, ItemStack inventoryStack) {
//         InvPickupCap<?> cap = resolveInvCap(inventoryStack);
//         return cap != null ? cap.getSignature() : -1;
//     }

//     @Override
//     public int countItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack) {
//         if (!(player instanceof ServerPlayer sp)) return 0;
//         InvPickupCap<?> cap = resolveInvCap(inventoryStack);
//         if (cap == null) return 0;

//         IItemHandlerModifiable inv = cap.getInv(new PickupTrace(false, sp));
//         if (inv == null) return 0;
//         int found = 0;

//         for (int i = 0; i < inv.getSlots(); i++) {
//             ItemStack stack = inv.getStackInSlot(i);
//             if (WandUtil.stackEquals(stack, itemStack)) {
//                 found += stack.getCount();
//                 if (found >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
//             } else {
//                 found += ConstructionWand.containerManager.countItems(player, trace, itemStack, stack);
//                 if (found >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
//             }
//         }

//         return found;
//     }

//     @Override
//     public int useItems(Player player, ContainerTrace trace, ItemStack itemStack, ItemStack inventoryStack, int count) {
//         if (!(player instanceof ServerPlayer sp)) return count;
//         InvPickupCap<?> cap = resolveInvCap(inventoryStack);
//         if (cap == null) return count;

//         IItemHandlerModifiable inv = cap.getInv(new PickupTrace(false, sp));
//         if (inv == null) return count;
//         int remaining = count;

//         for (int i = 0; i < inv.getSlots(); i++) {
//             ItemStack stack = inv.getStackInSlot(i);
//             if (WandUtil.stackEquals(stack, itemStack)) {
//                 int extractable = Math.min(stack.getCount(), remaining);
//                 ItemStack extracted = inv.extractItem(i, extractable, false);
//                 remaining -= extracted.getCount();
//                 if (remaining <= 0) return 0;
//             } else {
//                 remaining = ConstructionWand.containerManager.useItems(player, trace, itemStack, stack, remaining);
//                 if (remaining <= 0) return 0;
//             }
//         }

//         return remaining;
//     }

//     private InvPickupCap<?> resolveInvCap(ItemStack stack) {
//         if (stack == null || stack.isEmpty()) return null;

//         PickupModeCap cap = stack.getCapability(LBMisc.PICKUP);
//         return cap instanceof InvPickupCap<?> invPickupCap ? invPickupCap : null;
//     }
// }
