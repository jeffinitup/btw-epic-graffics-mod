package com.jeffyjamzhd.btwegshdma.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Random;

@Mixin(FontRenderer.class)
@Environment(EnvType.CLIENT)
public class FontRendererMixin {
    @Unique
    private final Random random = new Random();
    @Unique
    private final Vector3f[][] charRandom = new Vector3f[256][4];
    @Unique
    private long lastRandom = 0;

    @Inject(method = "renderDefaultChar", at = @At("HEAD"))
    private void randomizeIfNecessary(int par1, boolean par2, CallbackInfoReturnable<Float> cir) {
        if (Minecraft.getSystemTime() > lastRandom) {
            generateRandom();
            lastRandom = Minecraft.getSystemTime() + 666L;
        }
    }

    @ModifyArgs(method = "renderDefaultChar", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glVertex3f(FFF)V", ordinal = 0))
    private void modifyVertex0(Args args, @Local(argsOnly = true, ordinal = 0) int character) {
        setDraw(args, 0, character);
    }

    @ModifyArgs(method = "renderDefaultChar", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glVertex3f(FFF)V", ordinal = 1))
    private void modifyVertex1(Args args, @Local(argsOnly = true, ordinal = 0) int character) {
        setDraw(args, 1, character);
    }

    @ModifyArgs(method = "renderDefaultChar", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glVertex3f(FFF)V", ordinal = 2))
    private void modifyVertex2(Args args, @Local(argsOnly = true, ordinal = 0) int character) {
        setDraw(args, 2, character);
    }

    @ModifyArgs(method = "renderDefaultChar", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glVertex3f(FFF)V", ordinal = 3))
    private void modifyVertex3(Args args, @Local(argsOnly = true, ordinal = 0) int character) {
        setDraw(args, 3, character);
    }

    @Inject(method = "renderDefaultChar", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glBegin(I)V"))
    private void setRenderMode(int par1, boolean par2, CallbackInfoReturnable<Float> cir) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }

    @Unique
    private void setDraw(Args args, int pos, int character) {
        args.set(0, (float) args.get(0) + charRandom[character][pos].getX());
        args.set(1, (float) args.get(1) + charRandom[character][pos].getY());
    }

    @Unique
    private void generateRandom() {
        for (int i = 0; i < charRandom.length; i++) {
            for (int j = 0; j < 4; j++)
                charRandom[i][j] = new Vector3f(random.nextFloat() * 1.5F - .75F, random.nextFloat() * 1.5F - .75F, 0F);
        }
    }
}
