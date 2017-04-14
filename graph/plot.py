import json
import networkx as nx
import matplotlib.pyplot as plt
from networkx.readwrite import json_graph
from networkx.drawing.nx_agraph import graphviz_layout

iris = json.load(open('codeIrisExport.json'))
nodes = [node['className'] for node in iris]
neighbours = [[v.split('.')[-1] for v in node['usages']] for node in iris]
links = [[{'source': nodes.index(u), 'target': nodes.index(v), 'value': 1}
          for v in adj] for u, adj in zip(nodes, neighbours)]
links = [item for sublist in links for item in sublist]
nodes = [{'id': n, 'group': 1} for n in nodes]
graph = {'nodes': nodes, 'links': links}
graph = json_graph.node_link_graph(graph, multigraph = False)
layout = graphviz_layout(graph)
nx.draw(graph, layout, with_labels=True, node_shape='s', node_color='b', alpha=0.4, node_size=500)
plt.show()
