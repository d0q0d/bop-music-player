package order.android.com.Bop.ui.adapter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.dataloader.PlaylistLoader;
import order.android.com.Bop.dataloader.PlaylistSongLoader;
import order.android.com.Bop.mvp.model.Playlist;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.BopUtil;
import order.android.com.Bop.util.NavigationUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Playlist> arraylist;
    private Activity mContext;

    public PlaylistAdapter(Activity context, List<Playlist> arraylist) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;
        }
        this.mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return Type.TYPE_PLAYLIST;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
        viewHolder = new ItemHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        final Playlist localItem;
        ItemHolder itemHolder = (ItemHolder) holder;
        localItem = arraylist.get(i);
        itemHolder.title.setText(localItem.name);
        itemHolder.songcount.setText(BopUtil.makeLabel(mContext, R.plurals.Nsongs, localItem.songCount));
        PlaylistSongLoader.getSongsInPlaylist(mContext, localItem.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Song>>() {
                    @Override
                    public void call(List<Song> playlistsongs) {
                        String uri = "";
                        long firstAlbumID = -1;
                        if (playlistsongs.size() != 0) {
                            firstAlbumID = playlistsongs.get(0).albumId;
                            uri = BopUtil.getAlbumArtUri(firstAlbumID).toString();
                        }
                    }
                });

        setOnPopupMenuListener(itemHolder, i);
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    public void setPlaylist(List<Playlist> playlists) {
        this.arraylist.clear();
        this.arraylist.addAll(playlists);
        notifyDataSetChanged();
    }

    @NonNull
    private void setOnPopupMenuListener(final ItemHolder itemHolder, final int position) {
        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final Playlist playlist = arraylist.get(itemHolder.getAdapterPosition());
                        switch (item.getItemId()) {
                            case R.id.popup_playlist_rename:
                                new MaterialDialog.Builder(mContext)
                                        .title(R.string.rename_playlist)
                                        .positiveText(R.string.change)
                                        .negativeText(R.string.cancel)
                                        .input(null, playlist.name, false, new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                                MusicPlayer.renamePlaylist(mContext, playlist.id, input.toString());
                                                itemHolder.title.setText(input.toString());
                                                Toast.makeText(mContext, R.string.rename_playlist_success, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .show();
                                break;
                            case R.id.popup_playlist_addto_playlist:
                                getSongListIdByPlaylist(playlist.id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                BopUtil.showAddPlaylistDialog(mContext, ids);
                                            }
                                        });
                                break;
                            case R.id.popup_playlist_addto_queue:
                                getSongListIdByPlaylist(playlist.id)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Action1<long[]>() {
                                            @Override
                                            public void call(long[] ids) {
                                                MusicPlayer.addToQueue(mContext, ids, -1, BopUtil.IdType.Playlist);
                                            }
                                        });
                                break;
                            case R.id.popup_playlist_delete:
                                new MaterialDialog.Builder(mContext)
                                        .title(R.string.delete_playlist)
                                        .positiveText(R.string.delete)
                                        .negativeText(R.string.cancel)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                PlaylistLoader.deletePlaylists(mContext, playlist.id);
                                                arraylist.remove(itemHolder.getAdapterPosition());
                                                notifyItemRemoved(itemHolder.getAdapterPosition());
                                                Toast.makeText(mContext, R.string.delete_playlist_success, Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_playlist);
                menu.show();
            }
        });
    }

    private Observable<long[]> getSongListIdByPlaylist(long playlistId) {
        return PlaylistSongLoader.getSongsInPlaylist(mContext, playlistId)
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

    public static class Type {
        public static final int TYPE_PLAYLIST = 1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView playlistArt;
        private TextView title;
        private TextView songcount;
        private View footer;
        private ImageView popupMenu;


        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.txt1);
            this.songcount = (TextView) view.findViewById(R.id.txt2);
            this.playlistArt = (ImageView) view.findViewById(R.id.image);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavigationUtil.navigateToPlaylistDetail(mContext, arraylist.get(getAdapterPosition()).id,
                    String.valueOf(title.getText()),
                    new Pair<View, String>(playlistArt, "transition_album_art" + getAdapterPosition()));
        }

    }


}