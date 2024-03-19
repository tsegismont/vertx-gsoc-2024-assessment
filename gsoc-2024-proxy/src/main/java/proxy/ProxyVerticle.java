package proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
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
    HttpClient proxyClient = vertx.createHttpClient();

    HttpProxy httpProxy1 = HttpProxy.reverseProxy(proxyClient);
    httpProxy1.origin(8080, "localhost");

    HttpProxy httpProxy2 = HttpProxy.reverseProxy(proxyClient);
    httpProxy2.origin(3000, "localhost");

    HttpServer proxyServer = vertx.createHttpServer();
    Router proxyRouter = Router.router(vertx);

    proxyRouter
      .route(HttpMethod.GET, "/joke").handler(ProxyHandler.create(httpProxy1));
    proxyRouter
      .route("/").handler(ProxyHandler.create(httpProxy2));

    proxyServer.requestHandler(proxyRouter);
    proxyServer.listen(7070)
      .onSuccess(h->{
        startPromise.complete();
      })
      .onFailure(h->{
        startPromise.fail(h.getCause());
      });
    startPromise.future().onSuccess(h->{
      System.out.println("proxy server deployed successfully");
    })
      .onFailure(h->{
        System.out.println("failed to deploy proxy server: " + h.getCause());
      });
  }
}
