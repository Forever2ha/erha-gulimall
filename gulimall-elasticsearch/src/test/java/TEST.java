import java.util.concurrent.*;

public class TEST {

  public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

  public static void main(String[] args) throws ExecutionException, InterruptedException {
      System.out.println("main开始-----------------------------");
      CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
          System.out.println(Thread.currentThread().getName()+"开始0---------------");
          int a = 10 / 4;
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          System.out.println(Thread.currentThread().getName()+"结束0---------------");
          return a;
      }, threadPool).thenApplyAsync((res) -> {
          System.out.println(Thread.currentThread().getName()+"开始结束1---------------");
          return res+ 100;
      });

      CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
          System.out.println(Thread.currentThread().getName()+"开始结束2---------------");
          return "任务2结果";
      }, threadPool).thenCombineAsync(future, (r1, r2) -> {
          System.out.println(Thread.currentThread().getName()+"开始---------------");
          System.out.println("r1 = " + r1);
          System.out.println("r2 = " + r2);
          System.out.println(Thread.currentThread().getName()+"结束---------------");
          return r1 + r2;
      });

      System.out.println("future2.get() = " + future2.get());
      System.out.println("main结束-----------------------------");
  }

  public static class callable implements Callable<Integer>{

    @Override
    public Integer call() throws Exception {
      Integer i = 10/3;
      System.out.println("threadId:"+Thread.currentThread().getId());
      System.out.println("运行结果:"+i);
      return i;
    }
  }

}
