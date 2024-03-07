package website;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class WebsiteVerticle extends AbstractVerticle {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new WebsiteVerticle())
      .onFailure(Throwable::printStackTrace)
      .onSuccess(v -> System.out.println("Deployed website"));
  }

  @Override
  public void start(Promise<Void> startPromise) {
    startPromise.complete();
  }
}
