package pages;

import annotations.Path;
import annotations.Template;
import annotations.UrlTemplates;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AssertionsKt;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static com.microsoft.playwright.options.AriaRole.DIALOG;

@Path("/")
@UrlTemplates(
        @Template("lessons/$1"))
public class LessonPage extends AbsBasePage<LessonPage> {
    public LessonPage(Page page) {
        super(page);
    }

    // Блок преподавателей
    private Locator teachersSection = page.locator("text=Преподаватели")
            .locator("xpath=./ancestor::section");
    private Locator teacherCards = teachersSection.locator(".swiper-slide");
    private Locator popup = page.locator("[role='dialog'], #__PORTAL__ .swiper-slide-active");
    private Locator btnPopup = page.locator("[role='dialog'],#__PORTAL__ button:has(svg)");

    // Метод выполняет клик на плитку преподавателя и проверяет, что открыт popup того преподавателя
    public String checkTeacherPopupWindow(int index) {
        Locator activeCard = teacherCards.nth(index);
        // Берем имя преподавателя с карточки, чтобы сверить его внутри popup
        String expectedName = activeCard.locator("p, h3").first().textContent().trim();
        activeCard.click();
        assertThat(popup).isVisible();
        assertThat(popup).containsText(expectedName);
        return expectedName;
    }

    public int getTeacherCardsCount() {
        teacherCards.first().waitFor();
        // Возвращаем точное количество карточек на данный момент
        return teacherCards.count();
    }

    // Выполнение нажатия на кнопку "Следующий" (Вперед)
    public void clickNextButtonPopupWindow(String currentPopupName) {
        page.waitForTimeout(1500);
        Locator nextButton = btnPopup.nth(2);

        nextButton.click(new Locator.ClickOptions()
                .setPosition(5, 5) // Кликаем в верхний левый угол кнопки, мимо SVG
                .setForce(true)    // Игнорируем проверку на перекрытие другими элементами
        );
       // nextButton.dispatchEvent("click");

        // Проверяем, что имя внутри модалки изменилось
        page.waitForCondition(() -> !popup.locator("p, h3").first().textContent().trim().equals(currentPopupName));
        String nextPopupName = popup.locator("p,h3").first().textContent().trim();
        Assertions.assertNotEquals(currentPopupName, nextPopupName, "Карточка не переключилась вперед");
    }

    // Выполнение нажатия на кнопку "Предыдущий" (Назад)
    public void clickPrevButtonPopupWindow(String rolledBackName) {
        String nameBeforeClick = popup.locator("p, h3").first().textContent().trim();

        page.waitForTimeout(1500);

        Locator prevButton = btnPopup.nth(1);
        prevButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        prevButton.focus();
        prevButton.click(new Locator.ClickOptions()
                .setPosition(5, 5) // Кликаем в верхний левый угол кнопки, мимо SVG
                .setForce(true)    // Игнорируем проверку на перекрытие другими элементами
        );
//        prevButton.dispatchEvent("click");

        // Проверяем, что имя внутри модалки изменилось
        page.waitForCondition(() -> !popup.locator("p, h3").first().textContent().trim().equals(nameBeforeClick));
        String popupName = popup.locator("p,h3").first().textContent().trim();
        Assertions.assertEquals(rolledBackName, popupName, "Не открылась предыдущая карточка");
    }

    public void dragAndDropCardTeacher() {
        // Находим активную карточку (которую будем тащить)
        Locator sourceCard = teachersSection.locator(".swiper-slide-active").first();
        String firstCardBefore = sourceCard.locator("p").first().textContent().trim();

        // Находим следующую карточку (куда будем тащить)
        //влево
        Locator target = teachersSection.locator(".swiper-slide-next").first();
        //вправо
        //Locator target = teachersSection.locator(".swiper-slide-prev").first();

        Locator.DragToOptions options = new Locator.DragToOptions()
                .setForce(true)
                .setSourcePosition(20, 20)
                .setTargetPosition(20, 20)
                .setSteps(20);

        sourceCard.dragTo(target, options);
        page.waitForTimeout(1500);

        String firstCardAfter = sourceCard.locator("p").first().textContent().trim();
        Assertions.assertNotEquals(firstCardBefore, firstCardAfter, "Список преподавателей не прокрутился");
    }
}
