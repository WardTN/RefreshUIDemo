package com.example.threaddemo;

import android.os.AsyncTask;

/**
 * T 启动任务传见到的
 * Integer 显示进度条的参数
 * 后台执行后返回的参数的类型
 */
public class MyTask<T> extends AsyncTask<T, Integer, T> {
    private TaskListener taskListener;

    public MyTask() {

    }

    //执行预处理,运行于UI线程,可以为后台任务做一些准备工作,比如绘制一个进度条进度条控件
    @Override
    protected void onPreExecute() {
        if (taskListener != null) {
            taskListener.start();
        }
    }

    //运行于UI线程,可以对后台任务的结果做出处理,结果就是doInBackground(Params ..._)的返回值
    @Override
    protected void onPostExecute(T t) {
        if (taskListener != null) {
            taskListener.result(t);
        }
    }

    //更新子线程进度,运行于UI线程
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (taskListener != null) {
            taskListener.update(values[0]);
        }
    }

    @Override
    protected T doInBackground(T... ts) {
        if (taskListener != null) {
            return (T) taskListener.doInBackground(ts[0]);
        }
        return null;
    }

    public MyTask setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
        return this;
    }

    public interface TaskListener<T> {
        void start();

        void update(int progress);

        T doInBackground(T t);

        void result(T t);
    }

    /*
    取消一个正在执行的任务
     */
    public void cancle() {
        if (!isCancelled()) {
            cancel(true);
        }
    }
}
