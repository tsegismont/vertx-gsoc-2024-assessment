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
  private WebClient webClient; // Declare WebClient as a class variable

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

    webClient = WebClient.create(vertx); // Creating a single instance of WebClient

    router.get("/joke").handler(this::fetchJoke);

    // Creating an HTTP server
    HttpServer server = vertx.createHttpServer();

    // Setting request handler for the server
    server.requestHandler(router).listen(8081, result -> {
      if (result.succeeded()) {
        System.out.println("Backend server started successfully on port 8081");
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
  }

  private void fetchJoke(RoutingContext routingContext) {

    // Sending a GET request to the external API to fetch a joke
    webClient.getAbs(API_URL)
      .putHeader("Accept", "application/json")
      .send()
      .onSuccess(response -> {
        System.out.println("Received response with status code " + response.statusCode());
        if (response.statusCode() == 200) {
          // Parsing the response and extracting the joke
          JsonObject jsonObject = response.bodyAsJsonObject();
          String joke = jsonObject.getString("joke");

          // Sending the joke as the response to the client
          routingContext.response().putHeader("content-type", "text/plain").end(joke);

        } else {
          // Propagating the status code to the client if the request to the external API failed
          routingContext.response().setStatusCode(response.statusCode()).end();
        }
      })
      .onFailure(err -> {
        // Handling failure in fetching the joke
        System.out.println("Something went wrong " + err.getMessage());
        routingContext.fail(err);
      });
  }
}
