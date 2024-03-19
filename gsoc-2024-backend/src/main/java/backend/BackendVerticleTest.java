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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // Create an HTTP server and client for testing
    server = vertx.createHttpServer();
    client = vertx.createHttpClient();
    server.requestHandler(req -> {
      if (req.method() == HttpMethod.GET && req.path().equals("/joke")) {
        req.response().end("Test joke");
      } else {
        req.response().setStatusCode(404).end();
      }
    }).listen(8080, testContext.succeeding(result -> testContext.completeNow()));
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
            assertTrue(body.toString().length()>0);
            testContext.completeNow();
          });
        });
      }));
  }
}
