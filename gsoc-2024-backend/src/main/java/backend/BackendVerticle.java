package backend;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class BackendVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new BackendVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed backend"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    startPromise.complete();
  }
}
