package extensions;

import com.google.inject.Guice;
import com.microsoft.playwright.*;
import config.MoonConfig;
import modules.PagesModule;
import org.junit.jupiter.api.extension.*;

import java.nio.file.Paths;

public class UIExtensions implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    private Page page;
    private Browser browser;
    private BrowserContext browserContext;
    private Playwright playwright;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

//        //для локального запуска
//        // Указываем путь к кастомной папке драйвера (для Java)
//        System.setProperty("playwright.driver.tmpdir", "C:\\Users\\Администратор\\playwright-temp");

        String wssUrl = MoonConfig.getMoonUrl();

        playwright = Playwright.create();
        browser = playwright.chromium().connect(wssUrl, new BrowserType.ConnectOptions());

//        //для локального запуска
//        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
//                .setHeadless(false)
//                .setArgs(Arrays.asList("--start-maximized")));

    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        String testName = context.getDisplayName().replaceAll("[^a-zA-Z0-9]", "_");
        if (browserContext != null) {
            browserContext.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(testName + "_trace.zip")));
        }
        if (browserContext != null) {
            browserContext.close();
        }
        if (page != null) {
            page.close();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        browserContext = browser.newContext(new Browser.NewContextOptions()
                .setBaseURL(System.getProperty("base.url", "https://otus.ru"))
                .setViewportSize(null)
                .setIgnoreHTTPSErrors(true)
                .setHasTouch(true));

        browserContext.tracing().start(new Tracing.StartOptions()
                .setScreenshots(false)
                .setSnapshots(true)
                .setSources(true));

        page = browserContext.newPage();

        // Получаем экземпляр теста
        Object testInstance = context.getTestInstance().orElseThrow(() -> new IllegalStateException("Тестовый экземпляр не найден"));

        // Передаем рабочую страницу в модуль Guice
        Guice.createInjector(new PagesModule(page)).injectMembers(testInstance);
    }
}
