import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Program {

    static String inputFile, outputFile;
    static int B, L, D;
    static int currentDay;
    static ArrayList<Integer> scores;
    static HashSet<Integer> scannedBooks;
    static PriorityQueue<Library> maxLibGiver;

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Give all files\n");
        String[] inputFiles = new Scanner(System.in).nextLine().split(" ");

        for (String inputFile : inputFiles) {
            run(inputFile);
        }
    }

    static void run(String inputFile) throws FileNotFoundException {
        Program.inputFile = inputFile;
        Program.outputFile = inputFile + "out";
        readData();

        if (B <= 0 || L <= 0 || D <= 0) {
            throw new IllegalArgumentException("Faulty data");
        }

        currentDay = 0;

        PrintWriter writer = new PrintWriter(outputFile + ".temp");

        while (currentDay <= D) {
            if (maxLibGiver.isEmpty()) break;

            // get library with max value
            Library next = maxLibGiver.remove();

            long maxBooks = (long) (D - currentDay - next.T + 1) * next.M;
            if (maxBooks <= 0) {
                break;
            }
            // "scan" everything and advance time
            ArrayList<Integer> nextBooks = next.books.stream()
                    .filter((book) -> !scannedBooks.contains(book))
                    .limit(maxBooks)
                    .collect(Collectors.toCollection(ArrayList::new));

            // if max score library has 0 books to offer, stop
            if (nextBooks.isEmpty()) {
                break;
            }

            // write library ID and # of books to scan
            writer.printf("%d %d\n", next.ID, nextBooks.size());
            // write book ids on next line
            nextBooks.forEach((book) -> {
                scannedBooks.add(book);
                writer.printf("%d ", book);
            });
            writer.println();
            // advance time and remove library from consideration
            currentDay += next.T;
        }

        writer.close();
        writeNoOfLibsAndFinalize();
        //debugFields();
    }

    static double calculateScore(Library library, int currentDay) {
        long maxNoOfBooks = (long) (D - currentDay - library.T + 1) * library.M;
        if (maxNoOfBooks <= 0) {
            return 0;
        }

        return library.books.stream()
                .filter((book) -> !scannedBooks.contains(book))
                .limit(maxNoOfBooks)
                .mapToInt((book) -> scores.get(book))
                .sum() / (double) library.T;
    }

    static void readData() throws FileNotFoundException {

        scannedBooks = new HashSet<>();
        maxLibGiver = new PriorityQueue<>(Comparator.comparing((lib)->calculateScore(lib, currentDay), Comparator.reverseOrder()));

        Scanner reader = new Scanner(new File(inputFile));

        B = reader.nextInt();
        L = reader.nextInt();
        D = reader.nextInt();

        scores = new ArrayList<>(B);

        for (int i=0; i<B; i++) {
            scores.add(i, reader.nextInt());
        }

        int N, T, M;

        // read libraries
        for (int i=0; i<L; i++) {
            N = reader.nextInt();
            T = reader.nextInt();
            M = reader.nextInt();
            ArrayList<Integer> books = new ArrayList<>(N);
            for (int j=0; j<N; j++) {
                books.add(reader.nextInt());
            }
            books.sort(Comparator.comparing((book) -> scores.get(book), Comparator.reverseOrder()));
            maxLibGiver.add(new Library(N, T, M, books));
        }

        reader.close();
    }

    static void writeNoOfLibsAndFinalize() throws FileNotFoundException {
        File tempFile = new File(outputFile + ".temp");
        Scanner scanner = new Scanner(tempFile);

        PrintWriter writer = new PrintWriter(outputFile);
        writer.println(L - maxLibGiver.size());
        while (scanner.hasNextLine()) {
            writer.println(scanner.nextLine());
        }
        scanner.close();
        writer.close();
        tempFile.delete();
    }
}
