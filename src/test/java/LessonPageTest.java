import com.google.inject.Inject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.BoundingBox;
import extensions.UIExtensions;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pages.LessonPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@ExtendWith(UIExtensions.class)
@UsePlaywright
@Epic("Платформа OTUS")
@Feature("Действия на странице Курса")
public class LessonPageTest {

    @Inject
    private LessonPage lessonPage;

    @Test
    @DisplayName("Тестирвоание карусели преподавателей")
    public void testCheckTeachers() {
        lessonPage.open("clickhouse");

        // В блоке "Преподаватели" проверить, что отображаются плитки преподавателей
        int initialCardsCount = lessonPage.getTeacherCardsCount();
        Assertions.assertTrue(initialCardsCount > 0, "Плитки преподавателей не найдены");

        //С помощью drag and drop прокрутить преподавателей
        //Проверить, что список преподавателей прокрутился
        lessonPage.dragAndDropCardTeacher();

        // Выполняем нажатие на карточку в списке и проверяем, что открыта та же карточка на которую было выполнено нажатие
        String value = lessonPage.checkTeacherPopupWindow(1); //индекс можно изменять, сейчас выполняем нажатие на 2 карточку

        lessonPage.clickNextButtonPopupWindow(value);

        lessonPage.clickPrevButtonPopupWindow(value);
    }

}
