package io.github.railroad.plugin.defaults;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.railroad.Railroad;
import io.github.railroad.discord.activity.RailroadActivities;
import io.github.railroad.plugin.Plugin;
import io.github.railroad.plugin.PluginPhaseResult;
import io.github.railroad.plugin.PluginStates;
import io.github.railroad.plugin.defaults.github.ui.GithubAccounts;
import io.github.railroad.ui.defaults.RRVBox;
import io.github.railroad.utility.ConfigHandler;
import io.github.railroad.vcs.connections.Profile;
import io.github.railroad.vcs.connections.hubs.GithubConnection;

public class Github extends Plugin {
    @Override
    public PluginPhaseResult initPlugin() {
        setPluiginName("Github");
        var phaseResult = new PluginPhaseResult();
        try {
            JsonObject config = ConfigHandler.getPluginSettings("Github", true);
            if (config.has("accounts")) {
                for (JsonElement element : config.get("accounts").getAsJsonArray()) {
                    if (element.getAsJsonObject().get("token") != null && element.getAsJsonObject().get("alias") != null) {
                        Railroad.LOGGER.info("Adding new Github connection to RepositoryManager");
                        var prof = new Profile();
                        prof.setAccessToken(element.getAsJsonObject().get("token").getAsString());
                        prof.setAlias(element.getAsJsonObject().get("alias").getAsString());
                        prof.setConfig_obj(element.getAsJsonObject());
                        var connection = new GithubConnection(prof);
                        Railroad.REPOSITORY_MANAGER.addConnection(connection);
                    }
                }
            }

            updateStatus(PluginStates.FINISHED_INIT);
        } catch (Exception exception) {
            phaseResult.addError(new Error(exception.getMessage()));
        }

        return phaseResult;
    }

    @Override
    public PluginPhaseResult loadPlugin() {
        updateStatus(PluginStates.LOADED);
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
    public RRVBox showSettings() {
        return new GithubAccounts();
    }
}