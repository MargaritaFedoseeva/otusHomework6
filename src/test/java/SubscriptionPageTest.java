import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import extensions.UIExtensions;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.CoursesPage;
import pages.SubscriptionPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@ExtendWith(UIExtensions.class)
@UsePlaywright
@Epic("Платформа OTUS")
@Feature("Подписки")
public class SubscriptionPageTest {

    @Inject
    private SubscriptionPage subscriptionPage;

    @Test
    @DisplayName("Оформление подписки")
    void testSubscription() {
        // Открыть страницу подписок
        subscriptionPage.open("");

        // В блоке "Варианты подписки" проверить, что отображаются варианты подписки
        subscriptionPage.subscriptionsIsVisible();

        // Выбираем первую доступную плитку и нажимаем на кнопку "Подробнее", проверяем, что кнопка поменялась на "Свернуть"
        subscriptionPage.expandFirstSubscriptionDetails();
        Locator description = subscriptionPage.elementIsVisibleWithText("Можно продлить подписку на 3, 6 или 12 месяцев в личном кабинете");

        // Кликаем на ссылку "Свернуть" и проверяем, что часть описания тарифа свернулось
        subscriptionPage.collapseSubscriptionDetails();
        subscriptionPage.descriptionHidden(description);

        // Нажимаем на кнопку "Купить" и проверяем, что открыта страница оплаты
        subscriptionPage.btnBuySubscription();
    }
}
