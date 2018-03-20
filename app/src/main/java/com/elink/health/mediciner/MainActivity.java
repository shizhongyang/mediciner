package com.elink.health.mediciner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elink.health.mediciner.utils.ImageCompress;
import com.elink.health.mediciner.utils.SecurityUtil;
import com.jaydenxiao.common.base.BaseActivity;
import com.yuyh.library.imgsel.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    //测试图片的存位置
    private String picPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "test.jpg";

    @BindView(R.id.tv)
    TextView textView;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {

        
        //System.out.println("-----------"+textView);
    }


    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                /*String decode = SecurityUtil.encode("123440");
                System.out.println("加密后----"+decode);

                String decode1 = SecurityUtil.decode(decode);
                System.out.println("解密后----"+decode1);*/

                try {
                    //必须为128或192或256bits.也就是16或24或32byte。否则会报错
                    String str = SecurityUtil.encrypt("实行");
                    LogUtils.i("加密后：" + str);

                    byte[] bytes = str.getBytes("UTF-8");
                    byte[] base64TextToDecrypt = Base64.
                            decode(str.getBytes("UTF-8"), Base64.DEFAULT);

                    String decrypt = SecurityUtil.decrypt(str);

                    LogUtils.i("解密后：" + decrypt);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn2:
                Snackbar.make(view, "这是massage", Snackbar.LENGTH_LONG).show();
                break;
            case R.id.btn3:
                compress();
                break;
            case R.id.btn4:
                sendCode("86", "15981863579");
                break;
            case R.id.btn5:
                String token = "87wvxWiy4QIKOrQ8VUhohtVaZw3qXOXX7KNZyivYYlDskQxddDBEfqe9ENZeVNXxzCKCPT1U3tdIt0yvP74PxBLQQCRSTqyP";
                RongIM.connect(token, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        Log.e(TAG, "reToken Incorrect");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "reToken onSuccess");
                        // startActivity(new Intent(MainActivity.this, ChatActivity.class));
                        RongIM.getInstance().startPrivateChat(MainActivity.this, "s1234568", "标题");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
                break;
            case R.id.btn6:
                String token1 = "29wQtiriSZi4WXBi4C1KIYebHl8aHtlAZc+u4hC6V2q6U3" +
                        "tqqyB3zt8CHPu4XOvB75P+L3xpRwfctTaTtro4Shtb48JUezAG";
                RongIM.connect(token1, new RongIMClient.ConnectCallback() {
                    @Override
                    public void onTokenIncorrect() {
                        Log.e(TAG, "reToken Incorrect");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "reToken onSuccess");
                        // startActivity(new Intent(MainActivity.this, ChatActivity.class));
                        RongIM.getInstance().startPrivateChat(MainActivity.this,
                                "s1234567", "标题");
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode e) {

                    }
                });
                break;
        }

    }

    private void compress() {
        File file = new File(picPath);

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        ImageCompress.nativeCompressBitmap(bitmap, 20,
                "/sdcard/hfresult.jpg", true);

        try {

            bitmap.compress(Bitmap.CompressFormat.JPEG, 20,
                    new FileOutputStream("sdcard/result.jpg"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
    public void sendCode(String country, String phone) {
        // 注册一个事件回调，用于处理发送验证码操作的结果
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理成功得到验证码的结果
                    // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                } else {
                    // TODO 处理错误的结果
                }

                // 用完回调要注销，否则会造成泄露
                SMSSDK.unregisterEventHandler(this);
            }
        });
        // 触发操作
        SMSSDK.getVerificationCode(country, phone);
    }

}
