package proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.HttpProxy;

public class ProxyVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ProxyVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed proxy"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpClient proxyClient = vertx.createHttpClient();

    HttpProxy httpProxy1 = HttpProxy.reverseProxy(proxyClient);
    httpProxy1.origin(8080, "localhost");

    HttpServer proxyServer = vertx.createHttpServer();
    Router proxyRouter = Router.router(vertx);
    proxyRouter.route()
        .handler(
          CorsHandler.create()
            .addRelativeOrigin("http://localhost:3000")
            .allowedMethod(HttpMethod.GET)
        );
    proxyRouter
      .route(HttpMethod.GET, "/joke").handler(ProxyHandler.create(httpProxy1));

    proxyServer.requestHandler(proxyRouter);
    proxyServer.listen(7070);
    startPromise.complete();
  }
}
