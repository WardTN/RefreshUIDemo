package com.example.threaddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button btn_thread;
    private Button btn_handler;
    private Button btn_timetask;
    private Button btn_Async;
    private MyTask myTask;

    private Timer timer;
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String data = (String) msg.obj;
                    textView.setText(data);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        btn_thread = findViewById(R.id.btn_thread);
        btn_Async = findViewById(R.id.btn_asy);
        btn_thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUIByThread();
            }
        });
        btn_handler = findViewById(R.id.btn_handler);
        btn_handler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUIByHandler();
            }
        });
        btn_timetask = findViewById(R.id.btn_timerTask);
        btn_timetask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUIByTimeTask();
            }
        });
        btn_Async.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUIByAsyncTask();
            }
        });
    }

    public void changeUIByThread() {
        //通过纯Thread 更新UI 从结果上是可以实现的,但是违背了Android 单线程模型的原理  即 所有的操作都应该由UI线程去执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                textView.setText("通过纯Thread  更改UI");
            }
        }).start();
    }

    public void changeUIByHandler() {
        Message message = new Message();
        message.what = 0;
        message.obj = "通过Handler 更换UI";
//        handler.sendMessage(message);
        //延时操作
//        handler.sendEmptyMessageDelayed(0, 3 * 1000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                textView.setText("Handler 通过Post执行更换信息操作");
            }
        }, 2000);

    }

    public void changeUIByAsyncTask() {
        myTask = (MyTask) new MyTask<String>().setTaskListener(new MyTask.TaskListener() {
            @Override
            public void start() {
                Log.e("CHEN", "start 开始了， 运行在主线程");
                textView.setText("任务开始了");
            }

            @Override
            public void update(int progress) {
                textView.setText("进度" + progress);
            }

            @Override
            public Object doInBackground(Object o) {
                Log.d("task--", "doInBackground ， 运行在子线程");
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);
                        myTask.onProgressUpdate(i);
                        ;  //每隔100毫秒，更新一下进度
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return "结束了";
            }

            @Override
            public void result(Object o) {
                textView.setText("" + o);
            }
        }).execute("");
    }

    public void changeUIByTimeTask() {
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.purge();
        }

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText("通过timeTask 进行 UI 更新");
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 10);
    }

}
