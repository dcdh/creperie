package com.creperie.salle.infrastructure.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsEqual.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
class PriseDeCommandeEndpointTest {

    @Test
    @Order(1)
    void shouldCommencerLaPriseDeCommande() {
        given()
                .contentType(ContentType.URLENC)
                .formParam("nombreDeConvives", 4)
                .formParam("numeroDeTable", 12)
                .when()
                .post("/priseDeCommande/commencerLaPriseDeCommande")
                .then()
                .log().all()
                .statusCode(200)
                .body("commande", notNullValue())
                .body("commande.commandeIdentifier", equalTo("12-000001"))
                .body("commande.numeroDeTable", equalTo(12))
                .body("commande.nombreDeConvives", equalTo(4))
                .body("commande.datePriseDeCommande", notNullValue())
                .body("commande.plats", hasSize(0))
                .body("commande.status", equalTo("EN_COURS_DE_PRISE"))
                .body("events", hasSize(1))
                .body("events[0].nombreDeConvives", equalTo(4))
                .body("events[0].type", equalTo("CommandeEnCoursDePrise"));
    }

    @Test
    @Order(2)
    void shouldAjouterPlat() {
        given()
                .contentType(ContentType.URLENC)
                .formParam("nom", "NUTELLA")
                .pathParam("commandIdentifier", "12-000001")
                .when()
                .post("/priseDeCommande/{commandIdentifier}/ajouterPlat")
                .then()
                .log().all()
                .statusCode(200)
                .body("commande", notNullValue())
                .body("commande.commandeIdentifier", equalTo("12-000001"))
                .body("commande.numeroDeTable", equalTo(12))
                .body("commande.nombreDeConvives", equalTo(4))
                .body("commande.datePriseDeCommande", notNullValue())
                .body("commande.plats", hasSize(1))
                .body("commande.plats[0].nom", equalTo("NUTELLA"))
                .body("commande.status", equalTo("EN_COURS_DE_PRISE"))
                .body("events", hasSize(2))
                .body("events[0].nombreDeConvives", equalTo(4))
                .body("events[0].type", equalTo("CommandeEnCoursDePrise"))
                .body("events[1].plat.nom", equalTo("NUTELLA"))
                .body("events[1].type", equalTo("PlatAjoute"));
    }

    @Test
    @Order(3)
    void shouldFinaliserLaCommande() {
        given()
                .pathParam("commandIdentifier", "12-000001")
                .when()
                .post("/priseDeCommande/{commandIdentifier}/finaliserLaCommande")
                .then()
                .log().all()
                .statusCode(200)
                .body("commande", notNullValue())
                .body("commande.commandeIdentifier", equalTo("12-000001"))
                .body("commande.numeroDeTable", equalTo(12))
                .body("commande.nombreDeConvives", equalTo(4))
                .body("commande.datePriseDeCommande", notNullValue())
                .body("commande.plats", hasSize(1))
                .body("commande.plats[0].nom", equalTo("NUTELLA"))
                .body("commande.status", equalTo("FINALISEE"))
                .body("events", hasSize(3))
                .body("events[0].nombreDeConvives", equalTo(4))
                .body("events[0].type", equalTo("CommandeEnCoursDePrise"))
                .body("events[1].plat.nom", equalTo("NUTELLA"))
                .body("events[1].type", equalTo("PlatAjoute"))
                .body("events[2].type", equalTo("CommandeFinalisee"));
    }
}
