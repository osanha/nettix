# nettix

**A high-level framework for building high-performance asynchronous servers
and clients — for any protocol — quickly, simply, and reliably, on top of
Netty.**

Netty is an engine. It gives you non-blocking I/O and a pipeline, and leaves
everything above that to the application: connection lifecycle, reconnection,
keep-alive, timeout policy, TLS wiring, consistent logging and error mapping.
Every protocol you implement pays that cost again.

nettix pays it once. What remains for you to write is the protocol itself.

```java
// TLS is a layer, not a protocol variant: the same server becomes HTTPS
// by naming a keystore. Lifecycle, timeouts and logging come from the framework.
SslManager.loadKeyStore("my-ssl", "PKCS12", "/path/keystore.p12", "storePass", "keyPass");

HttpServer server = new HttpServer("MyServer", 8443, "my-ssl");
server.setHandler(new SimpleHttpRequestHandler() {
    @Override
    public void requestReceived(Channel ch, SocketAddress addr, HttpRequest req) {
        HttpResponse res = new HttpResponse(HttpResponseStatus.OK);
        res.setContent("Hello, World!", "text/plain");
        ch.write(res);
    }
});
server.setUp();
```

## Modules

| Module | What it is |
| --- | --- |
| [`nettix-core`](nettix-core/README.md) | The framework — channel managers, pipeline composition, transparent SSL/TLS, heartbeat and enquire-link, automatic reconnection, HTTP and WebSocket support |
| [`nettix-smpp`](nettix-smpp/README.md) | SMPP 3.4 implementation built on the core |
| [`nettix-mq`](nettix-mq/README.md) | Brokerless pub/sub message queue built on the core |

The two protocol modules exist as much to demonstrate the framework as to be
used. SMPP and a message queue share almost nothing at the wire level, yet
both are written against the same core abstractions without special cases —
which is the claim the framework makes.

## Why it looks the way it does

nettix was written while building a Transport-Agnostic API Gateway from
scratch — a system that had to speak to carrier SMSCs, vehicle terminals and
external systems at the same time, over HTTP, WebSocket, REST, SOAP, SMPP,
message queues and proprietary TCP protocols.

Protocols differ; the problems around them repeat. Long-lived connections
versus request/response. Keep-alive by heartbeat or enquire-link.
Reconnection after loss. Uniform socket options, timeouts, logging and
exception mapping. TLS as a transparent layer that turns any protocol into
its secure variant — HTTP into HTTPS, WS into WSS, TCP into TLS — by
inserting one handler rather than touching the protocol.

Naming those patterns once and making them composable is the whole idea.

That gateway ran in global multi-region high-availability deployments for
over a decade.

## What this framework does that Netty 3 did not

Netty 3 is a capable engine, but it is not easy to use safely — several of its
sharp edges only surface in production. nettix was built to blunt them. Much
of what it added was later formalized by Netty 4 itself:

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

It targets Netty 3.10.6 and Java 6, and is published as-is: the code that ran
that gateway, cleaned up and documented. There is no plan to port it to
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

Use `nettix-smpp` or `nettix-mq` for the protocol modules; both pull in the
core transitively.

## Documentation

Each module carries its own README with API details and examples. Start with
[`nettix-core`](nettix-core/README.md) — this page is the map, that one is the
manual.

## Building

```bash
mvn clean package
```

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE).

## Author

Sanha Lee
