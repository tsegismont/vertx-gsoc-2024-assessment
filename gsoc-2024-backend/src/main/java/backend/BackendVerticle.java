package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class BackendVerticle extends AbstractVerticle {
  private  WebClient client ;
  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));

  }

  public Future<String> getJoke() {
    Promise<String> promise = Promise.promise();
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
    return promise.future();
  }

  @Override
  public void start(Promise<Void> startPromise) {
    client = WebClient.create(vertx);
    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route(HttpMethod.GET,"/joke").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.putHeader("content-type", "text/plain");
      getJoke().onComplete(result ->{
        if(result.succeeded()){
          response.end(result.result());
        } else {
          response.end("Failed to fetch joke: " + result.cause().getMessage());
        }
      });
    });

    server.requestHandler(router).listen(8080, result->{
      if(result.succeeded()){
        startPromise.complete();
      } else {
        startPromise.fail(result.cause());
      }
    });
    startPromise.future().onSuccess(h->{
      System.out.println("server deployed successfully");
    })
      .onFailure(h->{
        System.out.println("failed to deploy server");
      });
  }
}
