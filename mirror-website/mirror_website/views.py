from django.http import HttpRequest
from django.views.decorators.http import require_GET
from django.shortcuts import render
from django.views.decorators.cache import cache_page
from django.conf import settings

import json

@cache_page(30)
@require_GET
def home(request: HttpRequest):
    return render(request, "home.html")

@cache_page(30)
@require_GET
def projects(request: HttpRequest):
    with open(settings.MIRRORS_FILE, "rb") as f:
        mirrors_json: dict[str, dict[str, str]] = json.loads(f.read())
    distributions: dict[str, str] = {}
    software: dict[str, str] = {}
    miscellaneous: dict[str, str] = {}
    for name, mirror in mirrors_json["mirrors"].items():
        match mirror.get("page", ""):
            case "Distributions":
                distributions[name] = mirror
            case "Software":
                software[name] = mirror
            case _:
                miscellaneous[name] = mirror
    context = {
        "distributions": distributions,
        "software": software,
        "miscellaneous": miscellaneous,
    }
    return render(request, "projects.html", context)

@cache_page(30)
@require_GET
def about(request: HttpRequest):
    return render(request, "about.html")

@cache_page(30)
@require_GET
def map(request: HttpRequest):
    return render(request, "map.html")

@cache_page(30)
@require_GET
def contact(request: HttpRequest):
    return render(request, "contact.html")
