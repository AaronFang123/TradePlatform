package com.commodity.trade.ui.page.share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.commodity.mylibrary.util.BitmapUtil;
import com.commodity.mylibrary.util.ViewUtil;
import com.commodity.trade.R;
import com.commodity.trade.entity.user.User;
import com.commodity.trade.util.Config;
import com.commodity.trade.util.QrUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * @author Aaron
 */
public class ShareQrCodeActivity extends AppCompatActivity {
    private static final int FLAG_SELECT_PIC = 0x5379;

    @BindView(R.id.announce_detail_back)
    ImageView announceDetailBack;
    @BindView(R.id.tv_order_share)
    TextView tvOrderShare;
    @BindView(R.id.tv_share_name)
    TextView tvOrderName;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.tv_qr_post_invite_code)
    TextView tvQrPostInviteCode;
    @BindView(R.id.iv_order_qr_code)
    ImageView ivOrderQrCode;
    @BindView(R.id.cl_qr_post)
    ConstraintLayout clQrPost;
    @BindView(R.id.btn_qr_post_save)
    Button btnQrPostSave;
    @BindView(R.id.order_qr_post_share)
    Button orderQrPostShare;
    private User currentUser;
    String inviteCodeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_qr_code);
        ButterKnife.bind(this);
        this.currentUser = BmobUser.getCurrentUser(User.class);
        inviteCodeString = Config.PREFIX_INVITE_R + currentUser.getObjectId().substring(0,6);
        initMyQrCode();
        tvOrderName.setText(currentUser.getUsername());
        tvQrPostInviteCode.setText(currentUser.getObjectId().substring(0,6));
    }

    private void initMyQrCode() {
        Bitmap bitmap = QrUtil.createQrCodeBitmap(inviteCodeString,
                800, 800,
                "UTF-8", "H", "1", Color.BLACK, Color.WHITE);
        ivOrderQrCode.setImageBitmap(bitmap);
    }

    @OnClick({R.id.announce_detail_back, R.id.btn_qr_post_save, R.id.order_qr_post_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.announce_detail_back:
                finish();
                break;
            case R.id.btn_qr_post_save:
                saveToLocal();
                break;
            case R.id.order_qr_post_share:
                // 保存到本地
                saveToLocal();
                // 选择图片
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, FLAG_SELECT_PIC);
                break;
            default:
        }
    }

    private void saveToLocal(){
        Bitmap bitmap = ViewUtil.getBitmapByView(clQrPost);
        BitmapUtil.saveBitmapToLocal(ShareQrCodeActivity.this, bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAG_SELECT_PIC && data != null) {
            Uri uri = data.getData();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent,"分享到"));
            Log.e("tag", uri != null ? uri.toString() : "");
        }
    }
}