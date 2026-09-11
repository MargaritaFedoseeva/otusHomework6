package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.microsoft.playwright.Page;
import pages.CoursesPage;
import pages.LessonPage;
import pages.SubscriptionPage;

public class PagesModule extends AbstractModule {
    private Page page;

    public PagesModule(Page page) {
        this.page = page;
    }

    @Provides
    @Singleton
    public LessonPage getLessonPage(){
        return new LessonPage(page);
    }

    @Provides
    @Singleton
    public CoursesPage getCoursesPage(){
        return new CoursesPage(page);
    }

    @Provides
    @Singleton
    public SubscriptionPage getSubscriptionPage(){
        return new SubscriptionPage(page);
    }
}
