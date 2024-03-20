package backend;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class BackendVerticleTest {

  private static Vertx vertx;
  private static HttpServer server;
  private static HttpClient client;

  @BeforeAll
  static void setUp(VertxTestContext testContext) {
    vertx = Vertx.vertx();

    // Deploy the BackendVerticle
    vertx.deployVerticle(new BackendVerticle(), testContext.succeeding(id -> testContext.completeNow()));
    client = vertx.createHttpClient();
  }

  @AfterAll
  static void tearDown(VertxTestContext testContext) {
    vertx.close(testContext.succeeding(result -> testContext.completeNow()));
  }

  @Test
  void testGetJokeAPI(VertxTestContext testContext) {
    client.request(HttpMethod.GET, 8080, "localhost", "/joke")
      .compose(HttpClientRequest::send)
      .onComplete(testContext.succeeding(response -> {
        testContext.verify(() -> {
          assertEquals(200, response.statusCode());
          response.bodyHandler(body -> {
              assertFalse(body.toString().isEmpty());
            testContext.completeNow();
          });
        });
      }));
  }
}
