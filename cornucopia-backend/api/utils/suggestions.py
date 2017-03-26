from collections import namedtuple
from django.forms.models import model_to_dict
from api.models import IngredientType
from api.utils.string_distance import ratcliff_obershelp


Node = namedtuple('Node', ['name', 'db_id', 'children'])

class SuggestionTree(object):

    def __init__(self, words, distfn):
        self.distfn = distfn
        self.root = None
        for word in words:
            self._add(word)

    def _add(self, word):
        name, db_id = word

        if self.root is None:
            self.root = Node(name, db_id, {})
            return

        curr = self.root
        dist = self.distfn(curr.name, name)

        while dist in curr.children.keys():
            curr = curr.children[dist]
            dist = self.distfn(curr.name, name)

        curr.children[dist] = Node(name, db_id, {})

    def __call__(self, query, max_dist=50):
        candidates = [self.root]
        found = []

        while len(candidates) > 0:
            node = candidates.pop(0)
            dist = self.distfn(node.name, query)

            if dist < max_dist:
                found.append((node, dist))

            candidates.extend([child_node
                    for child_dist, child_node in node.children.items()
                    if dist - max_dist <= child_dist <= dist + max_dist])

        found.sort(key=lambda x: x[1])
        return [(node.name, node.db_id) for node, _ in found]

def initialize_suggestions():
    data = IngredientType.objects.values_list('name', 'pk')
    return SuggestionTree(data, ratcliff_obershelp)

def initialize_search():
    data = Recipe.objects.values_list('name', 'pk')
    return SuggestionTree(data, ratcliff_obershelp)
