package g3.viewchoosephoto.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import g3.viewchoosephoto.R;
import g3.viewchoosephoto.model.AlbumImage;
import g3.viewchoosephoto.util.FunctionUtils;
import g3.viewchoosephoto.util.ResizeView;

public class FolderAdapter extends RecyclerView.Adapter {
    public interface OnClickItemFolderListener {
        void onClickItem(int position);
    }

    private static final String keyAd = System.currentTimeMillis() + "001";
    private LinearLayout itemAdAdvanced = null;
    private Activity mContext;
    private List<AlbumImage> mLocalImages;
    private OnClickItemFolderListener mOnClickItemFolderListener;

    private int mWidth;
    private int mHeight;


    public void setOnClickItemFolderListener(OnClickItemFolderListener onClickItemFolderListener) {
        this.mOnClickItemFolderListener = onClickItemFolderListener;
    }

    public FolderAdapter(Activity context, List<AlbumImage> localImages) {
        this.mContext = context;
        this.mLocalImages = localImages;

        DisplayMetrics metrics = FunctionUtils.getDisplayInfo();
        mWidth = metrics.widthPixels;
        mHeight = (int) (mWidth / 4.5);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_folder, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder holder1 = (ItemViewHolder) holder;
        int pos = holder1.getAdapterPosition();
        holder1.setData(pos);
    }

    @Override
    public int getItemCount() {
        return mLocalImages != null ? mLocalImages.size() : 0;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNameFolder;
        private TextView tvPathFolder;

        private ImageView imgNext;
        private LinearLayout layoutAd;

        private LinearLayout llDescription;

        private ArrayList<ImageView> mArrImgVisible = new ArrayList<>();

        private int SIZE_VISIBLE = 4;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvNameFolder = (TextView) itemView.findViewById(R.id.tvNameFolder);
            tvPathFolder = (TextView) itemView.findViewById(R.id.tvPathFolder);

            imgNext = (ImageView) itemView.findViewById(R.id.imgNext);
            layoutAd = (LinearLayout) itemView.findViewById(R.id.linear_ad_pick_folder_img);

            ImageView imgFolder = (ImageView) itemView.findViewById(R.id.imgFolder);
            ImageView imgFolder1 = (ImageView) itemView.findViewById(R.id.imgFolder1);
            ImageView imgFolder2 = (ImageView) itemView.findViewById(R.id.imgFolder2);
            ImageView imgFolder3 = (ImageView) itemView.findViewById(R.id.imgFolder3);

            mArrImgVisible.add(imgFolder);
            mArrImgVisible.add(imgFolder1);
            mArrImgVisible.add(imgFolder2);
            mArrImgVisible.add(imgFolder3);

            ResizeView.resizeView(imgNext, 17, 32);

            llDescription = itemView.findViewById(R.id.item_folder_layout_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickItemFolderListener != null) {
                        mOnClickItemFolderListener.onClickItem(getLayoutPosition());
                    }
                }
            });
        }

        private void setData(int position) {
            AlbumImage albumImage = mLocalImages.get(position);

            int size = albumImage.getLocalImages().size();

            if (size < SIZE_VISIBLE) {
                for (int i = 0; i < mArrImgVisible.size(); i++) {
                    if (i < size) {
                        FunctionUtils.displaySquareImage(mContext, mArrImgVisible.get(i), new File(albumImage.getLocalImages().get(i).getPath()), mHeight);
                    } else {
                        FunctionUtils.displaySquareImage(mContext, mArrImgVisible.get(i), new File(albumImage.getLocalImages().get(0).getPath()), mHeight);
                    }
                }

            } else {
                for (int i = 0; i < mArrImgVisible.size(); i++) {
                    FunctionUtils.displaySquareImage(mContext, mArrImgVisible.get(i), new File(albumImage.getLocalImages().get(i).getPath()), mHeight);
                }
            }


            tvNameFolder.setText(albumImage.getName());
            tvPathFolder.setText(albumImage.getPath());
        }
    }
}
