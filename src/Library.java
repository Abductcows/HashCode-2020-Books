import java.util.ArrayList;

public class Library {

    private static int nextId = 0;

    public final int ID;
    public final int N, T, M;
    public final ArrayList<Integer> books;

    public Library(int N, int T, int M, ArrayList<Integer> books) {
        this.ID = nextId++;
        this.N = N;
        this.T = T;
        this.M = M;
        this.books = books;
    }
}
