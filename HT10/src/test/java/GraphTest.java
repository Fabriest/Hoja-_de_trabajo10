import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class GraphTest {

    private DirectedGraph graph;
    private Floyd         floyd;

    @BeforeEach
    void setUp() {
        graph = new DirectedGraph();

        graph.addVertex("a");
        graph.addVertex("b");
        graph.addVertex("c");
        graph.addVertex("d");
        graph.addVertex("e");

        graph.addEdge("a", "b", 1);
        graph.addEdge("b", "c", 2);
        graph.addEdge("b", "d", 1);
        graph.addEdge("c", "d", 2);
        graph.addEdge("c", "e", 4);
        graph.addEdge("d", "c", 3);
        graph.addEdge("d", "e", 5);

        floyd = new Floyd();
        floyd.compute(graph.getAdjacencyMatrix());
    }


    @Test
    void testAddVertex_CountIncreases() {
        DirectedGraph g = new DirectedGraph();
        assertEquals(0, g.getVertexCount());
        g.addVertex("X");
        assertEquals(1, g.getVertexCount());
        g.addVertex("Y");
        assertEquals(2, g.getVertexCount());
    }

    @Test
    void testAddVertex_Duplicate_IgnoredSilently() {
        DirectedGraph g = new DirectedGraph();
        g.addVertex("X");
        g.addVertex("X");        
        assertEquals(1, g.getVertexCount());
    }

    @Test
    void testAddVertex_BlankName_ThrowsException() {
        DirectedGraph g = new DirectedGraph();
        assertThrows(IllegalArgumentException.class, () -> g.addVertex("  "));
    }

    @Test
    void testAddEdge_RecordedInMatrix() {
        DirectedGraph g = new DirectedGraph();
        g.addVertex("M");
        g.addVertex("N");
        g.addEdge("M", "N", 42);
        int[][] mat = g.getAdjacencyMatrix();
        assertEquals(42, mat[g.getIndex("M")][g.getIndex("N")]);
    }

    @Test
    void testAddEdge_NegativeWeight_ThrowsException() {
        DirectedGraph g = new DirectedGraph();
        g.addVertex("M");
        g.addVertex("N");
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("M", "N", -5));
    }

    @Test
    void testAddEdge_UnknownVertex_ThrowsException() {
        DirectedGraph g = new DirectedGraph();
        g.addVertex("M");
        assertThrows(IllegalArgumentException.class, () -> g.addEdge("M", "GHOST", 10));
    }

    @Test
    void testRemoveEdge_SetsToINF() {
        graph.removeEdge("a", "b");
        int i = graph.getIndex("a");
        int j = graph.getIndex("b");
        assertEquals(DirectedGraph.INF, graph.getAdjacencyMatrix()[i][j]);
    }

    @Test
    void testRemoveEdge_UnknownVertex_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> graph.removeEdge("a", "GHOST"));
    }

    @Test
    void testGetIndex_ReturnsMinusOneForUnknown() {
        assertEquals(-1, graph.getIndex("z"));
    }

    @Test
    void testAdjacencyMatrix_DiagonalIsZero() {
        int[][] mat = graph.getAdjacencyMatrix();
        for (int i = 0; i < graph.getVertexCount(); i++) {
            assertEquals(0, mat[i][i], "Diagonal should be 0 at index " + i);
        }
    }


    @Test
    void testFloyd_ShortestDistance_AtoE() {
        int a = graph.getIndex("a");
        int e = graph.getIndex("e");
        assertEquals(7, floyd.getDist(a, e));
    }

    @Test
    void testFloyd_ShortestDistance_AtoC() {
        int a = graph.getIndex("a");
        int c = graph.getIndex("c");
        assertEquals(3, floyd.getDist(a, c));
    }

    @Test
    void testFloyd_ShortestDistance_AtoD() {
        int a = graph.getIndex("a");
        int d = graph.getIndex("d");
        assertEquals(2, floyd.getDist(a, d));
    }

    @Test
    void testFloyd_NoPath_ReturnsINF() {
        int b = graph.getIndex("b");
        int a = graph.getIndex("a");
        assertEquals(DirectedGraph.INF, floyd.getDist(b, a));
    }

    @Test
    void testFloyd_SameVertex_IsZero() {
        int b = graph.getIndex("b");
        assertEquals(0, floyd.getDist(b, b));
    }

    @Test
    void testFloyd_PathReconstruction_AtoE() {
        int a = graph.getIndex("a");
        int e = graph.getIndex("e");
        List<String> path = floyd.getPath(a, e, graph);
        assertEquals("a", path.get(0));
        assertEquals("e", path.get(path.size() - 1));
        assertTrue(path.size() > 1);
    }

    @Test
    void testFloyd_PathReconstruction_Unreachable_IsEmpty() {
        int b = graph.getIndex("b");
        int a = graph.getIndex("a");
        List<String> path = floyd.getPath(b, a, graph);
        assertTrue(path.isEmpty());
    }


    @Test
    void testCenter_IsVertexD() {
        String center = floyd.getCenter(graph);
        assertTrue(center.startsWith("d"),
                "Expected center to start with 'd' but got: " + center);
    }

    @Test
    void testCenter_EccentricityOfD_Is5() {
        String center = floyd.getCenter(graph);
        assertTrue(center.contains("5"), "Eccentricity of d should be 5; got: " + center);
    }

    @Test
    void testCenter_DisconnectedGraph_ReturnsNoCenter() {
        DirectedGraph isolated = new DirectedGraph();
        isolated.addVertex("X");
        isolated.addVertex("Y");
        Floyd f = new Floyd();
        f.compute(isolated.getAdjacencyMatrix());
        String center = f.getCenter(isolated);
        assertTrue(center.contains("No center") || center.contains("disconnected"),
                "Disconnected graph should report no center; got: " + center);
    }


    @Test
    void testRemoveEdge_ThenRecompute_PathChanges() {
        graph.removeEdge("b", "d");
        floyd.compute(graph.getAdjacencyMatrix());
        int a = graph.getIndex("a");
        int d = graph.getIndex("d");
        assertEquals(5, floyd.getDist(a, d));
    }

    @Test
    void testAddEdge_ThenRecompute_ImprovesPath() {
        graph.addEdge("e", "a", 1);
        floyd.compute(graph.getAdjacencyMatrix());
        int e = graph.getIndex("e");
        int a = graph.getIndex("a");
        assertEquals(1, floyd.getDist(e, a));
    }
}
