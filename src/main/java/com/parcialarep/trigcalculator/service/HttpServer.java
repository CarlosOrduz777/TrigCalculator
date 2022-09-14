package com.parcialarep.trigcalculator.service;

import com.parcialarep.trigcalculator.model.TrigCalculator;

import java.net.*;
import java.io.*;

public class HttpServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(HttpServer.getPort());
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + HttpServer.getPort());
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean firstLine = true;
            String body = "";
            String path = "";
            String contentType= "application/json";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (firstLine) {
                    String[] receive = inputLine.split(" ");
                    path = receive[1];
                    if (path.startsWith("/cos")) {
                        String num = path.split("=")[1];
                        double val = Double.parseDouble(num);
                        body = "{"+"\"Coseno\""+":"+ TrigCalculator.getCos(val) +"}";
                    } else if (path.startsWith("/sen")) {
                        String num = path.split("=")[1];
                        double val = Double.parseDouble(num);
                        body = "{"+"Seno"+":"+ TrigCalculator.getSen(val) +"}";
                    } else if (path.startsWith("/tan")) {
                        String num = path.split("=")[1];
                        double val = Double.parseDouble(num);
                        body = "{"+"Tangente"+":"+ TrigCalculator.getTan(val) +"}";
                    } else if (path.startsWith("qck")) {
                        body = HttpServer.inConstruction();
                        contentType = "text/html";
                    } else {
                        body = HttpServer.getFail();
                        contentType = "text/html";
                    }
                    firstLine = false;
                }
                if (!in.ready()) {
                    break;
                }
            }
            outputLine = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: "+contentType+"\r\n"
                    + "\r\n"
                    + body;
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }

    public static int getPort() {
        if (System.getenv("PORT") != null) {
            return new Integer(System.getenv("PORT"));
        } else {
            return 4567;
        }
    }

    public static String getFail(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Error 404 file Not Found</h1>\n" +
                "    </body>\n" +
                "</html>";
    }

    public static String inConstruction(){
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>This service is in Construction</h1>\n" +
                "    </body>\n" +
                "</html>";
    }
}