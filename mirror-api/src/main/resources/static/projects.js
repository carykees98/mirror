const DISTRIBUTIONS_LIST = document.getElementById("DISTRIBUTIONS_LIST");
const SOFTWARE_LIST = document.getElementById("SOFTWARE_LIST");
const MISC_LIST = document.getElementById("MISC_LIST");

function displayMirrors() {
    fetch("/api/mirrors", {
        method: "GET",
        headers: { "Accept": "application/json" },
    }).then(async response => {
        if(!response.ok) { throw new Error(response.status); }
        const mirrors = await response.json();
        let distributions_html = "";
        let software_html = "";
        let misc_html = "";
        for(let i in mirrors) {
            switch(mirrors[i].page) {
                case "Distributions":
                    distributions_html += renderMirror(mirrors[i], i);
                    break;
                case "Software":
                    software_html += renderMirror(mirrors[i], i);
                    break;
                default:
                    misc_html += renderMirror(mirrors[i], i);
            }
            
        }
        DISTRIBUTIONS_LIST.innerHTML = distributions_html;
        SOFTWARE_LIST.innerHTML = software_html;
        MISC_LIST.innerHTML = misc_html;
    });
}

function renderMirror(mirror, i) {
    return `
        <div class="mirror_container">
            <div class="mirror_text">
                ${renderTitle(mirror)}
                <p>
                    HTTP: 
                    <a href="http://mirror.clarkson.edu/${i}">
                        http://mirror.clarkson.edu/${i}
                    </a>
                </p>
                <p>
                    HTTPS: 
                    <a href="https://mirror.clarkson.edu/${i}">
                        https://mirror.clarkson.edu/${i}
                    </a>
                </p>
                <p>
                    Homepage: 
                    <a href="${mirror.homepage}">
                        ${mirror.homepage}
                    </a>
                </p>
            </div>
            ${renderIcon(mirror)}
        </div>
    `;
}

function renderTitle(mirror) {
    if(mirror.official) { 
        return `<h3>${mirror.name} (Official Mirror)</h3>`;
    }
    return `<h3>${mirror.name}</h3>`;
}

function renderIcon(mirror) {
    if(mirror.icon == "") { return ""; }
    return `<img class="mirror_icon" loading="lazy" src="${mirror.icon}">`;
}

/* --- On page load --- */
displayMirrors();
