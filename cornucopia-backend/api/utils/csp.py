from django.forms.models import model_to_dict
from api.models import Recipe
from pint import UnitRegistry
from pint.errors import DimensionalityError

# will first receive a bunch of recipes
# [
#  (recipe_id, [(ingredient_type, quantity_ml), (ingredient_type, quantity_ml)])
# ]
# and save them so no more db querying is needed

# given ingredients:
# [(ingredient_type, quantity_ml, days_until_expiration), (ingredient_type, quantity_ml, days_until_expiration)]
# return recipe ids

u = UnitRegistry()

def is_favorited(user_id, recipe_id):
    """Returns 1 for favorited, 2 for not favorited, which are values used for sorting"""
    try:
        # If already favorited in the past, mark as not deleted
        existing = m.Favorite.objects.filter(recipe=kwargs['id'],
                user=request.META['HTTP_TOKEN']).values_list('deleted')
        if existing[0] == True:
            return True
        else:
            return False
    except m.Favorite.DoesNotExist:
        return False
    print('BAD')
    return False

class RecipeSuggester(object):

    def __init__(self, recipes):
        self.recipes = recipes

    def __call__(self, user_id, user_ingredients, num_extra_items):
        can_make_recipes = []
        could_make_recipes = []

        if len(user_ingredients) > 0:
            user_ingredient_types, user_ingredient_quant, user_ingredient_exp = zip(*user_ingredients)
        else:
            user_ingredient_types, user_ingredient_quant, user_ingredient_exp = [], []

        for recipe_id, ingredients in self.recipes:
            can_make = True
            could_make = True
            used_items_expiration_count = 0
            extra_items_count = 0

            for ingredient_type, quantity in ingredients:
                if ingredient_type not in user_ingredient_types:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > num_extra_items:
                        could_make = False
                    continue

                # the user has the ingredient, but maybe not have enough
                user_quantity = user_ingredient_quant[
                        user_ingredient_types.index(ingredient_type)]
                if user_quantity < quantity:
                    can_make = False
                    extra_items_count += 1
                    if extra_items_count > num_extra_items:
                        could_make = False
                    continue

                # at this point, the user has the time, and at enough quantity
                used_items_expiration_count += user_ingredient_exp[
                        user_ingredient_types.index(ingredient_type)]

            if can_make:
                can_make_recipes.append((recipe_id, (
                    is_favorited(user_id, recipe_id),
                    used_items_expiration_count)))
                # we dont want duplicates :)
                could_make = False

            if could_make:
                could_make_recipes.append((recipe_id, (
                    is_favorited(user_id, recipe_id),
                    extra_items_count,
                    used_items_expiration_count)))

            # sort recipes
            can_make = [i[0] for i in sorted(can_make, key=lambda x: x[1])]
            could_make = [i[0] for i in sorted(could_make, key=lambda x: x[1])]

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

