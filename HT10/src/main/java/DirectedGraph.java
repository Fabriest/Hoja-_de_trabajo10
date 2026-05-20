

import java.util.ArrayList;
import java.util.List;


public class DirectedGraph implements IGraph {

    public static final int INF = Integer.MAX_VALUE / 2;


    private final List<String> vertices;


    private int[][] matrix;

    private int capacity;

    public DirectedGraph() {
        this.vertices = new ArrayList<>();
        this.capacity = 20;
        this.matrix = new int[capacity][capacity];
        for (int i = 0; i < capacity; i++)
            for (int j = 0; j < capacity; j++)
                matrix[i][j] = (i == j) ? 0 : INF;
    }


    private void grow() {
        int newCap = capacity * 2;
        int[][] nm = new int[newCap][newCap];
        for (int i = 0; i < newCap; i++)
            for (int j = 0; j < newCap; j++)
                nm[i][j] = (i == j) ? 0 : INF;
        for (int i = 0; i < capacity; i++)
            for (int j = 0; j < capacity; j++)
                nm[i][j] = matrix[i][j];
        matrix = nm;
        capacity = newCap;
    }


    @Override
    public void addVertex(String vertex) {
        if (vertex == null || vertex.isBlank())
            throw new IllegalArgumentException("Vertex name cannot be null/blank.");
        if (vertices.contains(vertex)) return;
        if (vertices.size() == capacity) grow();
        vertices.add(vertex);
    }

    @Override
    public void addEdge(String vertex1, String vertex2, int weight) {
        if (weight < 0) throw new IllegalArgumentException("Weight cannot be negative.");
        int i = getIndex(vertex1), j = getIndex(vertex2);
        if (i < 0 || j < 0)
            throw new IllegalArgumentException("Vertex not found: " + vertex1 + " or " + vertex2);
        matrix[i][j] = weight;
    }

    @Override
    public void removeEdge(String vertex1, String vertex2) {
        int i = getIndex(vertex1), j = getIndex(vertex2);
        if (i < 0 || j < 0)
            throw new IllegalArgumentException("Vertex not found: " + vertex1 + " or " + vertex2);
        matrix[i][j] = INF;
    }

    @Override
    public int getVertexCount() { return vertices.size(); }

    @Override
    public int getIndex(String vertex) { return vertices.indexOf(vertex); }

    @Override
    public String getVertex(int index) { return vertices.get(index); }

    @Override
    public int[][] getAdjacencyMatrix() {
        int n = vertices.size();
        int[][] copy = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                copy[i][j] = matrix[i][j];
        return copy;
    }


    public void printAdjacencyMatrix() {
        int n = vertices.size();
        int w = 16;
        System.out.printf("%-" + w + "s", "");
        for (String v : vertices) System.out.printf("%-" + w + "s", v);
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("%-" + w + "s", vertices.get(i));
            for (int j = 0; j < n; j++) {
                String cell = (matrix[i][j] >= INF) ? "INF" : String.valueOf(matrix[i][j]);
                System.out.printf("%-" + w + "s", cell);
            }
            System.out.println();
        }
    }
}
