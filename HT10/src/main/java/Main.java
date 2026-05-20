

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Fabricio Estrada 25230
 */
public class Main {

    private static final String FILE_PATH = "guategrafo.txt";

    private static DirectedGraph graph;
    private static Floyd floyd;

    public static void main(String[] args) {
        graph = new DirectedGraph();
        floyd = new Floyd();

        try {
            GraphFileReader.load(FILE_PATH, graph);
        } catch (IOException e) {
            System.err.println("No se pudo leer el archivo: " + FILE_PATH);
            System.err.println("  " + e.getMessage());
            return;
        }
        recomputeFloyd();


        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String option = sc.nextLine().trim();
            switch (option) {
                case "1" -> handleShortestPath(sc);
                case "2" -> handleCenter();
                case "3" -> handleModify(sc);
                case "4" -> { running = false; }
                default  -> System.out.println("Opción no válida.");
            }
        }
        sc.close();
    }


    private static void printMenu() {
        System.out.println("\n──────────────────────────────────────────");
        System.out.println("  MENÚ PRINCIPAL");
        System.out.println("  1. Ruta más corta entre dos ciudades");
        System.out.println("  2. Centro del grafo");
        System.out.println("  3. Modificar grafo (agregar / eliminar arco)");
        System.out.println("  4. Salir");
        System.out.println("──────────────────────────────────────────");
        System.out.print("Seleccione una opción: ");
    }

    private static void handleShortestPath(Scanner sc) {
        System.out.print("Ciudad origen  : ");
        String from = sc.nextLine().trim();
        System.out.print("Ciudad destino : ");
        String to = sc.nextLine().trim();

        int idxFrom = graph.getIndex(from);
        int idxTo   = graph.getIndex(to);

        if (idxFrom < 0) { System.out.println("Ciudad no encontrada: " + from); return; }
        if (idxTo   < 0) { System.out.println("Ciudad no encontrada: " + to);   return; }

        int distance = floyd.getDist(idxFrom, idxTo);
        if (distance >= DirectedGraph.INF) {
            System.out.println("\nNo existe ruta de " + from + " a " + to + ".");
        } else {
            List<String> path = floyd.getPath(idxFrom, idxTo, graph);
            System.out.println("\nRuta más corta de " + from + " a " + to + ":");
            System.out.println("  Distancia : " + distance + " km");
            System.out.println("  Ruta      : " + String.join(" a ", path));
        }
    }

    private static void handleCenter() {
        System.out.println("\n Centro del grafo: " + floyd.getCenter(graph));
    }

    private static void handleModify(Scanner sc) {
        System.out.println("\n  a. Eliminar arco");
        System.out.println("  b. Agregar");
        System.out.print("Seleccione: ");
        String sub = sc.nextLine().trim().toLowerCase();

        switch (sub) {
            case "a" -> {
                System.out.print("Ciudad origen  : ");
                String c1 = sc.nextLine().trim();
                System.out.print("Ciudad destino : ");
                String c2 = sc.nextLine().trim();
                try {
                    graph.removeEdge(c1, c2);
                    System.out.println("De " + c1 + " a " + c2 + " eliminado.");
                    recomputeFloyd();
                    System.out.println("Rutas más cortas y centro del grafo rehechos.");
                } catch (IllegalArgumentException e) {
                    System.out.println("[!] " + e.getMessage());
                }
            }
            case "b" -> {
                System.out.print("Ciudad origen  : ");
                String c1 = sc.nextLine().trim();
                System.out.print("Ciudad destino : ");
                String c2 = sc.nextLine().trim();
                System.out.print("Distancia (km) : ");
                String kmStr = sc.nextLine().trim();
                try {
                    int km = Integer.parseInt(kmStr);
                    graph.addVertex(c1);
                    graph.addVertex(c2);
                    graph.addEdge(c1, c2, km);
                    System.out.println(" De " + c1 + " a " + c2 + " (" + km + " km) agregado.");
                    recomputeFloyd();
                    System.out.println(" Rutas más cortas y centro del grafo rehechos.");
                } catch (NumberFormatException e) {
                    System.out.println("Distancia inválida.");
                } catch (IllegalArgumentException e) {
                    System.out.println( e.getMessage());
                }
            }
            default -> System.out.println("Opción no válida.");
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private static void recomputeFloyd() {
        floyd.compute(graph.getAdjacencyMatrix());
    }
}
