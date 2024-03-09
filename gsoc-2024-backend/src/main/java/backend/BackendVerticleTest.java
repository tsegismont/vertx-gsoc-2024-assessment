package backend;


import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.vertx.junit5.VertxExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
public class BackendVerticleTest {

  @BeforeAll
  public static void init(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle("backend.BackendVerticle", testContext.succeedingThenComplete());
  }

  @Test
  void testApi(Vertx vertx, VertxTestContext testContext) {
    WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/joke").send()
      .onFailure(Throwable::printStackTrace)
      .onSuccess(resp -> {
        JsonObject obj = resp.bodyAsJsonObject();
        assertEquals(200, obj.getInteger("status"));
        testContext.completeNow();
      });
  }

  @AfterAll
  public static void cleanup(Vertx vertx, VertxTestContext testContext) {
    var futures = vertx.deploymentIDs().stream().map(vertx::undeploy).toList();
    Future.all(futures).onComplete(v -> testContext.completeNow());
  }

}
