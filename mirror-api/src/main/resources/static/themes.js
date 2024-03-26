
function handleThemeToggle() {
    let current_theme = document.documentElement.getAttribute("color_theme");
    if(current_theme == "dark") {
        document.documentElement.setAttribute("color_theme", "light");
        localStorage.setItem("color_theme", "light");
    } else {
        document.documentElement.setAttribute("color_theme", "dark");
        localStorage.setItem("color_theme", "dark");
    }
}

/* ----- On page load ----- */

if(localStorage.getItem("color_theme") == "dark") {
    document.documentElement.setAttribute("color_theme", "dark");
}