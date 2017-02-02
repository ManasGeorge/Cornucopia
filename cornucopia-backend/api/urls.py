from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^suggest_ingredient/(?P<prefix>[a-z A-Z]+)$', views.suggest_ingredient, name='suggest_ingredient'),
]
