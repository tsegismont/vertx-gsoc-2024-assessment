package backend;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(VertxExtension.class)
class BackendVerticleTest {

  @Test
  void test_joke_is_random(Vertx vertx, VertxTestContext testContext) {
    vertx
      .deployVerticle(new BackendVerticle(), testContext.succeeding(id -> {
        var url = "http://localhost:8080/joke";
        var webClient = WebClient.create(vertx);
        webClient
          .getAbs(url)
          .as(BodyCodec.jsonObject())
          .send()
          .onComplete(testContext.succeeding(response1 -> testContext.verify(() -> {
            assertEquals(200, response1.statusCode());
            var joke1 = response1.body().getString("joke");
            assertNotNull(joke1);

            webClient
              .getAbs(url)
              .as(BodyCodec.jsonObject())
              .send()
              .onComplete(testContext.succeeding(response2 -> testContext.verify(() -> {
                assertEquals(200, response2.statusCode());
                var joke2 = response2.body().getString("joke");
                assertNotNull(joke2);

                assertNotEquals(joke1,  joke2);
                testContext.completeNow();
              })));
          })));
    }));
  }

}
