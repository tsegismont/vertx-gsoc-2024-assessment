package proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
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
    var proxyRouter = Router.router(vertx);
    var proxyClient = vertx.createHttpClient();

    var backendProxy = HttpProxy.reverseProxy(proxyClient);
    backendProxy.origin(8080, "localhost");
    proxyRouter
      .route(HttpMethod.GET, "/joke")
      .handler(ProxyHandler.create(backendProxy));

    var websiteProxy = HttpProxy.reverseProxy(proxyClient);
    websiteProxy.origin(8081, "localhost");
    proxyRouter
      .route(HttpMethod.GET, "/")
      .handler(ProxyHandler.create(websiteProxy));

    var proxyServer = vertx.createHttpServer();
    proxyServer
      .requestHandler(proxyRouter)
      .listen(8000)
      .onSuccess(result -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}
