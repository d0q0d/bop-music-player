package order.android.com.Bop.mvp.usecase;

import java.util.List;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.mvp.model.Album;
import order.android.com.Bop.mvp.repository.Repository;
import rx.Observable;

public class GetAlbums extends UseCase<GetAlbums.RequestValues,GetAlbums.ResponseValue>{

    private final Repository mRepository;

    public GetAlbums(Repository repository){
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        String action = requestValues.getAction();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                return new ResponseValue(mRepository.getAllAlbums());
            default:
                throw new RuntimeException("wrong action type");
        }
    }

    public static final class RequestValues implements UseCase.RequestValues{

        private final String action;

        public RequestValues(String action) {
            this.action = action;
        }

        public String getAction() {
            return action;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<List<Album>> mListObservable;

        public ResponseValue(Observable<List<Album>> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<List<Album>> getSongList(){
            return mListObservable;
        }
    }
}
