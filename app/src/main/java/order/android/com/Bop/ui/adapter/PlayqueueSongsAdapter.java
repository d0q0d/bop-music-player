package order.android.com.Bop.ui.adapter;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import order.android.com.Bop.MusicPlayer;
import order.android.com.Bop.R;
import order.android.com.Bop.mvp.model.Song;
import order.android.com.Bop.util.BopUtil;

public class PlayqueueSongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private int currentlyPlayingPosition;
    private List<Song> arraylist;
    private AppCompatActivity mContext;
    private long[] songIDs;
    private Palette.Swatch mSwatch;

    public PlayqueueSongsAdapter(AppCompatActivity context, List<Song> arraylist) {
        if (arraylist == null) {
            this.arraylist = new ArrayList<>();
        } else {
            this.arraylist = arraylist;
        }
        this.mContext = context;
        this.songIDs = getSongIds();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View song = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyceler_song, viewGroup, false);
        return new ItemHolder(song);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        Song localItem;
        localItem = arraylist.get(position);

        itemHolder.title.setText(localItem.title);
        itemHolder.artist.setText(localItem.artistName);

        if (mSwatch != null) {
            itemHolder.title.setTextColor(mSwatch.getBodyTextColor());
            itemHolder.artist.setTextColor(mSwatch.getTitleTextColor());
        }   if (MusicPlayer.getCurrentAudioId() == localItem.id) {
            itemHolder.title.setTextColor(0xFF0000FF);
            itemHolder.artist.setTextColor(0xFF0000FF);
            itemHolder.popupMenu.setColorFilter(0xFF0000FF);
        } else {
            itemHolder.title.setTextColor(0xFF000000);
            itemHolder.artist.setTextColor(0xFF000000);
            itemHolder.popupMenu.setColorFilter(0xFF000000);
        }


    }

    @Override
    public int getItemCount() {
        return arraylist.size();
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
        notifyDataSetChanged();
    }

    public void setPaletteSwatch(Palette.Swatch swatch) {
        mSwatch = swatch;
        notifyDataSetChanged();
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
            popupMenu.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_clear_black_24dp));
            popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.removeFromQueue(getAdapterPosition());
                    arraylist.remove(getAdapterPosition());
                    songIDs = getSongIds();
                    notifyItemRemoved(getAdapterPosition());
                }
            });
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playAll(mContext, songIDs, getAdapterPosition(), -1, BopUtil.IdType.NA, false);
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
