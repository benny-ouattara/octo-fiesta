package mvp.tuts.todo_app;

public interface BaseView<T> {
    void setPresenter(T presenter);
}
