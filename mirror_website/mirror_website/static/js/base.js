function changeColorTheme() {
    let theme = document.documentElement.getAttribute("color_theme");
    if(theme == "light") {
        document.documentElement.setAttribute("color_theme", "dark");
        localStorage.setItem("color_theme", "dark");
    } else {
        document.documentElement.setAttribute("color_theme", "light");
        localStorage.setItem("color_theme", "light");
    }
}

if(localStorage.getItem("color_theme") == null && window.matchMedia) {
    if(!window.matchMedia("(prefers-color-scheme: dark)").matches) {
        document.documentElement.setAttribute("color_theme", "light");
    }
} else if(localStorage.getItem("color_theme") == "dark") {
    document.documentElement.setAttribute("color_theme", "dark");
}
