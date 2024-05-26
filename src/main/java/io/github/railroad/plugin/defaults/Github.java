package io.github.railroad.plugin.defaults;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.railroad.Railroad;
import io.github.railroad.discord.activity.RailroadActivities;
import io.github.railroad.plugin.Plugin;
import io.github.railroad.plugin.PluginPhaseResult;
import io.github.railroad.plugin.PluginStates;
import io.github.railroad.plugin.defaults.github.ui.GithubAccounts;
import io.github.railroad.utility.ConfigHandler;
import io.github.railroad.vcs.connections.Profile;
import io.github.railroad.vcs.connections.hubs.GithubConnection;
import javafx.scene.control.ScrollPane;

public class Github extends Plugin {
    @Override
    public PluginPhaseResult initPlugin() {
        this.setPluiginName("Github");
        PluginPhaseResult phaseResult = new PluginPhaseResult();
        try {
            JsonObject config = ConfigHandler.getPluginSettings("Github", true);
            if (config.has("accounts")) {
                for (JsonElement element : config.get("accounts").getAsJsonArray()) {
                    if (!element.getAsJsonObject().get("username").getAsString().isEmpty()) {
                        Railroad.LOGGER.info("Adding new Github connection to RepositoryManager");
                        Profile prof = new Profile();
                        prof.setAccessToken(element.getAsJsonObject().get("username").getAsString());
                        GithubConnection connection = new GithubConnection(prof);
                        Railroad.REPOSITORY_MANAGER.addConnection(connection);
                    }
                }
            }
            this.updateStatus(PluginStates.FINISHED_INIT);
        } catch (Exception exception) {
            phaseResult.addError(new Error(exception.getMessage()));
        }

        return phaseResult;
    }

    @Override
    public PluginPhaseResult loadPlugin() {
        this.updateStatus(PluginStates.LOADED);
        return new PluginPhaseResult();
    }

    @Override
    public PluginPhaseResult unloadPlugin() {
        return null;
    }

    @Override
    public PluginPhaseResult railroadActivityChange(RailroadActivities.RailroadActivityTypes railroadActivityTypes) {
        return null;
    }

    @Override
    public PluginPhaseResult reloadPlugin() {
        return null;
    }

    @Override
    public ScrollPane showSettings() {
        GithubAccounts accounts = new GithubAccounts();
        return accounts;
    }
}
