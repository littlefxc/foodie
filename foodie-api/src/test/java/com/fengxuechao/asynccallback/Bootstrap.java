package com.fengxuechao.asynccallback;

/**
 * 主逻辑
 * @author fengxuechao
 * @date 2021/9/3
 */
public class Bootstrap {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        Worker worker = bootstrap.newWorker();
        Wrapper wrapper = new Wrapper();
        wrapper.setWorker(worker);
        wrapper.setParam("Hello");
        bootstrap.doWork(wrapper).addListener(new Listener() {
            @Override
            public void result(Object result) {
                System.out.println(Thread.currentThread().getName());
                System.out.println(result);
            }
        });
        System.out.println(Thread.currentThread().getName());
    }

    private Wrapper doWork(Wrapper wrapper) {
        new Thread(() -> {
            Worker worker = wrapper.getWorker();
            String result = worker.action(wrapper.getParam());
            wrapper.getListener().result(result);
        }).start();
        return wrapper;
    }

    private Worker newWorker() {
        return new Worker() {
            @Override
            public String action(Object object) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return object + " world!";
            }
        };
    }
}
