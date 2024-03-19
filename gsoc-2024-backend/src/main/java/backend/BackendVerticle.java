package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class BackendVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));

  }
  
  public void getJoke(Promise<String > promise) {
    WebClient client = WebClient.create(vertx);

    client.getAbs("http://icanhazdadjoke.com/")
      .putHeader("Accept", "application/json")
      .as(BodyCodec.jsonObject())
      .send()
      .onSuccess(response -> {
        promise.complete(response.body().getString("joke"));
      })
      .onFailure(err -> {
        System.out.println("Something went wrong " + err.getMessage());
        promise.fail(err);
      });
  }

  @Override
  public void start(Promise<Void> startPromise) {

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route(HttpMethod.GET,"/joke").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.putHeader("content-type", "text/plain");
      Promise<String> promise = Promise.promise();
      getJoke(promise);
      promise.future().onComplete(result ->{
        if(result.succeeded()){
          response.end(result.result());
        } else {
          response.end("Failed to fetch joke: " + result.cause().getMessage());
        }
      });
    });

    server.requestHandler(router).listen(8080);
    startPromise.complete();
  }
}
