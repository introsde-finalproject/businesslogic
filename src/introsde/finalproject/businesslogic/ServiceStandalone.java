package introsde.finalproject.businesslogic;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import javax.xml.ws.Endpoint;

public class ServiceStandalone {
    public static void main(String[] args) throws IllegalArgumentException, IOException, URISyntaxException{
        String PROTOCOL = "http://";
        String HOSTNAME = "0.0.0.0";
        String PORT = "7001";
        String BASE_URL = "/soap/businesslogic";
        
        String endpointUrl = PROTOCOL+HOSTNAME+":"+PORT+BASE_URL;
        System.out.println("Starting Business Logic Service...");
        System.out.println("--> Published. Check out "+endpointUrl+"?wsdl");
        Endpoint.publish(endpointUrl, new ServiceImpl());
    }
}
