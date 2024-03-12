<template>
	<main>
		<h1>Welcome to Mirror</h1>

		<p class="about">
			This site is a public repository of free and open source software
			hosted by the
			<NuxtLink to="https://cosi.clarkson.edu" target="_blank" active-class="inline-link">
				Clarkson Open Source Institute
			</NuxtLink>
			at <NuxtLink to="https://clarkson.edu" target="_blank">
				Clarkson University
			</NuxtLink> in Potsdam, NY, US. This site strives to provide
			exposure to open source software and various open source operating
			systems. This site mirrors several Linux distributions, some third
			party repositories, and miscellaneous data dumps.
			<br><br>
			You can find out more information about everything we mirror on the
			<NuxtLink to="/projects">projects</NuxtLink> page.
		</p>
	</main>
</template>

<script setup lang="ts">

const pageInfo = {
	title: "Mirror - Home",
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
	ogUrl: "https://mirror.clarkson.edu/",
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

<style scoped>
main {
	margin-top: 4%;
	text-align: center;
}

h1 {
	font-size: 60px;
}

p.about {
	margin: auto;
	max-width: 1000px;
}
</style>