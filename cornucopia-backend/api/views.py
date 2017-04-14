import json
from django.forms.models import model_to_dict
from django.http import JsonResponse
from django.http import HttpResponse
from django.shortcuts import render

import api.models as m
from api.utils import initialize_suggestions
from api.utils import initialize_search
from api.utils import initialize_recipe_suggestions
from api.utils import initialize_collab_filter

suggest = None
suggest_recipes = None
search = None
collab = None

# Create your views here.
def index(request):
    if not 'HTTP_TOKEN' in request.META:
        request.META['HTTP_TOKEN'] = 'none'

    return HttpResponse("Cornucopia Backend API w/ token: " + request.META['HTTP_TOKEN'])

def suggest_ingredient(request, **kwargs):
    """Returns a list of suggested ingredient types, based on a query string"""
    global suggest
    if suggest is None:
        suggest = initialize_suggestions()
    prefix = kwargs['prefix']
    data = suggest(prefix)
    return JsonResponse(data, safe=False)

def search_recipes(request, **kwargs):
    """Returns a list of suggested recipe ids, based on a query string"""
    global search
    if search is None:
        search = initialize_search()
    prefix = kwargs['prefix']
    data = search(prefix)
    return JsonResponse(data, safe=False)

def ingredient_by_id(request, **kwargs):
    """Returns an ingredient type based on its id"""
    ingredient = model_to_dict(m.IngredientType.objects.get(pk=kwargs['id']))
    return JsonResponse(ingredient)

def recipe_by_id(request, **kwargs):
    """Returns the full version of a recipe given its id"""
    recipe = m.Recipe.objects.get(pk=kwargs['id'])
    recipe_dict = model_to_dict(recipe)
    # TODO(irapha): order instructions, comments, and ingredients by their number
    recipe_dict['ingredients'] = list(map(model_to_dict, recipe.ingredient_set.all()))
    recipe_dict['instructions'] = list(map(model_to_dict, recipe.recipeinstruction_set.all()))
    recipe_dict['comments'] = list(map(model_to_dict, recipe.recipecomment_set.all()))

    # if token received, also send whether recipe is favorited by user
    if 'HTTP_TOKEN' in request.META:
        try:
            # If already favorited in the past, mark as not deleted
            existing = m.Favorite.objects.filter(recipe=kwargs['id'],
                    user=request.META['HTTP_TOKEN']).values('deleted')
            recipe_dict['favorited'] = existing[0]['deleted']
        except m.Favorite.DoesNotExist:
            recipe_dict['favorited'] = False
    return JsonResponse(recipe_dict)

@csrf_exempt
def can_make_recipes(request):
    """Returns recipes the user can make, given their ingredients"""
    if not 'HTTP_TOKEN' in request.META:
        return JsonResponse({ 'status': 'error', 'msg': 'user token not found' })
    global suggest_recipes
    if suggest_recipes is None:
        suggest_recipes = initialize_recipe_suggestions()
    if 'ingredients' not in request.POST:
        return JsonResponse({ 'status': 'error', 'msg': 'no ingredients received in POST' })
    ingredients = json.loads(request.POST['ingredients'])
    can_make, _ = suggest_recipes(request.META['HTTP_TOKEN'], ingredients, 3)
    return JsonResponse(can_make, safe=False)

@csrf_exempt
def could_make_recipes(request):
    """Returns recipes the user could make, with their ingredients plus a few"""
    if not 'HTTP_TOKEN' in request.META:
        return JsonResponse({ 'status': 'error', 'msg': 'user token not found' })
    global suggest_recipes
    if suggest_recipes is None:
        suggest_recipes = initialize_recipe_suggestions()
    if 'ingredients' not in request.POST:
        return JsonResponse({ 'status': 'error', 'msg': 'no ingredients received in POST' })
    ingredients = json.loads(request.POST['ingredients'])
    _, could_make = suggest_recipes(request.META['HTTP_TOKEN'], ingredients, 3)
    return JsonResponse(could_make, safe=False)

def browse_recipes(request):
    """Returns recipes recommended to the user given their id"""
    if not 'HTTP_TOKEN' in request.META:
        return JsonResponse({ 'status': 'error', 'msg': 'user token not found' })
    global collab
    if collab is None:
        collab = initialize_collab_filter()
    data = collab(request.META['HTTP_TOKEN'])
    if len(data) == 0:
        # temporary sane default
        data = list(map(model_to_dict, m.Recipe.objects.all()[:10]))
    return JsonResponse(data, safe=False)

def favorite_recipe(request, **kwargs):
    """Marks a recipe as a favorite for a user."""
    if not 'HTTP_TOKEN' in request.META:
        return JsonResponse({ 'status': 'error', 'msg': 'user token not found' })

    try:
        # If already favorited in the past, mark as not deleted
        existing = m.Favorite.objects.filter(recipe=kwargs['id'],
                user=request.META['HTTP_TOKEN']).update(deleted=False)
    except m.Favorite.DoesNotExist:
        # Never favorited before, create new record
        fav = m.Favorite(recipe=kwargs['id'],
                         user=request.META['HTTP_TOKEN'],
                         deleted=False).save()

    return JsonResponse({ 'status': 'success' })

def unfavorite_recipe(request, **kwargs):
    """Unmarks a recipe as a favorite for a user."""
    if not 'HTTP_TOKEN' in request.META:
        return JsonResponse({ 'status': 'error', 'msg': 'user token not found' })

    try:
        # If already favorited in the past, mark as not deleted
        existing = m.Favorite.objects.filter(recipe=kwargs['id'],
                user=request.META['HTTP_TOKEN']).update(deleted=True)
    except m.Favorite.DoesNotExist:
        return JsonResponse({ 'status': 'error', 'msg': 'favorite record not found' })

    return JsonResponse({ 'status': 'success' })
