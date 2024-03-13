# mirror


> # Project Goals
>
> ## Modularity
> A modular Mirror allows us to write simple components that
> [Do One Thing and Do it Well.](https://en.wikipedia.org/wiki/Unix_philosophy)
> This eases the learning curve for younger students by allowing them to work
> in a familiar environment on small programs, while keeping the opportunity to
> learn about interactions between modules in a large software project open.
>
> Each module of Mirror should be assigned an easily describable purpose,
> for example:
>
> - The sync scheduler keeps our mirrors synced with upstream mirrors.
>
> - The website serves pages to visitors of mirror.clarkson.edu
>
> - The API serves information about what we mirror to other modules.
>
> - The log server processes log messages from other modules.
>
> - The metrics engine collects data about how Mirror is used.
>
> ## Maintainability
>
> Mirror should be easy to maintain for future students. To make this possible,
> it should be documented, structured, and written in in a way that students who
> may have not yet taken higher-level CS classes can learn and contribute to.
>
> ## Community
>
> Contributing to Mirror's source code, documentation, and maintenance should be
> an experience that many students at COSI take part in. Knowledge about Mirror
> should be available both as documentation and as in-person help for 
> contributors.
>
> ## Free and Open Source Software
> 
> While Mirror's primary purpose is to run our open source mirror, it should be
> available to others who wish to mirror free and open source software. This 
> goes beyond just an open source license, Mirror should be designed with
> adaptability to different environments in mind.
>