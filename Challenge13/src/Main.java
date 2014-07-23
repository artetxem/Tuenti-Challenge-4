/* Copyright (c) 2014 Mikel Artetxe. All Rights Reserved. */
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Main {
    
    public static void main(String args[]) throws MalformedURLException, IOException {
        final String input = new Scanner(System.in).nextLine();
        final Pattern pattern = Pattern.compile("Total run: (.*)\n");
        final URL url = new URL("http://54.83.207.90:4242/?input=" + input + "&debug=1");
        String key = "";
        while (true) {
            double maxTime = Double.MIN_VALUE;
            char bestMatch = '!';
            for (char c = '0'; c < 'g'; c = c == '9' ? 'a' : (char)(c+1)) {
                String params = "input=" + input + "&key=" + key + c;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
                connection.setDoOutput(true);
                connection.setRequestMethod("POST"); 
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();
                final String res = new Scanner(new BufferedReader(new InputStreamReader(connection.getInputStream()))).useDelimiter("\\A").next();
                if (!res.contains("<h1>Oh, god, you got it wrong!</h1>")) {
                    System.out.println(key + c);
                    System.exit(0);
                }
                final Matcher matcher = pattern.matcher(res);
                matcher.find();
                final double time = Double.parseDouble(matcher.group(1));
                if (time > maxTime) {
                    maxTime = time;
                    bestMatch = c;
                }
            }
            key = key + bestMatch;
            if (key.length() > 10*input.length()) key = ""; // Something strange has happened: try again
        }
    }
    
}
