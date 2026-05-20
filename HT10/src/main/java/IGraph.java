public interface IGraph {

    /**
     * Agrega un vertica que representa una ciudad al grafo
     * @param vertex nombre de la ciudad
     */
    void addVertex(String vertex);

    /**
     * Agrega un arco dirigido desde el vertice1 hacia el vertice2 con el peso dado
     * @param vertex1 vertice de origen
     * @param vertex2 vertice de destino
     * @param weight  KM
     */
    void addEdge(String vertex1, String vertex2, int weight);

    /**
     * Quita el arco dirigido desde el vertice1 hacia el vertice2
     * @param vertex1 vertice de origen
     * @param vertex2 vertice de destino
     */
    void removeEdge(String vertex1, String vertex2);

    /**
     * Returns the number of vertices in the graph.
     * @return number of vertices
     */
    int getVertexCount();

    /**
     * Retorna el índice de un vertice dado su nombre.
     * @param vertex nombre del vértice
     * @return índice o -1 si no se encuentra
     *
     */
    int getIndex(String vertex);

    /**
     * Retorna el nombre del vertice en una posición dada.
     * 
     * @param index posicion del vértice
     * @return nombre del vertice 
     */
    String getVertex(int index);

    /**
     * Retorna el matriz de adyacencia del grafo.
     */
    int[][] getAdjacencyMatrix();
}
