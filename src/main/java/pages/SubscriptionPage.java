package pages;

import annotations.Path;
import annotations.Template;
import annotations.UrlTemplates;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Path("/")
@UrlTemplates(
        @Template("subscription"))
public class SubscriptionPage extends AbsBasePage<SubscriptionPage> {
    public SubscriptionPage(Page page) {
        super(page);
    }

    private Locator subscriptionBlock = page.locator("section")
            .filter(new Locator.FilterOptions().setHasText("Варианты подписки"));

    private Locator btnDetailsSubscription = subscriptionBlock.locator("button");

    // Метод: разворачивает описание первой доступной подписки и проверяет, что текст кнопки изменился на "Свернуть".
    public void expandFirstSubscriptionDetails() {
        // 1. Находим ссылку "Подробнее" на первой плитке и кликаем
        Locator detailsLink = btnDetailsSubscription.getByText("Подробнее").first();
        detailsLink.click();

        // 2. Находим ссылку "Свернуть" и проверяем её видимость
        Locator btnCollapse = btnDetailsSubscription.getByText("Свернуть").first();
        assertThat(btnCollapse).isVisible();
    }

    public void collapseSubscriptionDetails() {
        // 1. Находим активную кнопку "Свернуть" и кликаем по ней
        Locator btnCollapse = btnDetailsSubscription.getByText("Свернуть").first();
        btnCollapse.click();

        // 2. Проверяем, что ссылка вернула прежнее название "Подробнее"
        Locator detailsLink = btnDetailsSubscription.getByText("Подробнее").first();
        assertThat(detailsLink).isVisible();
    }
    public Locator elementIsVisibleWithText(String value) {
        Locator locator = page.getByText(value).first();
        assertThat(locator).isVisible();
        return locator;
    }

    public void elementIsHidden(Locator locator) {
        assertThat(locator).isHidden();
    }

    public void descriptionHidden(Locator element) {
        Locator locator = element.locator("[style*='height: 0px']").first();
        assertThat(locator).not().isVisible();
    }

    public void subscriptionsIsVisible() {
        List<Locator> headers = subscriptionBlock.locator("h4").all();

        for (Locator header : headers) {
            // Проверяем, что элемент действительно виден на UI
            assertThat(header).isVisible();

            System.out.println("Название варианта подписки: " + header.innerText().trim());
        }
    }

    public void btnBuySubscription() {
        Locator btnBuy = subscriptionBlock.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Купить")).first();
        // Кликаем и ждем обновления страницы
        btnBuy.click();
        page.waitForLoadState();
        // Проверяем, что URL изменился
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*(payment|lk|lk/payment-2/subscription|subscription).*"));
    }

}
