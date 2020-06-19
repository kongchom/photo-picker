package g3.viewchoosephoto.ui;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import g3.viewchoosephoto.R;
import g3.viewchoosephoto.model.LocalImage;
import g3.viewchoosephoto.util.FunctionUtils;

public class PhotoAdapter extends RecyclerView.Adapter {
    public interface OnClickItemPhotoListener {
        void onClickItemPhoto(int position);
    }

    private final int mSizeImage;
    private Context mContext;
    private List<LocalImage> mLocalImages;
    private OnClickItemPhotoListener mOnClickItemHomeListener;

    private int mWidth;
    private int mHeight;

    public void setOnClickItemPhotoListener(OnClickItemPhotoListener onClickItemHomeListener) {
        this.mOnClickItemHomeListener = onClickItemHomeListener;
    }

    public PhotoAdapter(Context context, List<LocalImage> localImages) {
        this.mContext = context;
        this.mLocalImages = localImages;

        DisplayMetrics metrics = FunctionUtils.getDisplayInfo();
        mWidth = metrics.widthPixels;
        mHeight = (int) (mWidth / 4.5);

        mSizeImage = mWidth / 3;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String link = mLocalImages.get(position).getPath();
        if (!FunctionUtils.isBlank(link)) {
            FunctionUtils.displaySquareImage(mContext, ((ItemViewHolder)holder).mImgPhoto, new File(link), mSizeImage);
        }
    }

    @Override
    public int getItemCount() {
        return mLocalImages != null ? mLocalImages.size() : 0;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImgPhoto;
        private RelativeLayout mImgRlBorder;

        ItemViewHolder(View itemView) {
            super(itemView);
            mImgPhoto = (ImageView) itemView.findViewById(R.id.imgPhoto);
            mImgRlBorder = itemView.findViewById(R.id.item_photo_layout_border);

            mImgPhoto.getLayoutParams().height = mSizeImage;
            mImgPhoto.getLayoutParams().width = mSizeImage;

            mImgRlBorder.getLayoutParams().height = mSizeImage;
            mImgRlBorder.getLayoutParams().width = mSizeImage;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickItemHomeListener != null) {
                mOnClickItemHomeListener.onClickItemPhoto(getLayoutPosition());
            }
        }
    }
}
