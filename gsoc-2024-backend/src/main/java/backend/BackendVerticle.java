package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;

public class BackendVerticle extends AbstractVerticle {

  private static final String API_URL = "https://icanhazdadjoke.com/";

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new BackendVerticle(), result -> {
      if (result.succeeded()) {
        System.out.println("Deployed backend");
      } else {
        result.cause().printStackTrace();
      }
    });
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
    router.get("/joke").handler(this::fetchJoke);

    HttpServer server = vertx.createHttpServer();
    server.requestHandler(router).listen(8080, result -> {
      if (result.succeeded()) {
        System.out.println("Backend server started successfully on port 8080");
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }

  private void fetchJoke(RoutingContext routingContext) {
    WebClient webClient = WebClient.create(vertx);
    webClient.getAbs(API_URL)
      .putHeader("Accept", "application/json")
      .send()
      .onSuccess(response -> {
        System.out.println("Received response with status code " + response.statusCode());
        if (response.statusCode() == 200) {
          JsonObject jsonObject = response.bodyAsJsonObject();
          String joke = jsonObject.getString("joke");
          routingContext.response().putHeader("content-type", "text/plain").end(joke);

        } else {
          routingContext.response().setStatusCode(response.statusCode()).end();
        }
      })
      .onFailure(err -> {
        System.out.println("Something went wrong " + err.getMessage());
        routingContext.fail(err);
      });
  }
}

