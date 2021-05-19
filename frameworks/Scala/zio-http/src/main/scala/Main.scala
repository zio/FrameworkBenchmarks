import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.channel.epoll.{Epoll, EpollEventLoopGroup, EpollServerSocketChannel}
import io.netty.channel.kqueue.{KQueue, KQueueEventLoopGroup, KQueueServerSocketChannel}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket._
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http._
import io.netty.util.ResourceLeakDetector.Level
import io.netty.util.{CharsetUtil, ResourceLeakDetector}

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.{ScheduledExecutorService, TimeUnit}

/**
 * Handles a server-side channel.
 */
object Netty extends App {
  val helloNetty                   = "Hello, World!".getBytes(CharsetUtil.UTF_8);
  private val STATIC_PLAINTEXT_LEN = helloNetty.length
  val serverName                   = "ZIO-Http"
  @volatile
  var date                         = s"${DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now)}"

  import java.util.concurrent.Executors

  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  scheduler.scheduleWithFixedDelay(
    new Runnable() {
      override def run(): Unit = {
        date = s"${DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now)}"
      }
    },
    1000,
    1000,
    TimeUnit.MILLISECONDS,
  )
  @Sharable
  class NettyHandler extends SimpleChannelInboundHandler[HttpRequest](true) {

    override def channelRead0(
      ctx: ChannelHandlerContext,
      jReq: HttpRequest,
    ): Unit = {
      jReq.uri() match {
        case "/plaintext" =>
          val buf      = Unpooled.wrappedBuffer(helloNetty)
          val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf, false)

          response.headers
            .set(HttpHeaderNames.CONTENT_LENGTH, STATIC_PLAINTEXT_LEN)
            .set(HttpHeaderNames.SERVER, serverName)
            .set(HttpHeaderNames.DATE, date)
            .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)

          ctx.write(response, ctx.voidPromise())
        case _            =>
          val response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.NOT_FOUND,
            Unpooled.EMPTY_BUFFER,
            false,
          )
          ctx.write(response).addListener(ChannelFutureListener.CLOSE)
      }
      ()

    }

    override def channelUnregistered(ctx: ChannelHandlerContext): Unit = {
      ctx.close()
      ()
    }
    override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
      ctx.flush()
      ()
    }
    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
      ctx.close()
      ()
    }
  }
  class NettyServer {
    val handlerH                                 = new NettyHandler()
    val value: ChannelInitializer[SocketChannel] =
      (socketChannel: SocketChannel) => {
        val pipeline = socketChannel.pipeline
        pipeline.addLast("encoder", new HttpResponseEncoder)
        pipeline.addLast("decoder", new HttpRequestDecoder(4096, 8192, 8192, false))
        pipeline.addLast(handlerH)
        ()
      }

    def run(): Unit = {
      val eventLoopGroup      =
        if (Epoll.isAvailable)
          new EpollEventLoopGroup
        else if (KQueue.isAvailable)
          new KQueueEventLoopGroup
        else new NioEventLoopGroup
      val ServerSocketChannel =
        if (Epoll.isAvailable)
          classOf[EpollServerSocketChannel]
        else if (KQueue.isAvailable)
          classOf[KQueueServerSocketChannel]
        else classOf[NioServerSocketChannel]
      try {
        val serverBootstrap = new ServerBootstrap

        serverBootstrap
          .group(eventLoopGroup)
          .channel(ServerSocketChannel)
          .childHandler(value)
        val channel = serverBootstrap.bind(8080).sync.channel
        channel.closeFuture.sync()
        ()
      } finally {
        eventLoopGroup.shutdownGracefully()
        ()
      }
    }
    ResourceLeakDetector.setLevel(Level.DISABLED)
  }

  new NettyServer().run()
}
