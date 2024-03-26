package backend;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
// Use VertxUnitRunner for running Vert.x unit tests
@RunWith(VertxUnitRunner.class)
public class BackendVerticleTest {
  private Vertx vertx; // Vert.x instance
  private WebClient webClient; // WebClient for making HTTP requests

  // Set up before each test case
  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx(); // Create Vert.x instance
    vertx.deployVerticle(new BackendVerticle(), context.asyncAssertSuccess()); // Deploy BackendVerticle
    webClient = WebClient.create(vertx); // Create WebClient
  }

  // Tear down after each test case
  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess()); // Close Vert.x instance
  }

  // Test case for the '/joke' endpoint
  @Test
  public void testJokeEndpoint(TestContext context) {
    Async async = context.async(); // Asynchronous test completion

    // Send GET request to '/joke' endpoint
    webClient.get(8081, "localhost", "/joke")
      .send(response -> {
        if (response.succeeded()) { // If request succeeded
          context.assertTrue(response.result().statusCode() == 200); // Assert status code is 200
          context.assertNotNull(response.result().bodyAsString()); // Assert response body is not null
          async.complete(); // Complete asynchronous test
        } else {
          context.fail(response.cause()); // Fail test if request failed
        }
      });
  }
}
