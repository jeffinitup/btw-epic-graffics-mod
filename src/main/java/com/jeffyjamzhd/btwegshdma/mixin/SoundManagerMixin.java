package com.jeffyjamzhd.btwegshdma.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.Entity;
import net.minecraft.src.Minecraft;
import net.minecraft.src.SoundManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import java.util.Random;
import java.util.Set;

@Mixin(SoundManager.class)
@Environment(EnvType.CLIENT)
public class SoundManagerMixin {
    @Shadow
    private SoundSystem sndSystem;
    @Shadow
    private int ticksBeforeMusic;
    @Shadow
    @Final
    private Set playingSounds;
    @Unique
    private final Random random = new Random();
    @Unique
    private long lastMusicRandomPitch = 0;


    @Inject(method = "playStreaming", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
    private void randomizeMusicPitch(String par1Str, float par2, float par3, float par4, CallbackInfo ci) {
        this.sndSystem.setPitch("streaming", rollPitch());
    }

    @Inject(method = "playRandomMusicIfReady", at = @At("HEAD"))
    private void alwaysPlayMusic(CallbackInfo ci) {
        long time = Minecraft.getSystemTime();
        if (time > lastMusicRandomPitch) {
            this.sndSystem.setPitch("BgMusic", rollPitch());
            lastMusicRandomPitch = time + 10000;
        }

        this.ticksBeforeMusic = 0;
    }

    @ModifyArg(method = "setEntitySoundPitch", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;setPitch(Ljava/lang/String;F)V"), index = 1)
    private float randomizeMobPitch(float value) {
        return rollPitch();
    }

    @Inject(method = "playSound", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
    private void randomizeSoundPitch(String par1Str, float par2, float par3, float par4, float par5, float par6, CallbackInfo ci, @Local(name = "var8") String channel) {
        this.sndSystem.setPitch(channel, rollPitch());
    }

    @Redirect(method = "playSound", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;setPitch(Ljava/lang/String;F)V"))
    private void removeNormalPitch(SoundSystem instance, String sourcename, float value) {
    }

    @Inject(method = "playEntitySound", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
    private void randomizeEntitySoundPitch(String par1Str, Entity par2Entity, float par3, float par4, boolean par5, CallbackInfo ci, @Local(ordinal = 1) String channel) {
        this.sndSystem.setPitch(channel, rollPitch());
    }

    @Redirect(method = "playEntitySound", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;setPitch(Ljava/lang/String;F)V"))
    private void removeNormalEntityPitch(SoundSystem instance, String sourcename, float value) {
    }

    @Inject(method = "updateSoundLocation(Lnet/minecraft/src/Entity;Lnet/minecraft/src/Entity;)V", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;setPosition(Ljava/lang/String;FFF)V"))
    private void randomizeEntitySoundPitchUpdate(Entity par1Entity, Entity par2Entity, CallbackInfo ci, @Local String channel) {
        this.sndSystem.setPitch(channel, rollPitch());
    }

    @Inject(method = "playSoundFX", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;play(Ljava/lang/String;)V"))
    private void randomizeSoundFXPitch(String par1Str, float par2, float par3, CallbackInfo ci, @Local(ordinal = 1) String channel) {
        this.sndSystem.setPitch(channel, rollPitch());
    }

    @Unique
    private float rollPitch() {
        return random.nextFloat() * 1.5F + 0.5F;
    }
}
