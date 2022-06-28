package com.fengxuechao.jvm.async;

import java.util.Date;

public class AsyncDemoThead {
  private void subBiz1() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "subBiz1");
  }

  private void subBiz2() throws InterruptedException {
    Thread.sleep(1000L);
    System.out.println(new Date() + "biz2");
  }


  private void saveOpLog() throws InterruptedException {
   new Thread(new SaveOpLogThread()).start();
  }

  private void biz() throws InterruptedException {
    this.subBiz1();
    this.saveOpLog();
    this.subBiz2();

    System.out.println(new Date() + "执行结束");
  }

  public static void main(String[] args) throws InterruptedException {
    new AsyncDemoThead().biz();
  }
}

class SaveOpLogThread implements Runnable{

  @Override
  public void run() {
    try {
      Thread.sleep( 1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(new Date() + "插入操作日志");
  }
}