from django.forms.models import model_to_dict
from django.http import JsonResponse
from django.shortcuts import render

from api.models import Recipe, IngredientType
from api.utils import initialize_suggestions


suggest = None

# Create your views here.
def index(request):
    return HttpResponse("Cornucopia Backend API")

def suggest_ingredient(request, **kwargs):
    """Returns a list of suggested ingredient types, based on a query string"""
    global suggest
    if suggest is None:
        suggest = initialize_suggestions()
    prefix = kwargs['prefix']
    data = suggest(prefix)
    return JsonResponse(data, safe=False)

def by_id(request, **kwargs):
    """Returns an ingredient type based on its id"""
    ingredient = model_to_dict(IngredientType.objects.get(pk=kwargs['id']))
    return JsonResponse(ingredient)

def can_make_recipes(request):
    """Returns recipes the user can make, given their ingredients"""
    # TODO(irapha): use ingredients in the POST request to filter and order
    pass

def could_make_recipes(request):
    """Returns recipes the user could make, with their ingredients plus a few"""
    # TODO(irapha): use ingredients in the POST request to filter and order
    pass

def browse_recipes(request):
    """Returns recipes recommended to the user given their id"""
    # TODO(irapha): use user_id in POST request to retrieve favorites and use rec system
    pass
