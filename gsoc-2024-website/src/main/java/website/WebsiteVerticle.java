package website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Stack;

public class WebsiteVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebsiteVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed website"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    //create a Vert.x web router
    Router router = Router.router(vertx);

    //serve static content from the webroot directory
    router.route().handler(StaticHandler.create("webroot"));

    //create an http server and pass the router to handle requests
    vertx.createHttpServer()
        .requestHandler(router)
          .listen(8080, result -> {
            if (result.succeeded()) {
              System.out.println("Website server started successfully on port 8080");
              startPromise.complete();
            } else {
              startPromise.fail(result.cause());
            }
          });

  }
}
