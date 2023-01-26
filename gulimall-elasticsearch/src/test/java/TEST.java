import java.util.concurrent.*;

public class TEST {

  public static ExecutorService threadPool = Executors.newFixedThreadPool(10);

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    System.out.println("main......start");
    CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
      Integer i = 10 / 3;
      System.out.println("threadId:" + Thread.currentThread().getId());
      System.out.println("运行结果:" + i);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return i;
    }, threadPool);
    CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> "Hello", threadPool);

    CompletableFuture<Integer> res = future01.thenCombine(future02, (f1, f2) -> {
      System.out.println("任务3,得到任务1，2结果->" + f1 + ":" + f2);
      return 666;
    });

    System.out.println("main.......end:"+res.get());

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
