package com.wzy.schedulingshare.MainFourPage.adapter;

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
import com.wzy.schedulingshare.MainFourPage.modle.Comment;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.Utils.DateUtils;

import java.util.List;


/**
 * @ClassName MainLHorizontalAdapter
 * @Author Wei Zhouye
 * @Date 2020/3/12
 * @Version 1.0
 */
public class MainLHorizontalAdapter extends RecyclerView.Adapter<MainLHorizontalAdapter.FriendViewHolder> {

    private Context mContext;

    private List<ScheduleDetail> mList;
    private OnItemClickListener mListener;

    public MainLHorizontalAdapter(Context context, List<ScheduleDetail> list) {
        mList = list;
        mContext = context;
    }

    public void setDataList(List<ScheduleDetail> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.main_horizontal_item, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, final int position) {
        holder.itemView.setTag(R.string.main_horizontal_list_item_adapter_positionKey, position); //绑定Tag，用于识别点击itemView时的位置

        final ScheduleDetail detail = mList.get(position);
        Glide.with(mContext).
                load(detail.getAuth().getHeadIcon()).
                error(R.drawable.login_head). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())).
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(holder.headIcon);
        holder.nickName.setText(detail.getAuth().getNickname());
        holder.startAt.setText(DateUtils.getDateToString(Long.valueOf(detail.getStartAt())));
        holder.content.setText(detail.getBrief());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        TextView nickName;
        TextView content;
        TextView startAt;

        public FriendViewHolder(View itemView) {
            super(itemView);
            headIcon = (ImageView) itemView.findViewById(R.id.main_horizontal_item_headIcon);
            nickName = (TextView) itemView.findViewById(R.id.main_horizontal_item_nickname);
            startAt= (TextView) itemView.findViewById(R.id.mian_horizontal_item_startdAt);
            content = (TextView) itemView.findViewById(R.id.mian_horizontal_item_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.string.main_horizontal_list_item_adapter_positionKey);
                    mListener.onItemClick(position);
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
    }
}
