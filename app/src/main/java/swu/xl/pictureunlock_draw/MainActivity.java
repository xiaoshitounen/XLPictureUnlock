package swu.xl.pictureunlock_draw;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences的名字和存储数据的键名
    private final String SHARE_NAME = "PictureUnlockSelf";
    private final String PASSWORD_KEY = "password";

    //密码
    private String firPassword = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //读取SharedPreferences存储的数据，首先要获得SharedPreferences的对象
        final SharedPreferences sharedPreferences = getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        //存储数据需要edit()
        @SuppressLint("CommitPrefEdits") final SharedPreferences.Editor edit = sharedPreferences.edit();

        //TextView
        final TextView text = findViewById(R.id.text);
        password = sharedPreferences.getString(PASSWORD_KEY, "");
        if (password.length() == 0){
            //设置文本
            text.setText("请设置密码");
        }else {
            //设置文本
            text.setText("请输入密码");
        }

        //Button
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清空密码
                edit.putString(PASSWORD_KEY, "");
                //异步提交
                edit.apply();
                firPassword = "";
                password = "";

                //设置文本
                text.setText("请重新设置密码");
            }
        });

        //XLPictureUnlock
        XLPictureUnlock pictureUnlock = findViewById(R.id.unlock);
        //找到添加点的布局
        RelativeLayout layout = findViewById(R.id.layout);
        //添加点
        pictureUnlock.addDotView(layout);
        //监听密码
        pictureUnlock.setCallBackPasswordListener(new XLPictureUnlock.CallBackPasswordListener() {
            @Override
            public void picturePassword(String pwd) {
                if (password.length() == 0){
                    if (firPassword.length() == 0){
                        //第一次设置密码
                        firPassword = pwd;

                        //设置文本
                        text.setText("请再次输入密码以确认");
                    }else {
                        //第二次设置密码
                        if (firPassword.equals(pwd)){
                            //密码设置成功
                            //设置文本
                            text.setText("密码设置成功");

                            //保存
                            edit.putString(PASSWORD_KEY, pwd);
                            //异步提交
                            edit.apply();

                            //清楚第一次密码
                            firPassword = "";

                            jump();
                        }else {
                            //设置文本
                            text.setText("两次密码不一致，请重新输入");
                        }
                    }
                }else {
                    if (password.equals(pwd)){
                        //设置文本
                        text.setText("密码正确");

                        jump();
                    }else {
                        //设置文本
                        text.setText("密码错误");
                    }
                }
            }
        });
    }

    /**
     * 跳转界面
     */
    private void jump(){
        startActivity(new Intent(this,SecondActivity.class));
    }
}
