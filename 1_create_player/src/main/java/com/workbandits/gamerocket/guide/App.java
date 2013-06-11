package com.workbandits.gamerocket.guide;

import com.workbandits.gamerocket.*;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;
import static spark.Spark.post;

public class App {

    private static GameRocketGateway gateway = new GameRocketGateway(
            Environment.DEVELOPMENT,
            "use_your_apikey",
            "use_your_secretkey");

    private static String renderHtml(String pageName) {
        try {
            return FileUtils.readFileToString(new File(pageName));
        } catch (IOException e) {
            return "Couldn't find " + pageName;
        }
    }

    public static void main(String[] args) {
        get(new Route("/") {

            @Override
            public Object handle(Request request, Response response) {
                response.type("text/html");
                return renderHtml("views/form.html");
            }
        });

        post(new Route("/create_customer") {

            @Override
            public Object handle(Request request, Response response) {
                PlayerRequest playerRequest = new PlayerRequest()
                        .name(request.queryParams("name"))
                        .locale(request.queryParams("locale"));

                Result<Player> result = gateway.player().create(playerRequest);

                response.type("text/html");
                if (result.isSuccess()) {
                    // Create the customer in your database with a field which contains Player ID for next calls

                    return "<h1>Success! Player ID: " + result.getTarget().getId() + "</h1>";
                } else {
                    return "<h1>Error: " + result.getErrorDescription() + "</h1>";
                }
            }
        });
    }
}