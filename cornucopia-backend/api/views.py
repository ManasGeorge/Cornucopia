from django.shortcuts import render
from django.http import HttpResponse

# Create your views here.
def index(request):
    return HttpResponse("Cornucopia Backend API")

def suggest_ingredient(request, **kwargs):
    prefix = kwargs['prefix']
    # do something
    return HttpResponse(prefix)
