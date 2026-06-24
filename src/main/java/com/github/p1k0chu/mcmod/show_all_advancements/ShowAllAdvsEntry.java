package com.github.p1k0chu.mcmod.show_all_advancements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.p1k0chu.mcmod.show_all_advancements.mixin.PlayerAdvancementsAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;

public class ShowAllAdvsEntry implements ModInitializer {
    public static String MOD_ID = "show-all-advancements";

    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setStrictness(com.google.gson.Strictness.LENIENT)
            .create();

    private static ShowAllAdvsEntry INSTANCE;

    private ShowAllAdvsConfig config = new ShowAllAdvsConfig(false, Set.of());

    @Override
    public void onInitialize() {
        if (INSTANCE != null) {
            throw new IllegalStateException("already initialized");
        }
        INSTANCE = this;
        try {
            reloadConfig();
        } catch (IOException e) {
            LOGGER.error("Error while loading config", e);
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal(MOD_ID)
                    .then(Commands.literal("reloadconfig")
                            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN)
                                    || source.isPlayer() && source.getServer().isSingleplayerOwner(source.getPlayer().nameAndId()))
                            .executes(ctx -> {
                                try {
                                    ShowAllAdvsEntry.getInstance().reloadConfig();

                                    var server = ctx.getSource().getServer();

                                    for (var player : server.getPlayerList().getPlayers()) {
                                        Iterable<AdvancementNode> roots = ((IServerAdvancementManager) server
                                                .getAdvancements()).show_all_advancements$getRoots();
                                        roots.forEach(node -> ((PlayerAdvancementsAccessor) player.getAdvancements())
                                                .invokeMarkForVisibilityUpdate(node.holder()));
                                    }

                                    ctx.getSource().sendSuccess(() -> Component.literal("Successfully reloaded config."), true);
                                    return 0;
                                } catch (IOException e) {
                                    ctx.getSource().sendFailure(Component.literal(
                                            String.format("Error while reloading config: %s", e.getMessage())));
                                    LOGGER.error("Error while reloading config", e);
                                    return 1;
                                }
                            })));
        });
    }

    public boolean showsThisHidden(String id) {
        return config.showHiddens() && !config.alwaysHiddenAdvs().contains(id);
    }

    public static ShowAllAdvsEntry getInstance() {
        return INSTANCE;
    }

    void reloadConfig() throws IOException {
        Path dir = FabricLoader.getInstance().getConfigDir();
        Path configPath = dir.resolve("show_all_advancements.json");
        File config = configPath.toFile();
        if (config.isDirectory()) {
            throw new IllegalStateException(String.format("\"%s\" is a directory.", config.getAbsolutePath()));
        }

        if (config.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(config))) {
                this.config = GSON.fromJson(r, ShowAllAdvsConfig.class);
            }
        } else {
            this.config = new ShowAllAdvsConfig(false, Set.of());
            try (FileWriter w = new FileWriter(config)) {
                GSON.toJson(this.config, w);
            } catch (IOException e) {
                throw new IOException(String.format("Couldn't write to file \"%s\"", config.getAbsolutePath()));
            }
        }
    }
}
