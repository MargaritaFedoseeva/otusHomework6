package pages;

import annotations.Path;
import annotations.Template;
import annotations.UrlTemplates;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.IntStream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Path("/")
@UrlTemplates(
        @Template("catalog/courses"))
public class CoursesPage extends AbsBasePage<CoursesPage> {
    public CoursesPage(Page page) {
        super(page);
    }

    private Locator courseCards = page.locator("section")
            .filter(new Locator.FilterOptions().setHasText("Каталог"))
            .locator("div div a[href*='/lessons/']");

    public void checkboxCoursesFilterWithTextSelected(String filterText, String attributeValue) {
        Locator filterContainer = page.locator(".ReactCollapse--content > div > div")
                .filter(new Locator.FilterOptions().setHasText(filterText)).first();
        assertThat(filterContainer).hasAttribute("value", attributeValue);
    }

    public List<String> getAllCardsText() {
        // Проверка, что хотя бы одна карточка появилась на странице перед сбором текста
        courseCards.first().waitFor();

        return courseCards.allInnerTexts();
    }

    public void clearFilters() {
        Locator btnClearFilters = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Очистить фильтры"));
        page.waitForResponse("**/api/catalog.entity.list*", () -> {
            btnClearFilters.click();
        });
        page.waitForTimeout(2000);
    }

    public void checkboxCoursesFilterWithTextClick(String filterText) {
        Locator checkbox = page.locator(".ReactCollapse--content > div > div")
                .filter(new Locator.FilterOptions().setHasText(filterText)).first();

        page.waitForResponse("**/api/catalog.entity.list*", () -> {
            checkbox.click();
        });
    }

    public void selectCourseDurationMonth(int from, int to) {
        // Находим оба ползунка
        Locator leftSlider = page.getByRole(AriaRole.SLIDER).nth(0);
        Locator rightSlider = page.getByRole(AriaRole.SLIDER).nth(1);

        // Фокусируемся на ползунке
        leftSlider.focus();
        // Нажимаем стрелку вправо. Слайдер визуально сдвинется на UI, React обновит состояние
        IntStream.range(0, from).forEach(i -> page.keyboard().press("ArrowRight"));

        rightSlider.focus();
        // Нажимаем стрелку влево. Слайдер визуально сдвинется на UI, React обновит состояние
        IntStream.range(0, 15 - to).forEach(i -> page.keyboard().press("ArrowLeft"));

        String expectedText = String.format("От %d до %d месяцев", from, to);
        page.getByText(expectedText)
                .first()
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));

        //page.waitForTimeout(2000); // todo:
    }

    public void checkUpdateCoursesCards(List<String> cardTextFiltered) {
        // Получаем текущий список текстов карточек
        List<String> allCardsText = courseCards.allInnerTexts();

        // Если списки полностью совпадают, значит данные на странице НЕ обновились
        if (allCardsText.equals(cardTextFiltered)) {
            throw new AssertionError("Информация по карточкам курсов не обновилась! " +
                    "Старый и новый списки идентичны. ");
        }

        // Если мы прошли проверку выше, значит, списки отличаются (информация обновилась)
        System.out.println("Карточки курсов успешно обновились.");
    }
}
