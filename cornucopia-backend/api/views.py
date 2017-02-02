from api.models import IngredientType
from django.forms.models import model_to_dict
from django.http import JsonResponse
from django.shortcuts import render


# Create your views here.
def index(request):
    return HttpResponse("Cornucopia Backend API")

def suggest_ingredient(request, **kwargs):
    prefix = kwargs['prefix']
    data = list(map(model_to_dict, IngredientType.objects.filter(name__contains=prefix)[:5]))
    return JsonResponse(data, safe=False)
