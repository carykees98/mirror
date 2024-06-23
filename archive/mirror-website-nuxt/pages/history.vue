<template>
	<main>
		<div id="history">
			<h2>History</h2>
			<p>
				Mirror has been one of our longest running services, and as of today, we can be fairly certain that it now
				has existed for about 20 years with the earliest knowledge of it existing in October 2002. As part of a COSI
				History project, we have gone through the history of different pages, including past presentations, and
				websites archived on the wayback machine. With this, let’s take a journey back all the way to the beginning
				of Mirror and see it’s progress over the years.
			</p>
			<p>
				We start in December 2001. In this archive of
				<NuxtLink to="https://web.archive.org/web/20011205100928/http://cosi.clarkson.edu/">cosi.clarkson.edu
				</NuxtLink>, there is a section on the right
				titled
				Distributions which lists a few distros as well as plans of wanting to make a Clarkson University Linux
				distro. Below that it states that COSI would like to pursue mirroring other distributions on the same server
				as the website. This is the first time that we have evidence of people wanting to have a Mirror in COSI.
			</p>
			<p>
				Jump forward to February 2003 with this archive on <NuxtLink
					to="https://web.archive.org/web/20030213164427/http://mirror.clarkson.edu/">
					mirror.clarkson.edu</NuxtLink>. At this time, we see that there is
				now a set of machines actually running just for mirroring. According to the site, the storage was donated by
				Winchester Systems while the website was running on a SUN Ultra 60 workstation which was loaned out by
				Mathematics Prof. Scott Fulton. Looking through the site, we were hosting a decent amount of versions of
				Linux, BSD, and even the kernel (v2.6).
			</p>
			<p>
				Jump ahead to 2009, and the labs have looked at getting a new server for Mirror. On <NuxtLink
					to="http://web2.clarkson.edu/projects/cosi/sp2009/students/mccarrms/">Mathew McCarrell’s MP*
					Page</NuxtLink>, it was recorded that the server had been obtained and he and Roger Ignazio had worked
				on trying to
				install CentOS onto the machine, but ran into some issues. They then tried going over to Open Solaris so
				they could use ZFS as the filesystem for Mirror, we still use ZFS today. After a bit of tweaking they got
				CentOS working on Mirror and begun syncing again. Through the history, we can consider this the 2nd
				iteration of Mirror.
			</p>
			<p>
				Progressing into the 2010s and there were minor software tweaks and changes where we eventually switched
				over to Debian around 2013. And this brings us up to the fairly modern day. The end of Mirror 2 had about
				8TB of usable storage, and we were filling it up to almost max capacity. The drives were aging as well, and
				around Spring 2015, we begun plans on the 3rd iteration of Mirror. After successfully fundraising for the
				new mirror in Fall 2015, we purchased the hardware for the machine in December 2015. Through spring 2016, we
				set up the machine and retired the 2nd iteration of Mirror. At this point Mirror has ~30TB of usable space
				in the ZFS pool.
			</p>
			<p>
				Around 2022, the 4th iteration of Mirror was created. In the spring of 2022 we installed Debian 11 and
				upgraded the cache ssd, operating system ssd, and created a ZFS pool with eight 16 TB drives. With RAID we
				have about 60TB of usable space, and potentially more if we continue using the previous drives. We also
				overhalled the website, tracking, and scheduling subsystems with our own custom code.
			</p>
			<p>
				The page you are viewing now is a part of the 5th iteration of Mirror. This iteration only changed the
				software used on Mirror. With Debian 11 reaching EOL around July of 2024, we decided that we should update
				Mirror's operating system to something that would have a longer support cycle. We opted to go with Ubuntu
				22.04 since its EOL wasn't going to be until April 2027. We also overhauled most aspects of mirror by
				replacing the monolith architecture that we had been using with a more beginner friendly component based
				architecture that would make passing mirror on to the next generation of COSI members easier.
			</p>
			<p>
				As servers come and go, there has always been one server for almost the entirety of COSI’s existence that
				has been used as our Mirror.
			</p>
		</div>
		<div id="sponsors">
			<h2>Sponsors</h2>
			<p>We would like to thank the following people and companies for sponsoring mirror</p>
		</div>
	</main>
</template>

<script setup lang="ts">
const pageInfo = {
	title: "Mirror - History",
	description: "A public repository of free and open source software located at Clarkson University in Potsdam, NY, US.",
	image: "",
}

useServerSeoMeta({
	title: pageInfo.title,
	ogTitle: pageInfo.title,
	twitterTitle: pageInfo.title,
	description: pageInfo.description,
	ogDescription: pageInfo.description,
	twitterDescription: pageInfo.description,
	ogImage: pageInfo.image,
	twitterImage: pageInfo.image,
	ogUrl: "https://mirror.clarkson.edu/about",
	twitterCard: 'summary'
})

useHead({
	htmlAttrs: {
		lang: 'en'
	},
	link: [
		{
			rel: 'icon',
			type: 'image/x-icon',
			href: 'favicon.ico'
		}
	]
})
</script>