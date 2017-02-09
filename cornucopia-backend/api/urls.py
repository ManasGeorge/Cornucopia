from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^ingredient/suggest/(?P<prefix>[a-z A-Z]+)$', views.suggest_ingredient, name='suggest_ingredient'),
    url(r'^ingredient/by_id/(?P<id>[0-9]+)$', views.by_id, name='by_id'),
    url(r'^recipe/can_make/$', views.can_make_recipes, name='can_make_recipes'),
    url(r'^recipe/could_make/$', views.could_make_recipes, name='could_make_recipes'),
    url(r'^recipe/browse/$', views.browse_recipes, name='browse_recipes'),
]
