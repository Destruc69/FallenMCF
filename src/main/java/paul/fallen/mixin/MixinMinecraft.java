package paul.fallen.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paul.fallen.FALLENClient;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Mutable
    @Shadow
    @Final
    private Timer timer;

    private float i = 1;
    private boolean increasing = true;

    @Inject(method = "run", at = @At("HEAD"))
    private void tick(CallbackInfo callbackInfo) {
        if (!FALLENClient.INSTANCE.getModuleManager().timer.bypass.bval) {
            timer = new Timer(FALLENClient.INSTANCE.getModuleManager().timer.timer.dval, 0);
        } else {
            if (FALLENClient.INSTANCE.getModuleManager().timer.timer.dval > 1) {
                if (increasing) {
                    if (i < FALLENClient.INSTANCE.getModuleManager().timer.timer.dval) {
                        i += 0.01f;
                    } else {
                        increasing = false;
                    }
                } else {
                    if (i > 1) {
                        i -= 0.01f;
                    } else {
                        increasing = true;
                    }
                }
            } else {
                if (increasing) {
                    if (i < 1) {
                        i += 0.01f;
                    } else {
                        increasing = false;
                    }
                } else {
                    if (i > FALLENClient.INSTANCE.getModuleManager().timer.timer.dval) {
                        i -= 0.01f;
                    } else {
                        increasing = true;
                    }
                }
            }
            timer = new Timer(i, 0);
        }

        System.out.println(FALLENClient.INSTANCE.getModuleManager().timer.timer.dval);
    }
}