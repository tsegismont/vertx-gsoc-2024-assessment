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

    Route route = router.route(HttpMethod.GET, "/joke");
    route.handler(ctx -> {
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
