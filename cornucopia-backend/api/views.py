from django.forms.models import model_to_dict
from django.http import JsonResponse
from django.http import HttpResponse
from django.shortcuts import render

import api.models as m
from api.utils import initialize_suggestions


suggest = None

# Create your views here.
def index(request):
    print request.META
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
    return JsonResponse(recipe_dict)

def can_make_recipes(request):
    """Returns recipes the user can make, given their ingredients"""
    # TODO(irapha): use ingredients in the POST request to filter and order
    # TODO(irapha): paginate
    data = list(map(model_to_dict, m.Recipe.objects.all()[:10]))
    data = list() #todo remove later
    return JsonResponse(data, safe=False)

def could_make_recipes(request):
    """Returns recipes the user could make, with their ingredients plus a few"""
    # TODO(irapha): use ingredients in the POST request to filter and order
    # TODO(irapha): paginate
    data = list(map(model_to_dict, m.Recipe.objects.all()[:10]))
    return JsonResponse(data, safe=False)

def browse_recipes(request):
    """Returns recipes recommended to the user given their id"""
    # TODO(irapha): use user_id in POST request to retrieve favorites and use rec system
    # TODO(irapha): paginate
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
    except Favorite.DoesNotExist:
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
    except Favorite.DoesNotExist:
        return JsonResponse({ 'status': 'error', 'msg': 'favorite record not found' })

    return JsonResponse({ 'status': 'success' })
