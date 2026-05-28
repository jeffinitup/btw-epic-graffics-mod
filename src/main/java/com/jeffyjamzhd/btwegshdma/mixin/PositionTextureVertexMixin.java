package com.jeffyjamzhd.btwegshdma.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.PositionTextureVertex;
import net.minecraft.src.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Random;

@Mixin(PositionTextureVertex.class)
@Environment(EnvType.CLIENT)
public class PositionTextureVertexMixin {
    @Shadow
    public Vec3 vector3D;
    @Unique
    private static HashMap<String, Vec3> randCoords = new HashMap<>();

    @Inject(method = "<init>(Lnet/minecraft/src/PositionTextureVertex;FF)V", at = @At("TAIL"))
    private void randomizePositions1(PositionTextureVertex f, float g, float par3, CallbackInfo ci) {
        randomizeVector();
    }

    @Inject(method = "<init>(Lnet/minecraft/src/Vec3;FF)V", at = @At("TAIL"))
    private void randomizePositions2(Vec3 f, float g, float par3, CallbackInfo ci) {
        randomizeVector();
    }

    @Unique
    private void randomizeVector() {
        Random random = new Random();
        String coords = "%f.2,%f.2,%f.2".formatted(vector3D.xCoord, vector3D.yCoord, vector3D.zCoord);
        randCoords.putIfAbsent(coords, this.vector3D.addVector(random.nextFloat() - .5F, random.nextFloat() - .5F, random.nextFloat() - .5F));
        this.vector3D = randCoords.get(coords);
    }
}
