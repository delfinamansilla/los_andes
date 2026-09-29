package util;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

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
    
    public static String getMpAccessToken() {
        String env = System.getenv("MP_ACCESS_TOKEN");
        if (env != null && !env.isEmpty()) return env;

        Path archivo = Paths.get(System.getProperty("user.home"), "los_andes.properties");
        try (InputStream in = Files.newInputStream(archivo)) {
            Properties props = new Properties();
            props.load(in);
            return props.getProperty("MP_ACCESS_TOKEN");
        } catch (IOException e) {
            System.err.println("No se pudo leer " + archivo + ": " + e.getMessage());
            return null;
        }
    }
}