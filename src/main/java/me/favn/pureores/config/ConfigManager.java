package me.favn.pureores.config;

import java.io.File;
import java.nio.file.Path;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import me.favn.pureores.Pureores;

public class ConfigManager {
    private static final String ORES_CONFIG_NAME = "config.yml";

    private final Pureores plugin;
    private OresConfig oresConfig;
    private YamlConfigurationProperties oresProperties;
    private Path oresFile;

    public ConfigManager(Pureores plugin) {
        this.plugin = plugin;
        this.oresProperties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .header("Main config file for the PureOres plugin")
                .setNameFormatter(NameFormatters.LOWER_UNDERSCORE)
                .inputNulls(true)
                .build();
        this.oresFile = new File(this.plugin.getDataFolder(), ORES_CONFIG_NAME).toPath();
    }

    public OresConfig getOresConfig() {
        if (this.oresConfig == null) {
            this.oresConfig = YamlConfigurations.update(this.oresFile, OresConfig.class, this.oresProperties);
            boolean valid = this.oresConfig.validate();
            this.plugin.getLogger().info("Loaded ores config from " + ORES_CONFIG_NAME + ". Config is " + (valid ? "valid." : "invalid."));
        }
        return this.oresConfig;
    }

    public void saveOresConfig() {
        if (this.oresConfig == null) {
            return;
        }
        YamlConfigurations.save(oresFile, OresConfig.class, this.oresConfig, this.oresProperties);
    }
}
