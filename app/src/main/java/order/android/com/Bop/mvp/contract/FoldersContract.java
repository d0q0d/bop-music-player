package order.android.com.Bop.mvp.contract;

import android.content.Context;

import java.util.List;

import order.android.com.Bop.mvp.model.FolderInfo;
import order.android.com.Bop.mvp.presenter.BasePresenter;
import order.android.com.Bop.mvp.view.BaseView;


public interface FoldersContract {

    interface View extends BaseView {

        Context getContext();

        void showEmptyView();

        void showFolders(List<FolderInfo> folderInfos);
    }

    interface Presenter extends BasePresenter<View> {

        void loadFolders();
    }
}
