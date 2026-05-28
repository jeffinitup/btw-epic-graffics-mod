package com.jeffyjamzhd.btwegshdma.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Minecraft;
import net.minecraft.src.Tessellator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Random;

@Mixin(Tessellator.class)
@Environment(EnvType.CLIENT)
public class TessellatorMixin {
    @Shadow
    private int drawMode;
    @Shadow
    private boolean hasNormals;
    @Shadow
    private boolean hasColor;
    @Shadow
    private boolean useVBO;
    @Shadow
    private boolean hasBrightness;
    @Unique
    private final Random random = new Random();
    @Unique
    private final HashMap<String, Vector3f> randomVector = new HashMap<>();

    @Unique
    private double lastReset = 0;
    @Unique
    private Vector3f working;

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glEnableClientState(I)V", ordinal = 0))
    private void setFilterMode(CallbackInfoReturnable<Integer> cir) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }

    @Inject(method = "addVertex", at = @At(value = "INVOKE", target = "Ljava/lang/Float;floatToRawIntBits(F)I", ordinal = 2))
    private void checkVertex(double d, double e, double f, CallbackInfo ci) {
        if (!this.hasBrightness && !this.hasNormals) {
            String vector = "%f.2,%f.2,%f.2".formatted(d, e, f);
            Vector3f messedUp = new Vector3f((float) d * getScale(), (float) e * getScale(), (float) f * getScale());
            randomVector.putIfAbsent(vector, messedUp);
            this.working = randomVector.get(vector);
        }
    }

    @ModifyArg(method = "addVertex", at = @At(value = "INVOKE", target = "Ljava/lang/Float;floatToRawIntBits(F)I", ordinal = 2), index = 0)
    private float setXRandom(float value) {
        if (!this.hasBrightness && !this.hasNormals) {
            return working.getX();
        }
        return value;
    }

    @ModifyArg(method = "addVertex", at = @At(value = "INVOKE", target = "Ljava/lang/Float;floatToRawIntBits(F)I", ordinal = 3), index = 0)
    private float setYRandom(float value) {
        if (!this.hasBrightness && !this.hasNormals) {
            return working.getY();
        }
        return value;
    }

    @ModifyArg(method = "addVertex", at = @At(value = "INVOKE", target = "Ljava/lang/Float;floatToRawIntBits(F)I", ordinal = 4), index = 0)
    private float setZRandom(float value) {
        if (!this.hasBrightness && !this.hasNormals) {
            return working.getZ();
        }
        return value;
    }

    @Inject(method = "reset", at = @At("HEAD"))
    private void resetHashMaps(CallbackInfo ci) {
        if (Minecraft.getSystemTime() > lastReset) {
            randomVector.clear();
            lastReset = Minecraft.getSystemTime() + 500;
        }
    }

    @Unique
    private float getScale() {
        return (1F - random.nextFloat() * 0.02F);
    }
}
