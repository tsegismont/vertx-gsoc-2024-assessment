package proxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class ProxyVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ProxyVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed proxy"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    startPromise.complete();
  }
}
