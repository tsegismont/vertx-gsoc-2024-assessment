package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;

public class BackendVerticle extends AbstractVerticle {
  WebClient client;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    client = WebClient.create(vertx);
    Router router = Router.router(vertx);

    router.route(HttpMethod.GET, "/joke").handler(ctx -> {
      client.get("icanhazdadjoke.com", "/").putHeader("Accept", "application/json").send()
        .onSuccess(resp -> {
          ctx.response()
            .putHeader("content-type", "application/json")
            .end(resp.bodyAsBuffer());
        }).onFailure(ctx::fail);
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);
    startPromise.complete();
  }
}
