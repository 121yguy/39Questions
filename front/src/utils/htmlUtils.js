export function getImageSources(html) {
    const parser = new DOMParser();
    const doc = parser.parseFromString(html, 'text/html');
    const images = doc.querySelectorAll('img');
    return Array.from(images).map(img => img.src);
}