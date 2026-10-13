package config;

import java.util.Optional;

public class MoonConfig {

    public static String getMoonUrl() {
        // Читаем параметры из окружения Jenkins или оставляем дефолтные
        String moonBaseUrl = Optional.ofNullable(System.getenv("MOON_BASE_URL")).orElse("ws://moon.moon.svc.cluster.local:4444/");
        String version = Optional.ofNullable(System.getenv("PLAYWRIGHT_VERSION")).orElse("1.62.0");
        boolean headless = Boolean.parseBoolean(Optional.ofNullable(System.getenv("HEADLESS")).orElse("false"));
        String windowSize = Optional.ofNullable(System.getenv("WINDOW_SIZE")).orElse("1920,1080");

        String slash = moonBaseUrl.endsWith("/") ? "" : "/";
        String template = moonBaseUrl + slash + "playwright/chrome/" +
                "playwright-%s" +
                "?headless=%b" +
                "&arg=--start-maximized" +
                "&arg=--window-size=%s";
        // Формируем итоговую строку для Playwright
        return String.format(template, version, headless, windowSize);
    }
}
