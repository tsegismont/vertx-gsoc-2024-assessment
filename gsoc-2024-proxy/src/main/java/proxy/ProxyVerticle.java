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

    // Defining the reverse proxies for frontend and backend
    HttpProxy httpProxy = HttpProxy.reverseProxy(proxyClient);
    httpProxy.origin(8080, "localhost"); //Frontend

    HttpProxy httpProxy2 = HttpProxy.reverseProxy(proxyClient);
    httpProxy2.origin(8081, "localhost"); //Backend

    Router proxyRouter = Router.router(vertx);

    // Handling requests for root path
    proxyRouter.route(HttpMethod.GET, "/").handler(ProxyHandler.create(httpProxy));

    // Handling requests for "/joke" path
    proxyRouter.route(HttpMethod.GET, "/joke").handler(ProxyHandler.create(httpProxy2));

    // Creating an HTTP server
    HttpServer server = vertx.createHttpServer();

    // Setting request handler for the server
    server.requestHandler(proxyRouter);

    // Starting the server on port 8082
    server.listen(8082, result -> {
      if (result.succeeded()) {
        System.out.println("Proxy server started successfully on port 8082");
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }
}
