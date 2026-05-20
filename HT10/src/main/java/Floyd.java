public class Floyd {

    private int[][] dist;
    private int[][] next;
    private int n;

    private static final int INF = DirectedGraph.INF;

    // Inicializa las matrices y corre el algoritmo
    public void compute(int[][] adj) {
        n = adj.length;
        dist = new int[n][n];
        next = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = adj[i][j];
                next[i][j] = (adj[i][j] < INF && i != j) ? j : -1;
            }
        }

        // Triple loop de Floyd-Warshall
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (dist[i][k] < INF && dist[k][j] < INF && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
    }

    // Retorna la distancia mas corta entre i y j
    public int getDist(int i, int j) {
        return dist[i][j];
    }

    // Retorna la ruta mas corta entre from y to como lista de ciudades
    public java.util.List<String> getPath(int from, int to, IGraph graph) {
        java.util.List<String> path = new java.util.ArrayList<>();
        if (dist[from][to] >= INF) return path;

        path.add(graph.getVertex(from));
        int cur = from;
        while (cur != to) {
            cur = next[cur][to];
            if (cur == -1) { path.clear(); return path; }
            path.add(graph.getVertex(cur));
        }
        return path;
    }

    // Retorna el centro del grafo (vertice con menor excentricidad)
    // Excentricidad = maximo de la columna i en la matriz APSP
    public String getCenter(IGraph graph) {
        int minEcc = INF;
        int centerIdx = -1;

        for (int col = 0; col < n; col++) {
            int ecc = 0;
            for (int row = 0; row < n; row++) {
                if (dist[row][col] >= INF) { ecc = INF; break; }
                if (dist[row][col] > ecc)  { ecc = dist[row][col]; }
            }
            if (ecc < minEcc) {
                minEcc = ecc;
                centerIdx = col;
            }
        }

        if (centerIdx == -1 || minEcc >= INF) return "No hay centro (grafo desconectado)";
        return graph.getVertex(centerIdx) + " (excentricidad = " + minEcc + " km)";
    }
}