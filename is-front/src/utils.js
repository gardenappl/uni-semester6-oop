async function fetchPostJson(url, object) {
	const options = {
		method: 'POST',
		body: JSON.stringify(object),
		header: {
			'Content-Type': 'application/json'
		}
	}
	const response = await fetch(url, options);
	const text = await response.text();
	try {
		return JSON.parse(text);
	} catch (e) {
		console.error("Could not parse response:" + text);
		throw e;
	}
}

async function fetchGetJson(url) {
	const response = await fetch(url);
	const text = await response.text();
	try {
		return JSON.parse(text);
	} catch (e) {
		console.error("Could not parse response:" + text);
		throw e;
	}
}

function lastSegment(url) {
	const parts = url.split('/');
	return parts.pop() || parts.pop();  // handle potential trailing slash
}

export { fetchPostJson, fetchGetJson, lastSegment };
