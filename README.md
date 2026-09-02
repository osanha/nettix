# nettix

A high-level network application framework built on Netty 3, with its
protocol stack — SMPP 3.4 and a brokerless message queue.

```
nettix-core     high-level framework over Netty 3 (channel lifecycle, reconnect,
                heartbeat, transparent SSL, HTTP/WebSocket, unified logging)
nettix-smpp     SMPP 3.4 protocol implementation on nettix-core
nettix-mq       brokerless pub/sub message queue on nettix-core
```

## Background

nettix was written while building a RESTful ESB hybrid API gateway from
scratch — a system that had to speak to carrier SMSCs, vehicle terminals and
external systems simultaneously over HTTP, WebSocket, REST, SOAP, SMPP,
message queues and proprietary TCP protocols.

Protocols differ, but servers and clients fall into a small set of recurring
patterns: long-lived connections versus request/response, keep-alive by
heartbeat or enquire-link, reconnection on loss, consistent socket
configuration and timeout handling, and SSL as a transparent layer that turns
any protocol into its secure variant. nettix systematizes those patterns so
that only protocol-specific logic remains to be written.

That gateway ran in global multi-region high-availability deployments for over
a decade.

## What this framework does that Netty 3 did not

Netty 3 is a capable engine, but it is not easy to use safely — several of its
sharp edges only show up in production. nettix was built to blunt them. Much of
what it added was later formalized by Netty 4 itself:

| nettix (on Netty 3) | Formalized later in Netty 4 |
| --- | --- |
| `InboundMessageHandler` / `OutboundMessageHandler` — abstract handlers that filter message events and forward everything else, so a handler cannot silently stall the pipeline by forgetting `ctx.sendUpstream(e)` | `ChannelInboundHandler` / `ChannelOutboundHandler`, `SimpleChannelInboundHandler` |
| `CallableChannelFuture<T>` — a future that carries a result value; `CollectionChannelFuture` aggregates several | `Promise<V>`, `PromiseCombiner` |
| `SocketOptions` — socket options behind named constants and validation instead of a raw option map | `ChannelOption<T>` |
| `AbstractChannelManager` — pipeline factory, SSL, channel group and lifecycle behind one object | `ChannelInitializer` with the reworked bootstrap |
| `org.jboss.netty.handler.codec.http.HttpRequestDecoder` / `HttpResponseDecoder` — placed in Netty's own package to extend a decoder that was not open for extension | `HttpObjectDecoder` made public |

And some of it Netty still leaves to the application:

- **Automatic reconnection** — `PersistentClientChannelManager`
- **Heartbeat / enquire-link as a protocol-agnostic handler** — `HeartbeatHandler`, `EnquireLinkHandler`, with pluggable factories
- **Read timeout tied to connection state** — `ChannelReadTimeoutHandler`, `ConnectStateEventHandler`

## Status

This is a **maintained artifact, not an actively developed project.**

It targets Netty 3.10.6 and Java 6, and it is published as-is: the code that
ran that gateway, cleaned up and documented. There is no plan to port it to
Netty 4 — the value here is what was built on Netty 3, and a port would
replace exactly the parts that make that interesting.

Issues and questions are welcome. Feature work is not planned.

## Requirements

- Java 6 or later (build with JDK 8 or earlier to target Java 6)
- Netty 3.10.6.Final

## Installation

Available through [JitPack](https://jitpack.io/#osanha/nettix).

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.osanha.nettix</groupId>
    <artifactId>nettix-core</artifactId>
    <version>3.2.0</version>
</dependency>
```

Replace `nettix-core` with `nettix-smpp` or `nettix-mq` for the protocol
modules; both pull in the core transitively.

## Documentation

Each module carries its own README with API details and examples:

- [nettix-core](nettix-core/README.md)
- [nettix-smpp](nettix-smpp/README.md)
- [nettix-mq](nettix-mq/README.md)

## Building

```bash
mvn clean package
```

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).
