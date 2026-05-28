package com.jeffyjamzhd.btwegshdma.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.image.BufferedImage;

@Mixin(TextureUtil.class)
@Environment(EnvType.CLIENT)
public class TextureUtilMixin {
    @Shadow
    private static void setTextureBlurred(boolean par0) {
        throw new UnsupportedOperationException("Implemented via mixin");
    }

    @Inject(method = "uploadTextureImageSubImpl", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glTexSubImage2D(IIIIIIIILjava/nio/IntBuffer;)V"))
    private static void makeFilteredAlways(BufferedImage par0BufferedImage, int par1, int par2, boolean par3, boolean par4, CallbackInfo ci) {
        setTextureBlurred(true);
    }
}
