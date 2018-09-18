package order.android.com.Bop.mvp.presenter;
import order.android.com.Bop.mvp.view.BaseView;

public interface BasePresenter<T extends BaseView>{

    void attachView(T view);

    void subscribe();

    void unsubscribe();
}
