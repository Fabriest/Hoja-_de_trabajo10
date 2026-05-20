
import networkx as nx
import math
import sys
import os



def load_graph(filepath: str) -> nx.DiGraph:

    G = nx.DiGraph()
    if not os.path.exists(filepath):
        raise FileNotFoundError(f"Archivo no encontrado: {filepath}")

    with open(filepath, encoding="utf-8") as f:
        for lineno, line in enumerate(f, 1):
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            parts = line.split()
            if len(parts) < 3:
                print(f"Línea {lineno} ignorada (formato incorrecto): {line}")
                continue
            city1, city2 = parts[0], parts[1]
            try:
                km = int(parts[2])
            except ValueError:
                print(f"Línea {lineno} ignorada (distancia inválida): {line}")
                continue
            G.add_edge(city1, city2, weight=km)
    return G




def run_floyd(G: nx.DiGraph):
    pred, dist = nx.floyd_warshall_predecessor_and_distance(G, weight="weight")
    return dist, pred


def get_path(pred, source: str, target: str):
    try:
        return nx.reconstruct_path(source, target, pred)
    except (nx.NetworkXNoPath, KeyError):
        return []


def print_dist_matrix(dist, nodes):
    """Imprime la matriz APSP en forma tabular."""
    col_w = 16
    print(f"{'':>{col_w}}", end="")
    for n in nodes:
        print(f"{n:<{col_w}}", end="")
    print()
    for u in nodes:
        print(f"{u:<{col_w}}", end="")
        for v in nodes:
            val = dist[u][v]
            cell = "INF" if val == math.inf else str(int(val))
            print(f"{cell:<{col_w}}", end="")
        print()




def find_center(dist, nodes):
    min_ecc = math.inf
    center = None

    for col_vertex in nodes:
        ecc = max(dist[row][col_vertex] for row in nodes)
        if ecc < min_ecc:
            min_ecc = ecc
            center = col_vertex

    if center is None or min_ecc == math.inf:
        return None, math.inf
    return center, min_ecc



def print_menu():
    print("\n──────────────────────────────────────────")
    print("  MENÚ PRINCIPAL")
    print("  1. Ruta más corta entre dos ciudades")
    print("  2. Centro del grafo")
    print("  3. Modificar grafo (agregar / eliminar arco)")
    print("  4. Salir")
    print("──────────────────────────────────────────")


def handle_shortest_path(G, dist, pred, nodes):
    src = input("Ciudad origen  : ").strip()
    dst = input("Ciudad destino : ").strip()

    if src not in nodes:
        print(f"Ciudad no encontrada: {src}")
        return
    if dst not in nodes:
        print(f"Ciudad no encontrada: {dst}")
        return

    d = dist[src][dst]
    if d == math.inf:
        print(f"\nNo existe ruta de {src} a {dst}.")
    else:
        path = get_path(pred, src, dst)
        print(f"\nRuta más corta de {src} → {dst}:")
        print(f"  Distancia : {int(d)} km")
        print(f"  Ruta      : {' → '.join(path)}")


def handle_center(dist, nodes):
    center, ecc = find_center(dist, nodes)
    if center is None:
        print("\n El grafo está desconectado; no hay centro definido.")
    else:
        print(f"\n Centro del grafo: {center} (excentricidad = {int(ecc)} km)")
    print("\n=== Matriz de distancias mínimas (APSP) ===")
    print_dist_matrix(dist, nodes)


def handle_modify(G):
    print("\n  a. Eliminar arco (interrupción de tráfico)")
    print("  b. Agregar / actualizar arco")
    sub = input("Seleccione (a/b): ").strip().lower()

    if sub == "a":
        c1 = input("Ciudad origen  : ").strip()
        c2 = input("Ciudad destino : ").strip()
        if G.has_edge(c1, c2):
            G.remove_edge(c1, c2)
            print(f"✔ Arco {c1} → {c2} eliminado.")
            return True
        else:
            print(f"[!] El arco {c1} → {c2} no existe.")
            return False

    elif sub == "b":
        c1 = input("Ciudad origen  : ").strip()
        c2 = input("Ciudad destino : ").strip()
        try:
            km = int(input("Distancia (km) : ").strip())
            G.add_edge(c1, c2, weight=km)
            print(f"✔ Arco {c1} → {c2} ({km} km) agregado/actualizado.")
            return True
        except ValueError:
            print(" Distancia inválida.")
            return False
    else:
        print("[!] Opción no válida.")
        return False



def main():

    filepath = "guategrafo.txt"
    try:
        G = load_graph(filepath)
    except FileNotFoundError as e:
        print(f"✘ {e}")
        sys.exit(1)

    print(f" Grafo cargado: {G.number_of_nodes()} ciudades, "
          f"{G.number_of_edges()} arcos.\n")

    # Mostrar lista de adyacencia
    print("=== Arcos del grafo ===")
    for u, v, data in sorted(G.edges(data=True)):
        print(f"  {u} → {v}: {data['weight']} km")

    # Computar Floyd inicial
    dist, pred = run_floyd(G)
    nodes = sorted(G.nodes())

    running = True
    while running:
        print_menu()
        opt = input("Seleccione una opción: ").strip()

        if opt == "1":
            handle_shortest_path(G, dist, pred, nodes)

        elif opt == "2":
            handle_center(dist, nodes)

        elif opt == "3":
            modified = handle_modify(G)
            if modified:
                dist, pred = run_floyd(G)
                nodes = sorted(G.nodes())
                print(" Rutas más cortas y centro del grafo recalculados.")

        elif opt == "4":
            running = False

        else:
            print("Opción no válida.")

main()
