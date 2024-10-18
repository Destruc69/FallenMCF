package paul.fallen.module.modules.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import paul.fallen.module.Module;

import java.util.UUID;

public class FakePlayer extends Module {

    private static PlayerEntity fakeEntity;

    public FakePlayer(int bind, String name, String displayName, Category category, String description) {
        super(bind, name, displayName, category, description);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.world != null) {
            fakeEntity = new RemoteClientPlayerEntity(mc.world, new GameProfile(new UUID(69L, 96L), "Freecam"));
            fakeEntity.inventory.copyInventory(mc.player.inventory);
            fakeEntity.setPositionAndRotation(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), mc.player.rotationYaw, mc.player.rotationPitch);
            fakeEntity.rotationYawHead = mc.player.rotationYawHead;
            mc.world.addEntity(fakeEntity.getEntityId(), fakeEntity);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.world != null && fakeEntity != null) {
            mc.world.removeEntityFromWorld(fakeEntity.getEntityId());
        }
    }
}
