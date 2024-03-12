<template>
	<main>
	</main>
</template>

<script setup lang="ts">
const route = useRoute();

const pageInfo = {
	title: "Mirror - Projects",
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
	ogUrl: "https://mirror.clarkson.edu/projects",
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