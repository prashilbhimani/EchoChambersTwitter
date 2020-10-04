import networkx
from networkx.algorithms.community import greedy_modularity_communities
print(networkx.__version__)


G=networkx.Graph()
G.add_nodes_from([0,1,2,3,4,5,6,7,8,9])
G.add_edges_from([
    (1,2),
    (1,3),
    (2,3),
    (2,4),
    (3,4),
    (4,5),
    (5,6),
    (5,8),
    (6,7),
    (7,8),
    (7,9),
    (8,9)])
print(G.nodes)
print(G.edges)
print(list(greedy_modularity_communities(G)))