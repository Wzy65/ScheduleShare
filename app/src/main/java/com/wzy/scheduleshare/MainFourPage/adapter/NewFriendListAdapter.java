package com.wzy.scheduleshare.MainFourPage.adapter;

import android.content.Context;
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
import com.bumptech.glide.signature.ObjectKey;
import com.orhanobut.logger.Logger;
import com.wzy.scheduleshare.base.Utils.IMBmobUtils.Config;
import com.wzy.scheduleshare.MainFourPage.modle.NewFriend;
import com.wzy.scheduleshare.MainFourPage.presenter.inter.NewFriendPresenter;
import com.wzy.scheduleshare.R;


import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @ClassName NewFriendListAdapter
 * @Author Wei Zhouye
 * @Date 2020/3/5
 * @Version 1.0
 */
public class NewFriendListAdapter extends RecyclerView.Adapter<NewFriendListAdapter.FriendViewHolder> {

    private Context mContext;

    private List<NewFriend> mList;
    private NewFriendPresenter mPresenter;
    private OnItemClickListener mListener;

    public NewFriendListAdapter(Context context, List<NewFriend> list,NewFriendPresenter presenter) {
        mList = list;
        mContext = context;
        mPresenter=presenter;    }

    public void setDataList(List<NewFriend> list) {
        mList=list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.newfriend_list_item, parent, false);
        return new FriendViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendViewHolder holder, final int position) {
        holder.itemView.setTag(R.string.newfriendlist_item_adapter_positionKey,position); //绑定Tag，用于识别点击itemView时的位置

        final NewFriend add=mList.get(position);
        Logger.i("好友请求的头像地址："+add.getAvatar());
        Glide.with(mContext).
                load(TextUtils.isEmpty(add.getAvatar())?null:add.getAvatar()).
                error(R.drawable.login_head). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())).
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(holder.headIcon);
        holder.nickName.setText(add.getName());
        holder.msg.setText(add.getMsg());

        Integer status=add.getStatus();
        if (status == null || status == Config.STATUS_VERIFY_NONE || status == Config.STATUS_VERIFY_READED) {
            holder.btn.setText(mContext.getString(R.string.newfriendlist_item_agree));
            holder.btn.setBackground(mContext.getDrawable(R.drawable.btn_press_ripple_bg_green));
            holder.btn.setEnabled(true);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.agreeAdd(add, new SaveListener<Object>() {
                        @Override
                        public void done(Object o, BmobException e) {
                            if (e == null) {
                                holder.btn.setText(mContext.getString(R.string.newfriendlist_item_agreeAdd));
                                holder.btn.setBackgroundColor(mContext.getColor(R.color.white));
                                holder.btn.setEnabled(false);
                            } else {
                                holder.btn.setText(mContext.getString(R.string.newfriendlist_item_agree));
                                holder.btn.setBackground(mContext.getDrawable(R.drawable.btn_press_ripple_bg_green));
                                holder.btn.setEnabled(true);
                                Logger.e("添加好友失败:" + e.getMessage());
                            }
                        }
                    });
                }
            });
        }else {
            holder.btn.setText(mContext.getString(R.string.newfriendlist_item_agreeAdd));
            holder.btn.setBackgroundColor(mContext.getColor(R.color.white));
            holder.btn.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }



    public class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView headIcon;
        TextView nickName;
        TextView msg;
        TextView btn;

        public FriendViewHolder(View itemView) {
            super(itemView);
            headIcon=(ImageView)itemView.findViewById(R.id.newfriend_item_img);
            nickName=(TextView)itemView.findViewById(R.id.newfriend_item_nickname);
            msg=(TextView)itemView.findViewById(R.id.newfriend_item_msg);
            btn=(TextView) itemView.findViewById(R.id.newfriend_item_btn);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=(int)v.getTag(R.string.newfriendlist_item_adapter_positionKey);
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

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
