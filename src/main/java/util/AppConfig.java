package util;

public class AppConfig {

    private static final String FRONTEND_URL =
        System.getenv().getOrDefault(
            "FRONTEND_URL",
            "https://manor-routine-boundaries-interaction.trycloudflare.com"
        );

    private static final String BACKEND_URL =
        System.getenv().getOrDefault(
            "BACKEND_URL",
            "http://localhost:8080/club"
        );

    public static String getFrontendUrl() {
        return FRONTEND_URL;
    }

    public static String getBackendUrl() {
        return BACKEND_URL;
    }
}