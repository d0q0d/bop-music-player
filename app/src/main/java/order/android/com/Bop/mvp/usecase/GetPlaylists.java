package order.android.com.Bop.mvp.usecase;

import java.util.List;

import order.android.com.Bop.mvp.model.Playlist;
import order.android.com.Bop.mvp.repository.Repository;
import order.android.com.Bop.util.Constants;
import rx.Observable;
public class GetPlaylists extends UseCase<GetPlaylists.RequestValues,GetPlaylists.ResponseValue>{

    private final Repository mRepository;

    public GetPlaylists(Repository repository) { mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        return new ResponseValue(mRepository.getPlaylists(requestValues.defaultIncluded));


    }

    public static final class RequestValues implements UseCase.RequestValues{

        private boolean defaultIncluded;

        public RequestValues(boolean defaultIncluded) {
            this.defaultIncluded = defaultIncluded;
        }

    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<List<Playlist>> mListObservable;

        public ResponseValue(Observable<List<Playlist>> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<List<Playlist>> getPlaylists(){
            return mListObservable;
        }
    }
}
