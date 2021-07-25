package com.isp.memate;

import java.io.File;

/**
 * Somewhat Cross-Platform resolving for the users configuration directory.
 */
public class Config {

    /**
     * Returns the configuration directory to use. On Windows this will most likely
     * be the environment variable APP_DATA, on linux XDG_CONFIG_HOME if available, or
     * home/.config in all other cases and systems.
     *
     * @param folderName memate folder name
     * @return target directory for configuration files
     *
     * @throws IllegalStateException if no path could be found
     */
    public static String getConfigDir(String folderName) {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("windows")) {
            return System.getenv("APPDATA") + File.separator + folderName;
        }

        //Linux
        String confHome = System.getenv("XDG_CONFIG_HOME");
        if (confHome != null && !confHome.isEmpty()) {
            return confHome + File.separator + folderName;
        }

        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isEmpty()) {
            throw new IllegalStateException("User home couldn't be found. Unable to find configuration directory.");
        }

        //We'll assume that .config always exists.
        return userHome + File.separator + ".config" + File.separator + folderName;
    }
}
