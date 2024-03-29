package SpaceTraders.model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class Request {

    public List<String> getRequest(String uri) {
        List<String> msg = new ArrayList<String>();
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            msg.add(String.valueOf(response.statusCode()));
            msg.add(String.valueOf(response.headers()));
            msg.add(response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
//            System.out.println(e.getMessage());
            msg.add("Something went wrong with our request!");
            msg.add(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            msg.add("Something went wrong.");
            msg.add(ignored.getMessage());
        }
        return msg;
    }

    public List<String> postRequest(String uri) {
        List<String> msg = new ArrayList<String>();
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(uri))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            msg.add(String.valueOf(response.statusCode()));
            msg.add(String.valueOf(response.headers()));
            msg.add(response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with our request!");
//            System.out.println(e.getMessage());
            msg.add("Something went wrong with our request!");
            msg.add(e.getMessage());
        } catch (URISyntaxException ignored) {
            // This would mean our URI is incorrect - this is here because often the URI you use will not be (fully)
            // hard-coded and so needs a way to be checked for correctness at runtime.
            msg.add("Something went wrong.");
            msg.add(ignored.getMessage());
        }
        return msg;

    }
}
