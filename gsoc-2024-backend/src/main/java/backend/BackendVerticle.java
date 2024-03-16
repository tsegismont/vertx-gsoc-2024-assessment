package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.uritemplate.UriTemplate;

public class BackendVerticle extends AbstractVerticle {

  private WebClient webClient;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    webClient = WebClient.create(vertx);

    var router = Router.router(vertx);
    router.route(HttpMethod.GET, "/joke").handler(this::joke);

    var server = vertx.createHttpServer();
    server
      .requestHandler(router)
      .listen(8080)
      .onSuccess(result -> startPromise.complete())
      .onFailure(startPromise::fail);
  }

  private void joke(RoutingContext context) {
    webClient
      .getAbs("https://icanhazdadjoke.com/")
      .putHeader("accept", "application/json")
      .send()
      .onSuccess(response -> context
        .response()
        .putHeader("Content-Type", "application/json")
        .setStatusCode(response.statusCode())
        .end(response.bodyAsString())
      ).onFailure(context::fail);
  }

}
