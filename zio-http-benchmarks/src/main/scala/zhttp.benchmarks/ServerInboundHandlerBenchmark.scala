package zhttp.benchmarks

import org.openjdk.jmh.annotations._
import zio._
import zio.http._

import java.util.concurrent.TimeUnit

@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(value = 1)
@State(org.openjdk.jmh.annotations.Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class ServerInboundHandlerBenchmark {

  private val MAX  = 10000
  private val PAR  = 10
  private val res  = ZIO.succeed(Response.text("Hello World!"))
  private val endPoint = "test"
  private val http     = Routes(Route.route(Method.GET / endPoint)(handler(res))).toHttpApp

//  private val benchmarkZio =
//    (for {
//      port <- Server.install(http)
//      client <- ZIO.service[Client]
//      url    <- ZIO.fromEither(URL.decode(s"http://localhost:$port/$endPoint"))
//      _ <- client.request(Request(url = url)).repeatN(MAX)
//    } yield ()).provide(Server.default, ZClient.default, zio.Scope.default)

  private val benchmarkZioParallel =
    (for {
      port <- Server.install(http)
      client <- ZIO.service[Client]
      url    <- ZIO.fromEither(URL.decode(s"http://localhost:$port/$endPoint"))
      call = client.request(Request(url = url))
      _ <- ZIO.collectAllParDiscard(List.fill(MAX)(call)).withParallelism(PAR)
    } yield ()).provide(Server.default, ZClient.default, zio.Scope.default)

//  @Benchmark
//  def benchmarkApp(): Unit = {
//    zio.Unsafe.unsafe(implicit u =>
//      zio.Runtime.default.unsafe.run(benchmarkZio)
//    )
//  }

  @Benchmark
  def benchmarkAppParallel(): Unit = {
    zio.Unsafe.unsafe(implicit u =>
      zio.Runtime.default.unsafe.run(benchmarkZioParallel)
    )
  }
}
