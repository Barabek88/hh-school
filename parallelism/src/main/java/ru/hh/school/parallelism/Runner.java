package ru.hh.school.parallelism;

import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import ru.hh.school.parallelism.executor.ExecutorComputation;
import ru.hh.school.parallelism.fjp.ForkJoinPoolComputation;
import ru.hh.school.parallelism.sequential.SequentialComputation;
import ru.hh.school.parallelism.stream.ParallelStreamComputation;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Fork(value = 1, warmups = 0)
@Warmup(iterations = 1, time = 5)
@Measurement(iterations = 3, time = 5)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class Runner extends Main {

  private static int CYCLES = 1_000;
  private static int CPU_CYCLES = 10_000;
  private static int IO_MILLISECONDS = 0;

  @Benchmark
  public void sequential(Blackhole blackhole) {
    blackhole.consume(new SequentialComputation().compute(CYCLES));
  }

  public static ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  @TearDown
  public void tearDown() {
    EXECUTOR.shutdown();
  }


  @Benchmark
  public void executor(Blackhole blackhole) throws InterruptedException {
    blackhole.consume(new ExecutorComputation().compute(CYCLES));
  }

  @Benchmark
  public void FJP(Blackhole blackhole) {
    blackhole.consume(new ForkJoinPoolComputation().compute(CYCLES));
  }

  @Benchmark
  public void parallelStream(Blackhole blackhole) {
    blackhole.consume(new ParallelStreamComputation().compute(CYCLES));
  }

  public static long performCPUJob() {
    Blackhole.consumeCPU(CPU_CYCLES);
    return 100;
  }

  public static long performIOJob() {
    Uninterruptibles.sleepUninterruptibly(IO_MILLISECONDS, MILLISECONDS);
    return 200;
  }
}
