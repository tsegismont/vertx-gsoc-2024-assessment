package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.CorsHandler;

public class BackendVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));
  }

  Future<JsonObject> getJoke() {
    Promise<JsonObject> promise = Promise.promise();

    WebClient client = WebClient.create(vertx);
    client.get("icanhazdadjoke.com", "/").putHeader("Accept", "application/json").send()
      .onSuccess(resp -> promise.complete(resp.bodyAsJsonObject()))
      .onFailure(Throwable::printStackTrace);

    return promise.future();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    router.route().handler(CorsHandler.create()
      .allowedMethod(io.vertx.core.http.HttpMethod.GET)
      .allowedMethod(io.vertx.core.http.HttpMethod.POST)
      .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
      .allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Credentials")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers")
      .allowedHeader("Content-Type"));

    router.route(HttpMethod.GET, "/joke").handler(ctx -> {
      getJoke().onSuccess(resp -> {
        ctx.response()
          .putHeader("content-type", "application/json")
          .end(resp.toBuffer());
      });
    });

    vertx.createHttpServer().requestHandler(router).listen(8080);
    startPromise.complete();
  }
}
