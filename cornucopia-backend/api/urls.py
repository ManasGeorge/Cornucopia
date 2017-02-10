from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^ingredient/suggest/(?P<prefix>[a-z A-Z]+)$', views.suggest_ingredient, name='suggest_ingredient'),
    url(r'^ingredient/by_id/(?P<id>[0-9]+)$', views.ingredient_by_id, name='ingredient_by_id'),
    url(r'^recipe/by_id/(?P<id>[0-9]+)$', views.recipe_by_id, name='recipe_by_id'),
    url(r'^recipe/suggest/can_make/$', views.can_make_recipes, name='can_make_recipes'),
    url(r'^recipe/suggest/could_make/$', views.could_make_recipes, name='could_make_recipes'),
    url(r'^recipe/suggest/browse/$', views.browse_recipes, name='browse_recipes'),
]
