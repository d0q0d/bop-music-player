package order.android.com.Bop.mvp.usecase;

import java.util.List;

import order.android.com.Bop.util.Constants;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.repository.Repository;
import rx.Observable;
public class GetArtists extends UseCase<GetArtists.RequestValues,GetArtists.ResponseValue>{

    private final Repository mRepository;

    public GetArtists(Repository repository) {
        mRepository = repository;
    }

    @Override
    public ResponseValue execute(RequestValues requestValues) {
        String action = requestValues.getAction();
        switch (action) {
            case Constants.NAVIGATE_ALLSONG:
                return new ResponseValue(mRepository.getAllArtists());
            default:
                throw new RuntimeException("wrong action type");
        }
    }


    public static final class RequestValues implements UseCase.RequestValues{

        private String action;

        public RequestValues(String action) {
            this.action = action;
        }

        public String getAction(){
            return action;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {

        private final Observable<List<Artist>> mListObservable;

        public ResponseValue(Observable<List<Artist>> listObservable) {
            mListObservable = listObservable;
        }

        public Observable<List<Artist>> getArtistList(){
            return mListObservable;
        }
    }
}
