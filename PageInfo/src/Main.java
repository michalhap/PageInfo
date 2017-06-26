/**
 * Created by michal on 26.06.17.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

public class Main {

    static URL urlChecker(String arg) {
        URL url = null;

        try {
            url = new URL(arg);
        } catch (MalformedURLException e) {
            System.out.println("Type correct HTTP URL.");
        }

        return url;
    }

    static boolean linkChecker(String arg) {
        boolean isLink = false;
        URL url = null;
        try {
            url = new URL(arg);
            return isLink = true;
        } catch (MalformedURLException e) {
            return isLink;
        }
    }

    static boolean intChecker(String arg) {
        boolean isTrue = false;
        try {
            int depth = Integer.parseInt(arg);
            if (depth >= 0) {
                isTrue = true;
            } else {
                System.out.println("Depth must be >= 0.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Type correct depth: integer >= 0.");
        }
        return isTrue;
    }

    static void writeOutput(URL url, int size, String hash, HashSet set) {

        System.out.println("{");
        System.out.println(" \"Page URL \": " + "\"" + url + "\",");
        System.out.println(" \"Content size\": " + "\"" + size + "\", ");
        System.out.print(" \"Content MD5 hash\": " + "\"" + hash + "\"");
        if (set.isEmpty() == false) {
            System.out.print(",");
            System.out.println("");
            System.out.print(" \"Reachable pages\": " + " [ ");
            Iterator it = set.iterator();
            while (it.hasNext()) {
                System.out.print(it.next());
                if (it.hasNext() == false) {
                    System.out.print(" ]");
                    System.out.println();
                } else {
                    System.out.print(", ");
                }
            }
        } else {
            System.out.println();
        }
        System.out.println("}");
        System.out.println();
    }

    static HashSet getInfo(URL url) throws IOException {
        int size = 0;
        HashSet<String> links = new HashSet<>();
        HashSet<String> json_links = new HashSet<>();
        try {

            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String inputLine;
            String content = "";
            while ((inputLine = in.readLine()) != null) {
                content += inputLine;
                while (true) {
                    if (inputLine.contains("<a ")) {
                        if (inputLine.contains("href")) {
                            inputLine = inputLine.substring(inputLine.indexOf("href=") + 6);
                            String link = inputLine.substring(0, inputLine.indexOf("\""));
                            inputLine = inputLine.substring(inputLine.indexOf(link) + link.length());
                            if (linkChecker(link) == true) {
                                links.add(link);
                                link = "\"" + link + "\"";
                                json_links.add(link);
                            }

                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

            }
            in.close();

            URLConnection con = url.openConnection();

            String hash = md5Hex(content);
            size = content.getBytes().length;

            writeOutput(url, size, hash, json_links);
            return links;

        } catch (IOException e) {
            System.out.println("Page doesn't exists/access forbidden.");
        }

        return null;
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            System.out.println("Type URL HTTP and depth (number >= 0).");
        } else {
            if (urlChecker(args[0]) != null && intChecker(args[1]) == true) {
                URL url = urlChecker(args[0]);
                int depth = Integer.parseInt(args[1]);
                HashSet<String> pages = new HashSet<>();
                pages = getInfo(url);
                while (depth != 0) {
                    depth--;
                    for (String p : pages) {
                        URL url2 = new URL(p);
                        pages = getInfo(url2);
                    }
                }
            }
        }
    }
}