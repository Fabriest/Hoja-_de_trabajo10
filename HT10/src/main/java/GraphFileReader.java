

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Lee el archivo para cargar un grafo de cuidad a cuidad con distancia.
 *
 * Formato del archivo:
 *   Cuidad1 Cuidad2 KM
 *
 */
public class GraphFileReader {

    /**
     * Lee el archivo especificado y carga los vértices y aristas en el grafo dado.
     *
     * @param filePath path al archivo
     * @param graph    el grafo a cargar
     * @throws IOException si no se encuentra o lee el archivo
     */
    public static void load(String filePath, DirectedGraph graph) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\s+");
                if (parts.length < 3) {
                    System.err.println("[WARN] Line " + lineNumber + " skipped (bad format): " + line);
                    continue;
                }

                String city1 = parts[0];
                String city2 = parts[1];
                int km;
                try {
                    km = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Line " + lineNumber + " skipped (bad distance): " + line);
                    continue;
                }

                graph.addVertex(city1);
                graph.addVertex(city2);
                graph.addEdge(city1, city2, km);
            }
        }
    }
}
