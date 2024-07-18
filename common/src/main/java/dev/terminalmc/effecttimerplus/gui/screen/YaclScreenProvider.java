package dev.terminalmc.effecttimerplus.gui.screen;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import dev.terminalmc.effecttimerplus.config.Config;
import dev.terminalmc.effecttimerplus.mixin.GuiAccessor;
import dev.terminalmc.effecttimerplus.util.IndicatorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.awt.*;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

import static dev.terminalmc.effecttimerplus.util.Localization.localized;

public class YaclScreenProvider {
    /**
     * Builds and returns a YACL options screen.
     * @param parent the current screen.
     * @return a new options {@link Screen}.
     * @throws NoClassDefFoundError if the YACL mod is not available.
     */
    static Screen getConfigScreen(Screen parent) {
        Config options = Config.get();
        Preview preview = new Preview();

        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(localized("screen", "options"))
                .save(Config::save);

        ConfigCategory.Builder timerCat = ConfigCategory.createBuilder()
                .name(localized("option", "timer"));

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerEnabled = val)
                .binding(Config.defaultTimerEnabled,
                        () -> options.timerEnabled,
                        val -> options.timerEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.ambient.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerEnabledAmbient = val)
                .binding(Config.defaultTimerEnabledAmbient,
                        () -> options.timerEnabledAmbient,
                        val -> options.timerEnabledAmbient = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.warn.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerWarnEnabled = val)
                .binding(Config.defaultTimerWarnEnabled,
                        () -> options.timerWarnEnabled,
                        val -> options.timerWarnEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "timer.warn.flash.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerFlashEnabled = val)
                .binding(Config.defaultTimerFlashEnabled,
                        () -> options.timerFlashEnabled,
                        val -> options.timerFlashEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerColor = fixAlpha(val.getRGB()))
                .binding(fromArgb(Config.defaultTimerColor),
                        () -> fromArgb(options.timerColor),
                        val -> options.timerColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.color.back"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerBackColor = fixAlpha(val.getRGB()))
                .binding(fromArgb(Config.defaultTimerBackColor),
                        () -> fromArgb(options.timerBackColor),
                        val -> options.timerBackColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Color>createBuilder()
                .name(localized("option", "timer.warn.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerWarnColor = fixAlpha(val.getRGB()))
                .binding(fromArgb(Config.defaultTimerWarnColor),
                        () -> fromArgb(options.timerWarnColor),
                        val -> options.timerWarnColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        timerCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "timer.warn.time"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerWarnTime = val)
                .binding(Config.defaultTimerWarnTime,
                        () -> options.timerWarnTime,
                        val -> options.timerWarnTime = val)
                .controller(option -> IntegerSliderControllerBuilder.create(option)
                        .range(0, 60)
                        .step(1)
                        .formatValue(val ->
                                localized("option", "timer.warn.time.value", val)))
                .build());

        timerCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "timer.location"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.timerLocation = val)
                .binding(Config.defaultTimerLocation,
                        () -> options.timerLocation,
                        val -> options.timerLocation = val)
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(Config.locations)
                        .formatValue(val -> localized("option", "location." + val)))
                .build());

        // Potency indicator category
        ConfigCategory.Builder potencyCat = ConfigCategory.createBuilder()
                .name(localized("option", "potency"));

        potencyCat.option(Option.<Boolean>createBuilder()
                .name(localized("option", "potency.enabled"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.potencyEnabled = val)
                .binding(Config.defaultPotencyEnabled,
                        () -> options.potencyEnabled,
                        val -> options.potencyEnabled = val)
                .controller(option -> BooleanControllerBuilder.create(option).coloured(true))
                .build());

        potencyCat.option(Option.<Color>createBuilder()
                .name(localized("option", "potency.color.back"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.potencyBackColor = fixAlpha(val.getRGB()))
                .binding(fromArgb(Config.defaultPotencyBackColor),
                        () -> fromArgb(options.potencyBackColor),
                        val -> options.potencyBackColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        potencyCat.option(Option.<Color>createBuilder()
                .name(localized("option", "potency.color"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.potencyColor = fixAlpha(val.getRGB()))
                .binding(fromArgb(Config.defaultPotencyColor),
                        () -> fromArgb(options.potencyColor),
                        val -> options.potencyColor = fixAlpha(val.getRGB()))
                .controller(option -> ColorControllerBuilder.create(option).allowAlpha(true))
                .build());

        potencyCat.option(Option.<Integer>createBuilder()
                .name(localized("option", "potency.location"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.potencyLocation = val)
                .binding(Config.defaultPotencyLocation,
                        () -> options.potencyLocation,
                        val -> options.potencyLocation = val)
                .controller(option -> CyclingListControllerBuilder.create(option)
                        .values(Config.locations)
                        .formatValue(val -> localized("option", "location." + val)))
                .build());

        ConfigCategory.Builder scaleCat = ConfigCategory.createBuilder()
                .name(localized("option", "scale"));

        scaleCat.option(Option.<Double>createBuilder()
                .name(localized("option", "scale"))
                .description(OptionDescription.createBuilder().customImage(preview).build())
                .listener((option, val) -> preview.scale = val)
                .binding(Config.defaultScale,
                        () -> options.scale,
                        val -> options.scale = val)
                .controller(option -> DoubleSliderControllerBuilder.create(option)
                        .range(0.5D, 1.5D)
                        .step(0.1D))
                .build());

        // Assemble
        builder.category(timerCat.build());
        builder.category(potencyCat.build());
        builder.category(scaleCat.build());

        YetAnotherConfigLib yacl = builder.build();
        return yacl.generateScreen(parent);
    }

    // Special option utils

    private static Color fromArgb(int i) {
        return new Color((i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF, (i >> 24) & 0xFF);
    }

    // Workaround for a bug (?) causing alpha to snap to max when set below 4.
    private static int fixAlpha(int color) {
        if (toAlpha.applyAsInt(color) < 4) {
            return withAlpha.applyAsInt(color, fromAlpha.applyAsInt(4));
        }
        return color;
    }

    private static final IntUnaryOperator toAlpha = (value) -> (value >> 24 & 255);
    private static final IntUnaryOperator fromAlpha = (value) -> (value * 16777216);
    private static final IntBinaryOperator withAlpha = (value, alpha) ->
            (value - (fromAlpha.applyAsInt(toAlpha.applyAsInt(value))) + alpha);

    // Preview

    private static class Preview implements ImageRenderer {
        // Maintain a copy of all values for instant update by listeners
        Config options = Config.get();
        public double scale = options.scale;
        public boolean potencyEnabled = options.potencyEnabled;
        public boolean timerEnabled = options.timerEnabled;
        public boolean timerEnabledAmbient = options.timerEnabledAmbient;
        public boolean timerWarnEnabled = options.timerWarnEnabled;
        public boolean timerFlashEnabled = options.timerFlashEnabled;
        public int timerWarnTime = options.timerWarnTime;
        public int potencyColor = options.potencyColor;
        public int potencyBackColor = options.potencyBackColor;
        public int timerColor = options.timerColor;
        public int timerWarnColor = options.timerWarnColor;
        public int timerBackColor = options.timerBackColor;
        public int potencyLocation = options.potencyLocation;
        public int timerLocation = options.timerLocation;

        // Params: effect, duration, amplifier, ambient, visible
        private final MobEffectInstance[] DEMO_EFFECTS = new MobEffectInstance[] {
                new MobEffectInstance(MobEffects.DIG_SPEED, 111, 1, true, true),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 211, 1, false, true),
                new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 411, 2, false, true),
                new MobEffectInstance(MobEffects.DAMAGE_BOOST, 811, 9, false, true),
                new MobEffectInstance(MobEffects.JUMP, 1251, 4, false, true),
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2131, 0, false, true),
                new MobEffectInstance(MobEffects.WEAKNESS, 3500, 1, false, true),
                new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600, 0, false, true),
                new MobEffectInstance(MobEffects.INVISIBILITY, 144000, 0, false, true),
                new MobEffectInstance(MobEffects.CONDUIT_POWER, -1, 0, false, true),
        };

        @Override
        public int render(GuiGraphics graphics, int x, int y, int width, float delta) {
            float scale = (float)this.scale;
            graphics.pose().pushPose();
            graphics.pose().translate(x * (1 - scale), y * (1 - scale), 0.0F);
            graphics.pose().scale(scale, scale, 0.0F);

            Minecraft mc = Minecraft.getInstance();
            int movingX = x;
            int movingY = y;
            int space = 27;
            int targetHeight = space;
            int maxX = (int)(x + width / scale);

            for (MobEffectInstance effect : DEMO_EFFECTS) {
                if (effect.isAmbient()) {
                    graphics.blitSprite(GuiAccessor.getEffectBackgroundAmbientSprite(), movingX, movingY, 24, 24);
                } else {
                    graphics.blitSprite(GuiAccessor.getEffectBackgroundSprite(), movingX, movingY, 24, 24);
                }
                graphics.blit(movingX + 3, movingY + 3, 0, 18, 18, mc.getMobEffectTextures().get(effect.getEffect()));

                // Render potency overlay
                if (potencyEnabled && effect.getAmplifier() > 0) {
                    String label = IndicatorUtil.getAmplifierAsString(effect.getAmplifier());
                    int labelWidth = mc.font.width(label);
                    int pX = movingX + IndicatorUtil.getTextOffsetX(potencyLocation, labelWidth);
                    int pY = movingY + IndicatorUtil.getTextOffsetY(potencyLocation);
                    graphics.fill(pX, pY, pX + labelWidth, pY + mc.font.lineHeight - 1, potencyBackColor);
                    graphics.drawString(mc.font, label, pX, pY, potencyColor, false);
                }
                // Render timer overlay
                if (timerEnabled && (timerEnabledAmbient || !effect.isAmbient())) {
                    String label = IndicatorUtil.getDurationAsString(effect.getDuration());
                    int labelWidth = mc.font.width(label);
                    int pX = movingX + IndicatorUtil.getTextOffsetX(timerLocation, labelWidth);
                    int pY = movingY + IndicatorUtil.getTextOffsetY(timerLocation);
                    graphics.fill(pX, pY, pX + labelWidth, pY + mc.font.lineHeight - 1, timerBackColor);
                    graphics.drawString(mc.font, label, pX, pY, IndicatorUtil.getTimerColor(effect, timerColor,
                            timerWarnEnabled, timerWarnTime, timerWarnColor, timerFlashEnabled), false);
                }
                movingX += space;
                if (movingX + space > maxX) {
                    movingX = x;
                    movingY += space;
                    targetHeight += space;
                }
            }

            graphics.pose().popPose();
            return (int)(targetHeight * scale);
        }

        @Override
        public void close() {
            // Not currently used
        }
    }
}