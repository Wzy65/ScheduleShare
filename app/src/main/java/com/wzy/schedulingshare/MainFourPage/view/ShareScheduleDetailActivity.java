package com.wzy.schedulingshare.MainFourPage.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.sendtion.xrichtext.RichTextView;
import com.wzy.schedulingshare.MainFourPage.adapter.CommentsListAdapter;
import com.wzy.schedulingshare.MainFourPage.modle.Comment;
import com.wzy.schedulingshare.MainFourPage.modle.ScheduleDetail;
import com.wzy.schedulingshare.MainFourPage.presenter.impl.ShareScheduleDetailPresenterImpl;
import com.wzy.schedulingshare.MainFourPage.presenter.inter.ShareScheduleDetailPresenter;
import com.wzy.schedulingshare.R;
import com.wzy.schedulingshare.base.Utils.DateUtils;
import com.wzy.schedulingshare.base.Utils.RecyclerViewDivider;
import com.wzy.schedulingshare.base.Utils.XRichTextStringUtils;
import com.wzy.schedulingshare.base.modle.User;
import com.wzy.schedulingshare.base.view.impl.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ClassName ShareScheduleDetailActivity
 * @Author Wei Zhouye
 * @Date 2020/3/10
 * @Version 1.0
 */
public class ShareScheduleDetailActivity extends BaseActivity<ShareScheduleDetailPresenter> implements ShareScheduleDetailPresenter.View {
    public static final String INTENT_TO_SSDA_KEY = "INTENT_TO_SSDA_KEY";

    private int collect_status = 0;
    private ScheduleDetail mDetail;
    private Disposable subsLoading;
    private CommentsListAdapter mAdapter;
    private List<Comment> mList;

