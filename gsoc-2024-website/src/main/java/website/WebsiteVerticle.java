package website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class WebsiteVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebsiteVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed website"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route("/").handler(StaticHandler.create());
    server.requestHandler(router).listen(3000)
      .onSuccess(httpServer -> {
        System.out.println("successfully deployed frontend server on 3000");
        startPromise.complete();
      })
      .onFailure(h->{
        System.out.println("failed to deployed frontend server on 3000: " + h.getCause());
      });
    startPromise.future().onSuccess(h->{
      System.out.println("successfully deployed frontend");
    })
      .onFailure(h->{
        System.out.println("failed to deploy frontend");
      });
  }
}
