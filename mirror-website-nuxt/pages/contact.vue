<template>
	<main>
		<div id="contact-information">
			<h2>Contact Information</h2>
			<p>

			</p>
		</div>
		<div id="request-mirror">
			<h2>Request A Mirror</h2>
		</div>
	</main>
</template>

<script setup lang="ts">
const pageInfo = {
	title: "Mirror - Contact",
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