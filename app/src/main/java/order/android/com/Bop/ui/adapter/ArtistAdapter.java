package order.android.com.Bop.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import order.android.com.Bop.BopApp;
import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.api.model.ArtistInfo;
import order.android.com.Bop.api.model.Artwork;
import order.android.com.Bop.dataloader.ArtistSongLoader;
import order.android.com.Bop.injector.component.ApplicationComponent;
import order.android.com.Bop.injector.component.ArtistInfoComponent;
import order.android.com.Bop.injector.component.DaggerArtistInfoComponent;
import order.android.com.Bop.injector.module.ArtistInfoModule;
import order.android.com.Bop.mvp.model.Artist;
import order.android.com.Bop.mvp.model.ArtistArt;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.mvp.usecase.GetArtistInfo;
import order.android.com.Bop.util.ATEUtil;
import order.android.com.Bop.util.ColorUtil;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.NavigationUtil;
import order.android.com.Bop.util.PreferencesUtility;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ItemHolder> {
    @Inject
    GetArtistInfo getArtistInfo;
    private List<Artist> arraylist;
    private Activity mContext;
    private String action;
    private boolean isGrid;

    public ArtistAdapter(Activity context, List<Artist> arraylist) {
        this.arraylist = arraylist;
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
        injectDependences(context);
    }

    public ArtistAdapter(Activity context, String action) {
        this.mContext = context;
        this.isGrid = PreferencesUtility.getInstance(mContext).isArtistsInGrid();
        this.action = action;
        injectDependences(context);

    }
    private void injectDependences(Activity context) {
        ApplicationComponent applicationComponent = ((BopApp) context.getApplication()).getApplicationComponent();
        ArtistInfoComponent artistInfoComponent = DaggerArtistInfoComponent.builder()
                .applicationComponent(applicationComponent)
                .artistInfoModule(new ArtistInfoModule())
                .build();
        artistInfoComponent.injectForAdapter(this);
    }

    public void setArtistList(List<Artist> arraylist) {
        this.arraylist = arraylist;
        notifyDataSetChanged();
    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_grid_layout_item, viewGroup, false);
        return new ItemHolder(v);
    }


    @Override
    public void onBindViewHolder(final ItemHolder itemHolder, int i) {
        final Artist localItem = arraylist.get(i);

        itemHolder.name.setText(localItem.name);
        itemHolder.songCount.setText(BopUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount));
        setOnPopupMenuListener(itemHolder, i);
        String artistArtJson = PreferencesUtility.getInstance(mContext).getArtistArt(localItem.id);
        if (TextUtils.isEmpty(artistArtJson)) {
            getArtistInfo.execute(new GetArtistInfo.RequestValues(localItem.name))
                    .getArtistInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorReturn(new Func1<Throwable, ArtistInfo>() {
                        @Override
                        public ArtistInfo call(Throwable throwable) {
                            return null;
                        }
                    })
                    .subscribe(new Action1<ArtistInfo>() {
                        @Override
                        public void call(ArtistInfo artistInfo) {
                            if (artistInfo != null && artistInfo.mArtist != null && artistInfo.mArtist.mArtwork != null) {
                                List<Artwork> artworks = artistInfo.mArtist.mArtwork;
                                ArtistArt artistArt = new ArtistArt(artworks.get(0).mUrl, artworks.get(1).mUrl,
                                        artworks.get(2).mUrl, artworks.get(3).mUrl);
                                PreferencesUtility.getInstance(mContext).setArtistArt(localItem.id, new Gson().toJson(artistArt));
                                loadArtistArt(artistArt, itemHolder);
                            }
                        }
                    });

        }else {
            ArtistArt artistArt = new Gson().fromJson(artistArtJson, ArtistArt.class);
            loadArtistArt(artistArt, itemHolder);
        }
    }

    private void loadArtistArt(ArtistArt artistArt, final ItemHolder itemHolder) {
            Glide.with(mContext)
                    .load(artistArt.getExtralarge())
                    .asBitmap()
                    .placeholder(ATEUtil.getDefaultSingerDrawable(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            itemHolder.artistImage.setImageDrawable(ATEUtil.getDefaultSingerDrawable(mContext));
                            itemHolder.name.setTextColor(mContext.getResources().getColor(R.color.black));
                            itemHolder.songCount.setTextColor(mContext.getResources().getColor(R.color.black));
                            itemHolder.popupMenu.setColorFilter(mContext.getResources().getColor(R.color.black));
                        }

                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            new Palette.Builder(resource).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    Palette.Swatch swatch = ColorUtil.getMostPopulousSwatch(palette);
                                    if (swatch != null) {
                                        itemHolder.artistImage.setImageBitmap(resource);
                                        itemHolder.name.setTextColor(mContext.getResources().getColor(R.color.black));
                                        itemHolder.songCount.setTextColor(mContext.getResources().getColor(R.color.black));
                                        itemHolder.popupMenu.setColorFilter(mContext.getResources().getColor(R.color.black));
                                    }
                                }
                            });
                        }
                    });

    }
    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    private void setOnPopupMenuListener(final ArtistAdapter.ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                int adapterPosition = itemHolder.getAdapterPosition();
                final Artist artist = arraylist.get(adapterPosition);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_artist_addto_queue:
                                getSongListIdByArtist(arraylist.get(position).id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                MusicPlayer.addToQueue(mContext, ids, -1, BopUtil.IdType.NA);
                                            }
                                        });
                                break;
                            case R.id.popup_artist_addto_playlist:
                                getSongListIdByArtist(arraylist.get(position).id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                BopUtil.showAddPlaylistDialog(mContext,ids);
                                            }
                                        });
                                break;
                            case R.id.popup_artist_delete:
                                switch (action) {
                                    case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                                        getSongListIdByArtist(artist.id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<long[]>() {
                                                    @Override
                                                    public void call(long[] ids) {
                                                        BopUtil.showDeleteFromFavourate(mContext,ids);
                                                    }
                                                });
                                        break;
                                    case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
                                        getSongListIdByArtist(artist.id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<long[]>() {
                                                    @Override
                                                    public void call(long[] ids) {
                                                        BopUtil.showDeleteFromRecentlyPlay(mContext,ids);
                                                    }
                                                });
                                        break;
                                    default:
                                        ArtistSongLoader.getSongsForArtist(mContext,arraylist.get(position).id)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Action1<List<Song>>() {
                                                    @Override
                                                    public void call(List<Song> songs) {
                                                        long[] ids = new long[songs.size()];
                                                        int i = 0;
                                                        for (Song song : songs) {
                                                            ids[i] = song.id;
                                                            i++;
                                                        }
                                                        if (ids.length == 1) {
                                                            BopUtil.showDeleteDialog(mContext, songs.get(0).title, ids,
                                                                    new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            arraylist.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    });
                                                        } else {
                                                            String songCount = BopUtil.makeLabel(mContext,
                                                                    R.plurals.Nsongs, arraylist.get(position).songCount);
                                                            BopUtil.showDeleteDialog(mContext, songCount, ids,
                                                                    new MaterialDialog.SingleButtonCallback() {
                                                                        @Override
                                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                            arraylist.remove(position);
                                                                            notifyDataSetChanged();
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                        break;
                                }
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_artist);
                menu.show();
            }
        });
    }


    private Observable<long[]> getSongListIdByArtist(long id) {
        return ArtistSongLoader.getSongsForArtist(mContext, id)
                .map(new Func1<List<Song>, long[]>() {
                    @Override
                    public long[] call(List<Song> songs) {
                        long[] ids = new long[songs.size()];
                        int i = 0;
                        for (Song song : songs) {
                            ids[i] = song.id;
                            i++;
                        }
                        return ids;
                    }
                });
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView artistImage;
        private ImageView popupMenu;
        private TextView name;
        private TextView songCount;

        public ItemHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.text_item_title);
            this.songCount = (TextView) view.findViewById(R.id.text_item_subtitle);
            this.artistImage = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu);
            view.setOnClickListener(this);
        }

      @Override
        public void onClick(View v) {
          NavigationUtil.navigateToArtist(mContext, arraylist.get(getAdapterPosition()).id, arraylist.get(getAdapterPosition()).name,
                  new Pair<View, String>(artistImage, "transition_artist_art" + getAdapterPosition()));
      }


    }
}




