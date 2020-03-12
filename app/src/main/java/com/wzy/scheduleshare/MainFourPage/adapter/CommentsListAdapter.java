package com.wzy.scheduleshare.MainFourPage.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.wzy.scheduleshare.MainFourPage.modle.Comment;
import com.wzy.scheduleshare.R;
import java.util.List;


/**
 * @ClassName CommentsListAdapter
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.FriendViewHolder> {

    private Context mContext;

    private List<Comment> mList;
    private OnItemClickListener mListener;

    public CommentsListAdapter(Context context, List<Comment> list) {
        mList = list;
        mContext = context;
    }

    public void setDataList(List<Comment> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.comments_list_item, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, final int position) {
        holder.itemView.setTag(R.string.comment_list_item_adapter_Image_positionKey, position); //绑定Tag，用于识别点击itemView时的位置
        holder.headIcon.setTag(R.string.comment_list_item_adapter_Image_positionKey, position);

        final Comment comment = mList.get(position);
        Glide.with(mContext).
                load(comment.getAuth().getHeadIcon()).
                error(R.drawable.login_head). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())).
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(holder.headIcon);
        holder.nickName.setText(comment.getAuth().getNickname());
        holder.createAt.setText(comment.getCreatedAt());
        holder.content.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        TextView nickName;
        TextView content;
        TextView createAt;

        public FriendViewHolder(View itemView) {
            super(itemView);
            headIcon = (ImageView) itemView.findViewById(R.id.comment_list_item_headIcon);
            nickName = (TextView) itemView.findViewById(R.id.comment_list_item_nickname);
            content = (TextView) itemView.findViewById(R.id.comment_list_item_content);
            createAt = (TextView) itemView.findViewById(R.id.comment_list_item_createAt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.string.comment_list_item_adapter_Image_positionKey);
                    mListener.onItemClick(position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = (int) v.getTag(R.string.comment_list_item_adapter_Image_positionKey);
                    mListener.onLongClick(position);
                    return true;
                }
            });
            headIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.string.comment_list_item_adapter_Image_positionKey);
                    mListener.onImageClick(position);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onImageClick(int position);
        void onLongClick(int position);
    }
}
