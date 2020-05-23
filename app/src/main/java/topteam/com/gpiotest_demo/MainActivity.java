package topteam.com.gpiotest_demo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sdk_d80.D80;
import com.example.sdk_d80.D80Serial;
import com.example.sdk_d80.MsgRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, D80Serial.D80Interface {

    TextView showio_v;
    EditText settime_v;
    Button set_v;
    TextView ok_v;
    TextView ng_v;
    ImageView io_v;
    Button cle_v;
    String showTime1;
    String showTime2;
    int settime;
    int ioState;
    Handler handler;
    boolean flg = false;
    long startTime;
    long endTime;
    long contTime;
    int okio;
    int ngio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        showio_v = (TextView) findViewById(R.id.showio);
        set_v = (Button) findViewById(R.id.set);
        set_v.setOnClickListener(this);
        settime_v = (EditText) findViewById(R.id.settime);
        ok_v = (TextView) findViewById(R.id.ok);
        ng_v = (TextView) findViewById(R.id.ng);
        io_v = (ImageView) findViewById(R.id.io);
        cle_v = (Button) findViewById(R.id.cle);
        cle_v.setOnClickListener(this);
        //初始化GPIO
        iniGpio();

        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        io_v.setImageResource(R.drawable.io1);
                        showio_v.append(showTime1 + "    " + ioState + "\n");
                        showio_v.setMovementMethod(ScrollingMovementMethod.getInstance());
                        break;
                    case 1:
                        io_v.setImageResource(R.drawable.io0);
                        showio_v.append(showTime2 + "   " + ioState + "\n");
                        showio_v.setMovementMethod(ScrollingMovementMethod.getInstance());
                        if(contTime>settime){
                            okio++;
                            ok_v.setText(okio+"");
                        }else {
                            ngio++;
                            ng_v.setText(ngio+"");
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.set:
                String ss = settime_v.getText().toString();
                settime = Integer.parseInt(ss);
                Toast.makeText(MainActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.cle:
                showio_v.setText("");
                ok_v.setText("");
                ng_v.setText("");
                okio=0;
                ngio=0;
                break;
            default:
                break;
        }
    }

    @Override
    public void CallBackMethod(final MsgRecord Value) {

        ioState = Value.TotalState[2];
        switch (ioState) {
            case 0:
                startTime = System.currentTimeMillis();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                showTime1 = sdf.format(date);
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
                break;
            case 1:
                Date date2 = new Date();
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                showTime2 = sdf2.format(date2);
                endTime = System.currentTimeMillis();
                contTime = (endTime - startTime);
                Message message1 = new Message();
                message1.what = 1;
                handler.sendMessage(message1);
                break;
            default:
                break;
        }


    }

    /**
     * 处理io信号的相关逻辑
     */
    private void iniGpio() {
        if (Upublis.comm.equals("")) {
            if (D80.OpenIODev()) {
                android.widget.Toast.makeText(MainActivity.this, "程序启动成功", android.widget.Toast.LENGTH_SHORT).show();
                //android.widget.Toast.makeText(mComtext, "", android.widget.Toast.LENGTH_SHORT).show();

            }/*else {
                android.widget.Toast.makeText(mComtext, "程序启动失败", android.widget.Toast.LENGTH_SHORT).show();
            }*/
        } else {
            if (D80.OpenIODevDebug(Upublis.comm))
                // android.widget.Toast.makeText(mComtext, "", android.widget.Toast.LENGTH_SHORT).show();
                android.widget.Toast.makeText(MainActivity.this, "已经成功连接采集器", android.widget.Toast.LENGTH_SHORT).show();
            else
                android.widget.Toast.makeText(MainActivity.this, "连接采集器失败", android.widget.Toast.LENGTH_SHORT).show();

        }


        boolean b = D80.SetParameter(0, 1);//过滤时间+  0不上报，1上下跳变，2上跳变，3下跳变
        if (b) {
            android.widget.Toast.makeText(MainActivity.this, "设置参数成功", android.widget.Toast.LENGTH_SHORT).show();
            //android.widget.Toast.makeText(mComtext, "", android.widget.Toast.LENGTH_SHORT).show();
        } else {
            android.widget.Toast.makeText(MainActivity.this, "设置参数失败", android.widget.Toast.LENGTH_SHORT).show();

        }
        if (D80.CallBackIOData(MainActivity.this)) {
            android.widget.Toast.makeText(MainActivity.this, "设置接收采集成功", android.widget.Toast.LENGTH_SHORT).show();
            //android.widget.Toast.makeText(MainActivity.this, "", android.widget.Toast.LENGTH_SHORT).show();
        } else
            android.widget.Toast.makeText(MainActivity.this, "设置接收采集失败", android.widget.Toast.LENGTH_SHORT).show();

    }


}
