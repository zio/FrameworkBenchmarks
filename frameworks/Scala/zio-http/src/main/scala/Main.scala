import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.Unpooled
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

/**
 * Handles a server-side channel.
 */
object Netty extends App {
  val helloNetty = "Hello, World!"
  val serverName = "ZIO-Http"
  class NettyHandler extends SimpleChannelInboundHandler[FullHttpRequest](false) {

    override def channelRead0(
                               ctx: ChannelHandlerContext,
                               jReq: FullHttpRequest
                             ): Unit = {
      val buf = Unpooled.copiedBuffer(helloNetty, CharsetUtil.UTF_8)
      val headers: HttpHeaders = new DefaultHttpHeaders()
        .set(HttpHeaders.Names.CONTENT_LENGTH, buf.readableBytes)
        .set(HttpHeaderNames.SERVER, serverName)
        .set(HttpHeaderNames.DATE, s"${DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now)}")
        .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
      ctx.write(new DefaultHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        headers
      ))
      ctx.write(buf)
      ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
      ()
    }

    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = super.exceptionCaught(ctx, cause)
  }

  class NettyServer {
    val value: ChannelInitializer[SocketChannel] =
      (socketChannel: SocketChannel) => {
        val pipeline = socketChannel.pipeline
        pipeline.addLast(new HttpServerCodec())
        pipeline.addLast(new HttpObjectAggregator(Int.MaxValue))
        pipeline.addLast(new NettyHandler())
        ()
      }

    def run(): Unit = {
      val eventLoopGroup = if (Epoll.isAvailable)
        new EpollEventLoopGroup
      else if (KQueue.isAvailable)
        new KQueueEventLoopGroup
      else new NioEventLoopGroup
      val ServerSocketChannel = if (Epoll.isAvailable)
        classOf[EpollServerSocketChannel]
      else if (KQueue.isAvailable)
        classOf[KQueueServerSocketChannel]
      else  classOf[NioServerSocketChannel]
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