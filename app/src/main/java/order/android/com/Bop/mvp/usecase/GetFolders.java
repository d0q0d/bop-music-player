package order.android.com.Bop.mvp.usecase;

import java.util.List;


import order.android.com.Bop.mvp.model.FolderInfo;
import order.android.com.Bop.mvp.repository.Repository;
import rx.Observable;

public class GetFolders extends UseCase<GetFolders.RequestValues,GetFolders.ResponseValue>{

    private final Repository mRepository;

    public GetFolders(Repository repository) {
        this.mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getFoldersWithSong());
    }

    public static final class RequestValues implements UseCase.RequestValues{
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<List<FolderInfo>> mListObservable;

        public ResponseValue(Observable<List<FolderInfo>> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<List<FolderInfo>> getFolderList(){
            return mListObservable;
        }
    }
}
