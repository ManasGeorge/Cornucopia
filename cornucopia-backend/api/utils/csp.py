from django.forms.models import model_to_dict
from api.models import Recipe
from pint import UnitRegistry
from pint.errors import DimensionalityError

u = UnitRegistry()
# will first receive a bunch of recipes
# [
#  (recipe_id, [(ingredient_type, quantity_ml), (ingredient_type, quantity_ml)])
# ]
# and save them so no more db querying is needed

# given ingredients:
# [(ingredient_type, quantity_ml), (ingredient_type, quantity_ml)]
# return recipe ids

class RecipeSuggester(object):

    def __init__(self, recipes):
        self.recipes = recipes

    def __call__(self, user_ingredients, num_extra_items):
        can_make_recipes = []
        could_make_recipes = []

        if len(user_ingredients) > 0:
            user_ingredient_types, user_ingredient_quant = zip(*user_ingredients)
        else:
            user_ingredient_types, user_ingredient_quant = [], []

        for recipe_id, ingredients in self.recipes:
            can_make = True
            could_make = True
            extra_items_count = 0

            for ingredient_type, quantity in ingredients:
                if ingredient_type not in user_ingredient_types:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > num_extra_items:
                        could_make = False
                        break
                    continue

                # the user has the ingredient, but maybe not have enough
                user_quantity = user_ingredient_quant[
                        user_ingredient_types.index(ingredient_type)]
                if user_quantity < quantity:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > num_extra_items:
                        could_make = False
                        break
                    continue

            if can_make:
                can_make_recipes.append(recipe_id)
                # we dont want duplicates :)
                could_make = False

            if could_make:
                could_make_recipes.append(recipe_id)

        return can_make_recipes, could_make_recipes

def convert_quantity(ingredient):
    quant = ingredient.quantity * getattr(u, ingredient.measure)
    # convert to ml
    try:
        quant = quant.to(u.ml)
    except DimensionalityError:
        # it was weight. convert to volume
        density = ingredient.ingredient_type.density * (u.g / u.ml)
        quant = quant / density
        quant = quant.to(u.ml)
    return quant.magnitude

def initialize_suggestions():
    data = Recipe.objects.all()
    recipes = []
    for recipe in data:
        ingredients = []
        for ing in recipe.ingredient_set.all():
            ingredients.append((ing.ingredient_type.pk, convert_quantity(ing)))
        recipes.append((recipe.pk, ingredients))

    return RecipeSuggester(recipes)