    @BindView(R.id.share_schedule_detail_recycleView)
    RecyclerView mShareScheduleDetailRecycleView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.share_schedule_detail_comment_edit)
    EditText mShareScheduleDetailCommentEdit;
    @BindView(R.id.share_schedule_detail_comment_send)
    Button mShareScheduleDetailCommentSend;
    @BindView(R.id.share_schedule_detail_comment_layout)
    LinearLayout mShareScheduleDetailCommentLayout;
    @BindView(R.id.share_schedule_detail_comment)
    TextView mShareScheduleDetailComment;
    @BindView(R.id.setting_toolbar)
    Toolbar mSettingToolbar;
    @BindView(R.id.share_schedule_detail_title)
    TextView mShareScheduleDetailTitle;
    @BindView(R.id.share_schedule_detail_userIcon)
    ImageView mShareScheduleDetailUserIcon;
    @BindView(R.id.share_schedule_detail_uerName)
    TextView mShareScheduleDetailUerName;
    @BindView(R.id.share_schedule_detail_updateAt)
    TextView mShareScheduleDetailUpdateAt;
    @BindView(R.id.share_schedule_detail_startDate)
    TextView mShareScheduleDetailStartDate;
    @BindView(R.id.share_schedule_detail_endDate)
    TextView mShareScheduleDetailEndDate;
    @BindView(R.id.share_schedule_detail_contnt)
    RichTextView mShareScheduleDetailContnt;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_schedule_datail;
    }

    @Override
    public void initView() {
        mPresenter = new ShareScheduleDetailPresenterImpl(this);
        setSupportActionBar(mSettingToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); //关闭lable的显示
        getSupportActionBar().setHomeButtonEnabled(true);  //设置默认的返回图标是否可点击
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //设置显示默认的返回图标

        mDetail = (ScheduleDetail) getIntent().getSerializableExtra(INTENT_TO_SSDA_KEY);
        initData(mDetail);
        mShareScheduleDetailContnt.setOnRtImageClickListener(new RichTextView.OnRtImageClickListener() {
            @Override
            public void onRtImageClick(View view, String imagePath) {
                showBigPhoto(imagePath);
            }
        });

        if (mList == null) {
            mList = new ArrayList<>();
        }
        mAdapter = new CommentsListAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        mShareScheduleDetailRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mShareScheduleDetailRecycleView.setAdapter(mAdapter);
        mShareScheduleDetailRecycleView.addItemDecoration(new RecyclerViewDivider(this, LinearLayout.HORIZONTAL, 1, getResources().getColor(R.color.gray3)));
        mAdapter.setOnItemClickListener(new CommentsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //TODO
            }

            /*头像的点击事件*/
            @Override
            public void onImageClick(int position) {
                if (!mList.get(position).getAuth().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                    Intent intent = new Intent(ShareScheduleDetailActivity.this, UserInfoActivity.class);
                    intent.putExtra(UserInfoActivity.UserInfoKey, mList.get(position).getAuth());
                    intent.putExtra(UserInfoActivity.ShowAddFriendKey, true);
                    startActivity(intent);
                }
            }

            /*长按点击事件*/
            @Override
            public void onLongClick(int position) {
                mShareScheduleDetailComment.setVisibility(View.GONE);
                mShareScheduleDetailCommentLayout.setVisibility(View.VISIBLE);
                String temp=mShareScheduleDetailCommentEdit.getText().toString();
                mShareScheduleDetailCommentEdit.setText(temp+" @" + mList.get(position).getAuth().getNickname()+" ");
            }
        });

        mPresenter.refreshList(mDetail, mList);

        /*设置上拉和下拉监听*/
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.refreshDetail(mDetail);
                mPresenter.refreshList(mDetail, mList);
            }
        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMore(mDetail, mList);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*  rxjava虽然好用，但是总所周知，容易遭层内存泄漏。也就说在订阅了事件后没有及时取阅，
          导致在activity或者fragment销毁后仍然占用着内存，无法释放。而disposable便是这个订阅事件，可以用来取消订阅*/
        if (subsLoading != null && subsLoading.isDisposed()) {
            subsLoading.dispose();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_schedule_detail_toolbar_menu, menu);
        return true;
    }

    /*
    * 动态变换toolbar上的menu控件
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        switch (collect_status) {
            case 0:
                menu.findItem(R.id.share_schedule_detail_menu_collect).setVisible(true);
                menu.findItem(R.id.share_schedule_detail_menu_collect_cancel).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.share_schedule_detail_menu_collect).setVisible(false);
                menu.findItem(R.id.share_schedule_detail_menu_collect_cancel).setVisible(true);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.share_schedule_detail_menu_collect:
                mPresenter.addCollect(mDetail);
                break;
            case R.id.share_schedule_detail_menu_collect_cancel:
                mPresenter.cancelCollect(mDetail);
                break;
        }
        return true;
    }

    @Override
    public void initData(final ScheduleDetail detail) {
        mDetail = detail;
        mShareScheduleDetailTitle.setText(detail.getTitle());
        mShareScheduleDetailStartDate.setText(DateUtils.getDateToString(Long.valueOf(detail.getStartAt())));
        mShareScheduleDetailEndDate.setText(DateUtils.getDateToString(Long.valueOf(detail.getEndAT())));
        mShareScheduleDetailUpdateAt.setText(detail.getUpdatedAt());
        mShareScheduleDetailUerName.setText(detail.getAuth().getNickname());
        Glide.with(this).
                load(detail.getAuth().getHeadIcon()).
                error(R.drawable.ic_picture_error). //异常时候显示的图片
                placeholder(R.drawable.ic_picture_placeholder).//加载成功前显示的图片
                fallback(R.drawable.login_head).//url为空的时候,显示的图片
                apply(RequestOptions.bitmapTransform(new CircleCrop())). //显示圆形
                signature(new ObjectKey(System.currentTimeMillis())). //增加版本号，避免同一名称缓存不更新
                into(mShareScheduleDetailUserIcon);
        mShareScheduleDetailContnt.post(new Runnable() {
            @Override
            public void run() {
                dealWithContent(detail.getContent());
            }
        });
    }

    @Override
    public void refresh() {
        mRefreshLayout.autoRefresh();
        mRefreshLayout.finishRefresh(3000);
    }


    @Override
    public void refreshCommentList(List<Comment> list) {
        mList = list;
        mAdapter.setDataList(list);
    }

    @Override
    public void updateCommentList(List<Comment> list) {
        mList = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void refreshToolbar(int flag) {
        collect_status=flag;
        invalidateOptionsMenu();
    }

    /*富文本内容处理*/
    private void dealWithContent(String content) {
        mShareScheduleDetailContnt.clearAllLayout();
        showDataSync(content);
    }

    /**
     * 异步方式显示数据
     */
    private void showDataSync(final String html) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                showEditData(emitter, html);
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(R.string.photo_not_find);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subsLoading = d;
                    }

                    @Override
                    public void onNext(String text) {
                        try {
                            if (mShareScheduleDetailContnt != null) {
                                if (text.contains("<img") && text.contains("src=")) {
                                    //imagePath可能是本地路径，也可能是网络地址
                                    String imagePath = XRichTextStringUtils.getImgSrc(text);
                                    mShareScheduleDetailContnt.addImageViewAtIndex(mShareScheduleDetailContnt.getLastIndex(), imagePath);
                                } else {
                                    mShareScheduleDetailContnt.addTextViewAtIndex(mShareScheduleDetailContnt.getLastIndex(), text);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 显示数据
     */
    protected void showEditData(ObservableEmitter<String> emitter, String html) {
        try {
            List<String> textList = XRichTextStringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
            emitter.onError(e);
        }
    }

    /*
    * 点击小图展示大图
    * */
    private void showBigPhoto(String path) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_show_big, null);
        final AlertDialog dialog = new AlertDialog.Builder(ShareScheduleDetailActivity.this).create();
        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_show_big_img);
        Glide.with(ShareScheduleDetailActivity.this).load(path).into(imageView);
        dialog.setView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    @OnClick({R.id.share_schedule_detail_comment_send, R.id.share_schedule_detail_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_schedule_detail_comment_send:
                mPresenter.addComment(mShareScheduleDetailCommentEdit.getText().toString(), mDetail);
                mShareScheduleDetailCommentEdit.setText("");
                mShareScheduleDetailComment.setVisibility(View.VISIBLE);
                mShareScheduleDetailCommentLayout.setVisibility(View.GONE);
                break;
            case R.id.share_schedule_detail_comment:
                mShareScheduleDetailComment.setVisibility(View.GONE);
                mShareScheduleDetailCommentLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void finishRefresh(boolean flag) {
        if (flag) {
            mRefreshLayout.finishRefresh();
        } else {
            mRefreshLayout.finishRefresh(false);
        }
    }

    @Override
    public void finishLoadMore(boolean flag) {
        if (flag) {
            mRefreshLayout.finishLoadMore();
        } else {
            mRefreshLayout.finishLoadMore(false);
        }
    }


}
