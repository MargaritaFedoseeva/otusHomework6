import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.junit.UsePlaywright;
import extensions.UIExtensions;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.CoursesPage;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ExtendWith(UIExtensions.class)
@UsePlaywright
@Epic("Платформа OTUS")
@Feature("Действия на странице Список курсов")
public class CoursesPageTest {

    @Inject
    private CoursesPage coursesPage;

    @Test
    @DisplayName("Фильтрация курсов")
    void testCourseFilters() {
        // Открыть страницу каталог курсов
        coursesPage.open("");

        // Проверить, что в фильтрах по умолчанию выбраны "Все направления" и "Любой уровень сложности"
        coursesPage.checkboxCoursesFilterWithTextSelected("Все направления","true");
        coursesPage.checkboxCoursesFilterWithTextSelected("Любой уровень", "true");

        int fromMonth = 3;
        int toMonth = 10;

        // Выбрать продолжительность курса в фильтре от 3 до 10 месяцев
        coursesPage.selectCourseDurationMonth(fromMonth, toMonth);

        // Проверяем, что в каталоге отображаются курсы с выбранной продолжительностью
        // Получаем текстовое содержимое из абсолютно всех карточек
        List<String> allCardsTextFilteredMonth = coursesPage.getAllCardsText();
        Pattern pattern = Pattern.compile("(\\d+)\\s+месяц");

        // Проверяем каждую карточку в цикле
        for (String cardText : allCardsTextFilteredMonth) {
            Matcher matcher = pattern.matcher(cardText);

            if (matcher.find()) {
                // Извлекаем число месяцев
                int months = Integer.parseInt(matcher.group(1));

                // Проверяем, что число входит в диапазон от 3 до 10 месяцев
                Assertions.assertTrue(months >= fromMonth && months <= toMonth,
                        String.format("Ошибка! Найден курс с длительностью %d мес., что выходит за рамки фильтра (%d-%d). Текст карточки: %s", months, fromMonth, toMonth, cardText)
                );
            } else {
                System.out.println("Предупреждение: Срок обучения не найден в карточке: " + cardText);
            }
        }
        // Выбираем направление курса "Архитектура"
        coursesPage.checkboxCoursesFilterWithTextClick("Архитектура");
        List<String> allCardsTextFiltered = coursesPage.getAllCardsText();

        // Проверяем, что карточки курсов обновились
        coursesPage.checkUpdateCoursesCards(allCardsTextFilteredMonth); //берем карточки из предыдущего фильтра

        // Нажимаем на кнопку "Очистить фильтры"
        coursesPage.clearFilters();

        // Проверяем, что фильтр вернулись к исходному состоянию
        coursesPage.checkboxCoursesFilterWithTextSelected("Все направления","true");
        coursesPage.checkboxCoursesFilterWithTextSelected("Любой уровень","true");
        coursesPage.checkboxCoursesFilterWithTextSelected("Архитектура","false");

        // Проверяем, что карточки курсов обновились
        coursesPage.checkUpdateCoursesCards(allCardsTextFiltered); //берем карточки из предыдущего фильтра
    }
}