
# will first receive a bunch of recipes
# [
#  (recipe_id, [(ingredient_type, quantity_ml), (ingredient_type, quantity_ml)])
# ]

# given ingredients:
# [(ingredient_type, quantity_ml), (ingredient_type, quantity_ml)]
# return recipe ids

class RecipeSuggester(object):

    def __init__(self, recipes):
        self.recipes = recipes

    def __call__(self, user_ingredients, num_extra_items):
        can_make_recipes = []
        could_make_recipes = []

        user_ingredient_types, user_ingredient_quant = zip(*user_ingredients)

        for recipe_id, ingredients in self.recipes:
            can_make = True
            could_make = True
            extra_items_count = 0

            for ingredient_type, quantity in ingredients:
                if ingredient_type not in user_ingredient_types:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > self.num_extra_items:
                        could_make = False
                        break
                    continue

                # the user has the ingredient, but maybe not have enough
                user_quantity = user_ingredient_quant[
                        user_ingredient_types.index(ingredient_type)]
                if user_quantity < quantity:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > self.num_extra_items:
                        could_make = False
                        break
                    continue

            if can_make:
                can_make_recipes.append(recipe_id)

            if could_make:
                could_make_recipes.append(recipe_id)

        return can_make_recipes, could_make_recipes


def initialize_suggesters():
    recipes = [
            (1, [(1, 20), (2, 10), (3, 5.5)]), # no, quantities are over
            (2, [(4, 20), (2, 10), (3, 0.3)]), # no, user doesnt have 4
            (3, [(2, 10), (3, 3)]), # no, user doesnt have enough of 3
            (4, [(4, 10), (5, 3)]), # no, user doesnt have ANY ingredients
            (5, [(1, 8), (2, 10), (3, 0.1)]), # true
            (6, [(1, 0.5), (2, 5)]), # true
            ]
    user_ingredients = [(1, 10), (2, 10), (3, 0.5)]
    sug = RecipeSuggester(recipes)
    print(sug(user_ingredients, 1))
    # return RecipeSuggester(recipes)

if __name__ == '__main__': initialize_suggesters()
