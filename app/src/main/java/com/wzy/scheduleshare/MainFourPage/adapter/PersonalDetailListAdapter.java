package com.wzy.scheduleshare.MainFourPage.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.scheduleshare.R;
import com.wzy.scheduleshare.base.Utils.DateUtils;
import com.wzy.scheduleshare.base.modle.User;

import java.util.List;

import cn.bmob.v3.BmobUser;

/**
 * @ClassName PersonalDetailListAdapter
 * @Author Wei Zhouye
 * @Date 2020/3/2
 * @Version 1.0
 */
public class PersonalDetailListAdapter extends RecyclerView.Adapter<PersonalDetailListAdapter.PersonalDetailViewHolder> {

    private Context mContext;
    private List<ScheduleDetail> mList;
    private OnItemClickListener mListener;
    private String headIconUrl;


    public PersonalDetailListAdapter(Context context, List<ScheduleDetail> list) {
        mContext = context;
        mList = list;
        headIconUrl = getLocalHeadIcon();
    }

    public void setDataList(List<ScheduleDetail> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonalDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.personal_list_item, parent, false);
        return new PersonalDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalDetailViewHolder holder, int position) {
        holder.itemView.setTag(R.string.personal_list_item_adapter_positionKey, position); //绑定Tag，用于识别点击itemView时的位置
        if (TextUtils.isEmpty(headIconUrl)) {
            headIconUrl = BmobUser.getCurrentUser(User.class).getHeadIcon();
        }
        Glide.with(mContext).
                load(headIconUrl).
                error(R.drawable.ic_picture_error). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())).
                into(holder.Icon);
        Logger.i("个人列表简略："+mList.get(position).getBrief());
        holder.title.setText(mList.get(position).getTitle());
        holder.content.setText(mList.get(position).getBrief());
        holder.nickname.setText( BmobUser.getCurrentUser(User.class).getNickname());
        if (mList.get(position).getUpdatedAt().contains("-")) {
            holder.updateAt.setText(mList.get(position).getUpdatedAt());
        } else {
            holder.updateAt.setText(DateUtils.getDateToString(Long.valueOf(mList.get(position).getUpdatedAt())));
        }
        holder.startAt.setText(DateUtils.getDateToString(Long.valueOf(mList.get(position).getStartAt())));
        holder.endAt.setText(DateUtils.getDateToString(Long.valueOf(mList.get(position).getEndAT())));

    }

    private String getLocalHeadIcon() {
        SharedPreferences settings = mContext.getSharedPreferences("UserInfo", 0);
        return settings.getString("LocalHeadIcon", null);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    public class PersonalDetailViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView updateAt;
        ImageView Icon;
        TextView nickname;
        TextView content;
        TextView startAt;
        TextView endAt;

        public PersonalDetailViewHolder(final View itemView) {
            super(itemView);
            Icon = (ImageView) itemView.findViewById(R.id.personal_list_item_userIcon);
            title = (TextView) itemView.findViewById(R.id.personal_list_item_title);
            updateAt = (TextView) itemView.findViewById(R.id.personal_list_item_updateAt);
            content = (TextView) itemView.findViewById(R.id.personal_list_item_content);
            startAt = (TextView) itemView.findViewById(R.id.personal_list_item_startAt);
            endAt = (TextView) itemView.findViewById(R.id.personal_list_item_endAt);
            nickname = (TextView) itemView.findViewById(R.id.personal_list_item_nickname);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag(R.string.personal_list_item_adapter_positionKey);
                    mListener.onItemClick(position);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = (int) v.getTag(R.string.personal_list_item_adapter_positionKey);
                    mListener.onItemLongClick(position, itemView);
                    return true;
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

        void onItemLongClick(int position, View view);
    }
}
