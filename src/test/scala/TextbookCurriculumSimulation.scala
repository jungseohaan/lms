import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

/**
 * 커리큘럼 API 성능 시뮬레이션 테스트
 */
class TextbookCurriculumSimulation extends Simulation {

  var endPoint = "http://localhost:15000"
  var header = "application/json"

  var userCount = 1000 // 사용자 수
  var duration = 3 // 지속 시간
  var RESPONSE_SUCCESS = 200 // 성공 코드

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(endPoint)
    .acceptHeader(header)

  val scenario1: ScenarioBuilder = scenario("교과서 커리큘럼 목록 조회")
    .exec(
      http("교과서 커리큘럼 목록 조회")
        .get("/textbook/crcu/list")
        .queryParam("textbookIndexId", "1")
        .queryParam("userId", "550e8400-e29b-41d4-a716-446655440000")
        .check(status.is(RESPONSE_SUCCESS))
        .check(jsonPath("$.resultCode").is("200"))
    )

  val scenario2: ScenarioBuilder = scenario("교과서 학습맵 커리큘럼 목록 조회")
    .exec(
      http("교과서 학습맵 커리큘럼 목록 조회")
        .get("/textbook/meta/crcu/list")
        .queryParam("textbookIndexId", "1")
        .check(status.is(RESPONSE_SUCCESS))
        .check(jsonPath("$.resultCode").is("200"))
    )

  setUp(
    scenario1.inject(rampUsers(userCount) during (duration)),
    scenario2.inject(rampUsers(userCount) during (duration))
  ).protocols(httpProtocol)

}