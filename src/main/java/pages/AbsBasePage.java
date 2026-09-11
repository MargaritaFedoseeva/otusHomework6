package pages;

import annotations.Path;
import annotations.Template;
import annotations.UrlTemplates;
import com.microsoft.playwright.Page;

public class AbsBasePage<T> {

    protected Page page;

    public AbsBasePage(Page page) {
        this.page = page;
    }

    @SuppressWarnings("unchecked")
    public T open() {
        page.navigate( getPath());
        return (T) this;
    }

    public T open(String... data) {
        page.navigate(getPath().isEmpty() ? getPathByTemplate(data) : getPath() + getPathByTemplate(data));
        return (T) this;
    }

    private String getPath() {
        Class<T> clazz = (Class<T>) getClass();
        if (clazz.isAnnotationPresent(Path.class)) {
            Path path = clazz.getDeclaredAnnotation(Path.class);
            return path.value();
        }
        return "";
    }

    private String getPathByTemplate(String... data) {
        Class<T> clazz = (Class<T>) getClass();
        if (clazz.isAnnotationPresent(UrlTemplates.class)) {
            UrlTemplates urlTemplates = clazz.getDeclaredAnnotation(UrlTemplates.class);
            Template template = urlTemplates.value();

            String templateStr = template.value();
            for (int i = 0; i < data.length; i++) {
                templateStr = templateStr.replace("$" + (i + 1), data[i]);
            }
            return templateStr;
        }
        return "";
    }

}
