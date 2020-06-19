package g3.viewchoosephoto.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import g3.viewchoosephoto.R;
import g3.viewchoosephoto.model.LocalImage;
import g3.viewchoosephoto.util.FunctionUtils;

public class PhotoChooseAdapter extends RecyclerView.Adapter<PhotoChooseAdapter.ItemViewHolder> {

    public interface OnClickRemoveItemListener {
        void onRemoveItem(int position);
    }

    private Context mContext;
    private List<LocalImage> mLocalImages;
    private OnClickRemoveItemListener mOnClickRemoveItemListener;
    private int mSizeImage;

    public PhotoChooseAdapter(Context context, List<LocalImage> localImages) {
        this.mContext = context;
        this.mLocalImages = localImages;

        DisplayMetrics displayMetrics = FunctionUtils.getDisplayInfo();
        mSizeImage = (int) (displayMetrics.widthPixels / 5);
    }

    public void setOnClickRemoveItemListener(OnClickRemoveItemListener mOnClickRemoveItemListener) {
        this.mOnClickRemoveItemListener = mOnClickRemoveItemListener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photo_choose, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (mLocalImages != null && position < mLocalImages.size()) {
            final String imgPath = mLocalImages.get(position).getPath();
            if (!FunctionUtils.isBlank(imgPath)) {
                FunctionUtils.displaySquareImageCenterInside(mContext, holder.imgChoose, new File(imgPath), mSizeImage);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLocalImages != null ? mLocalImages.size() : 0;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgRemove;
        private ImageView imgChoose;

        private RelativeLayout rlImgChoose;

        ItemViewHolder(View itemView) {
            super(itemView);

            imgRemove = itemView.findViewById(R.id.imgRemove);
            imgChoose = itemView.findViewById(R.id.imgChoose);

            rlImgChoose = itemView.findViewById(R.id.item_photo_choose_layout_img);

            RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(mSizeImage,mSizeImage);
            rlParams.setMargins((int)mContext.getResources().getDimension(R.dimen._5sdp),
                    (int)mContext.getResources().getDimension(R.dimen._5sdp),0,0);
            rlImgChoose.setLayoutParams(rlParams);

            imgChoose.getLayoutParams().width = mSizeImage;
            imgChoose.getLayoutParams().height = mSizeImage;
            imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnClickRemoveItemListener != null) {
                        mOnClickRemoveItemListener.onRemoveItem(getLayoutPosition());
                    }
                }
            });
        }
    }
}
