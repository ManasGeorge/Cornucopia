from django.forms.models import model_to_dict
from django.http import JsonResponse
from django.shortcuts import render

from api.models import IngredientType
from api.utils import initialize_suggestions


suggest = None

# Create your views here.
def index(request):
    return HttpResponse("Cornucopia Backend API")

def suggest_ingredient(request, **kwargs):
    global suggest
    if suggest is None:
        suggest = initialize_suggestions()
    prefix = kwargs['prefix']
    data = suggest(prefix)
    return JsonResponse(data, safe=False)
