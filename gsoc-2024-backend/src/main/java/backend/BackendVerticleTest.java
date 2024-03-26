package backend;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(VertxExtension.class)
class BackendVerticleTest {
  private Vertx vertx; // Vert.x instance
  private WebClient webClient; // WebClient for making HTTP requests

  @BeforeEach
  void prepare(VertxTestContext testContext) {
    vertx = Vertx.vertx(); // Create Vert.x instance
    webClient = WebClient.create(vertx); // Create WebClient
    vertx.deployVerticle(new BackendVerticle(), testContext.succeeding(id -> testContext.completeNow())); // Deploy the BackendVerticle
  }

  @Test
  void testJokeEndpoint(VertxTestContext testContext) {

    // Send GET request to '/joke' endpoint
    webClient.get(8081, "localhost", "/joke")
      .send(response -> {
        if (response.succeeded()) { // If request succeeded
          assertEquals(200, response.result().statusCode());
          assertNotNull(response.result().bodyAsString());
          testContext.completeNow(); // Complete the test
        } else {
          testContext.failNow(response.cause()); // Fail test if request failed
        }
      });

  }

  @AfterEach
  void cleanUp(VertxTestContext testContext) {
    testContext.completeNow();
  }
}


