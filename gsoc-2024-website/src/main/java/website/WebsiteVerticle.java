package website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
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
    var server = vertx.createHttpServer();
    var router = Router.router(vertx);
    router.route().handler(StaticHandler.create());
    server
      .requestHandler(router)
      .listen(8081)
      .onSuccess(result -> startPromise.complete())
      .onFailure(startPromise::fail);
  }
}
