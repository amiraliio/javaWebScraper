import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);
        if (input.hasNext()) {
            String url = input.next().replaceAll("=", "");
            Scrapper.init().fetch(url).parse().store("/download").build(true);
        }
    }
}