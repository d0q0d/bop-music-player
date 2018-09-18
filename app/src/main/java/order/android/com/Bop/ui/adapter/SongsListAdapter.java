package order.android.com.Bop.ui.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.Constants;
import order.android.com.Bop.util.BopUtil;

public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private boolean withHeader;
    private float topPlayScore;
    private String action;

    public SongsListAdapter(AppCompatActivity context, List<Song> arraylist, String action, boolean withHeader) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;

        }
        this.mContext = context;
        this.songIDs = getSongIds();
        this.withHeader = withHeader;
        this.action = action;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && withHeader) {
            return Type.TYPE_PLAY_SHUFFLE;
        } else {
            return Type.TYPE_SONG;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case Type.TYPE_PLAY_SHUFFLE:
                View playShuffle = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_play_shuffle, viewGroup, false);
                ImageView imageView = (ImageView) playShuffle.findViewById(R.id.play_shuffle);
                viewHolder = new PlayShuffleViewHoler(playShuffle);
                break;
            case Type.TYPE_SONG:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
                viewHolder = new ItemHolder(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case Type.TYPE_PLAY_SHUFFLE:
                break;
            case Type.TYPE_SONG:
                ItemHolder itemHolder = (ItemHolder) holder;
                Song localItem;
                if (withHeader) {
                    localItem = arraylist.get(position - 1);
                } else {
                    localItem = arraylist.get(position);
                }

                itemHolder.title.setText(localItem.title);
                itemHolder.artist.setText(localItem.artistName);

                if (MusicPlayer.getCurrentAudioId() == localItem.id) {
                    itemHolder.title.setTextColor(0xFF0000FF);
                    itemHolder.artist.setTextColor(0xFF0000FF);
                    itemHolder.popupMenu.setColorFilter(0xFF0000FF);
                } else {
                    itemHolder.title.setTextColor(0xFF000000);
                    itemHolder.artist.setTextColor(0xFF000000);
                    itemHolder.popupMenu.setColorFilter(0xFF000000);
                }


                setOnPopupMenuListener(itemHolder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (withHeader && arraylist.size() != 0) {
            return (null != arraylist ? arraylist.size() + 1 : 0);
        } else {
            return (null != arraylist ? arraylist.size() : 0);
        }
    }

    private void setOnPopupMenuListener(ItemHolder itemHolder, final int position) {

        final int realSongPosition;
        if (withHeader) {
            realSongPosition = position - 1;
        } else {
            realSongPosition = position;
        }

        itemHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PopupMenu menu = new PopupMenu(mContext, v);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popup_song_play_next:
                                long[] ids = new long[1];
                                ids[0] = arraylist.get(realSongPosition).id;
                                MusicPlayer.playNext(mContext, ids, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_queue:
                                long[] id = new long[1];
                                id[0] = arraylist.get(realSongPosition).id;
                                MusicPlayer.addToQueue(mContext, id, -1, BopUtil.IdType.NA);
                                break;
                            case R.id.popup_song_addto_playlist:

                                BopUtil.showAddPlaylistDialog(mContext, new long[]{arraylist.get(realSongPosition).id});
                                break;
                            case R.id.popup_song_share:
                                BopUtil.shareTrack(mContext, arraylist.get(realSongPosition).id);
                                break;
                            case R.id.popup_song_delete:
                                long[] deleteIds = {arraylist.get(realSongPosition).id};
                                switch (action) {
                                    case Constants.NAVIGATE_PLAYLIST_FAVOURATE:
                                        BopUtil.showDeleteFromFavourate(mContext, deleteIds);
                                        break;
                                    case Constants.NAVIGATE_PLAYLIST_RECENTPLAY:
                                        BopUtil.showDeleteFromRecentlyPlay(mContext, deleteIds);
                                        break;
                                    default:
                                        BopUtil.showDeleteDialog(mContext, arraylist.get(realSongPosition).title, deleteIds,
                                                new MaterialDialog.SingleButtonCallback() {
                                                    @Override
                                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                        arraylist.remove(realSongPosition);
                                                        songIDs = getSongIds();
                                                        notifyItemRemoved(position);
                                                    }
                                                });
                                        break;
                                }
                                break;
                        }
                        return false;
                    }
                });
                menu.inflate(R.menu.popup_song);
                menu.show();
            }
        });
    }

    public long[] getSongIds() {
        int songNum = arraylist.size();
        long[] ret = new long[songNum];
        for (int i = 0; i < songNum; i++) {
            ret[i] = arraylist.get(i).id;
        }

        return ret;
    }

    public void setSongList(List<Song> arraylist) {
        this.arraylist = arraylist;
        this.songIDs = getSongIds();
        if (arraylist.size() != 0) {
            this.topPlayScore = arraylist.get(0).getPlayCountScore();
        }
        notifyDataSetChanged();
    }

    public static class Type {
        public static final int TYPE_PLAY_SHUFFLE = 0;
        public static final int TYPE_SONG = 1;
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView artist;
        private ImageView popupMenu;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.txt1);
            this.artist = (TextView) view.findViewById(R.id.txt2);
            this.popupMenu = (ImageView) view.findViewById(R.id.popup_menu2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, getAdapterPosition() - 1, -1, BopUtil.IdType.NA, false);
                }
            }, 100);

        }
    }

    public class PlayShuffleViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener {
        public PlayShuffleViewHoler(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, -1, -1, BopUtil.IdType.NA, false);
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(currentlyPlayingPosition);
                            notifyItemChanged(getAdapterPosition());
                            currentlyPlayingPosition = getAdapterPosition();
                        }
                    }, 50);
                }
            }, 100);
        }
    }

}


