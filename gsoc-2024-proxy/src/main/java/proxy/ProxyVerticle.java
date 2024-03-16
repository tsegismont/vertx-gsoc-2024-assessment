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
    HttpServer proxyServer = vertx.createHttpServer();
    Router proxyRouter = Router.router(vertx);

    HttpClient proxyClient = vertx.createHttpClient();
    HttpProxy beProxy = HttpProxy.reverseProxy(proxyClient);
    beProxy.origin(8080, "localhost");
    HttpProxy feProxy = HttpProxy.reverseProxy(proxyClient);
    feProxy.origin(80, "localhost");

    proxyRouter.route("/").handler(ProxyHandler.create(feProxy));
    proxyRouter.route("/joke").handler(ProxyHandler.create(beProxy));

    proxyServer.requestHandler(proxyRouter).listen(5000);
  }
}
