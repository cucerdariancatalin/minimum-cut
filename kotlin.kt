import java.util.*

class Edge(val from: Int, val to: Int, val capacity: Int)

fun minCut(n: Int, edges: List<Edge>, source: Int, sink: Int): Set<Int> {
    val graph = Array(n) { mutableListOf<Int>() }
    val capacities = Array(n) { IntArray(n) }
    val flow = IntArray(n)
    val seen = BooleanArray(n)
    edges.forEach {
        graph[it.from].add(it.to)
        capacities[it.from][it.to] = it.capacity
    }

    // Use BFS to find a path from the source to the sink
    val queue = ArrayDeque<Int>()
    queue.add(source)
    flow[source] = Int.MAX_VALUE
    while (queue.isNotEmpty()) {
        val from = queue.poll()
        seen[from] = true
        graph[from].forEach { to ->
            if (!seen[to] && capacities[from][to] > 0) {
                queue.add(to)
                flow[to] = Math.min(flow[from], capacities[from][to])
            }
        }
    }

    // If no path from source to sink was found, return an empty set
    if (!seen[sink]) {
        return emptySet()
    }

    // Use the path found to update capacities and add edges to the residual graph
    val residualGraph = Array(n) { mutableListOf<Int>() }
    var from = source
    while (from != sink) {
        val to = graph[from].first { seen[it] }
        capacities[from][to] -= flow[sink]
        capacities[to][from] += flow[sink]
        residualGraph[to].add(from)
        residualGraph[from].add(to)
        from = to
    }

    // Find the connected component of the source in the residual graph
    val component = BooleanArray(n)
    fun dfs(i: Int) {
        component[i] = true
        residualGraph[i].forEach { j ->
            if (!component[j]) {
                dfs(j)
            }
        }
    }
    dfs(source)

    // Return the set of nodes in the connected component
    return component.withIndex().filter { it.value }.map { it.index }.toSet()
}
