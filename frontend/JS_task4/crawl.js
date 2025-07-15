const linkPattern = /href="(http[^"]+)"/g;

async function fetchPage(url, currentDepth) {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            return { url, depth: currentDepth, content: "", links: [] };
        }
        const htmlContent = await response.text();
        const foundLinks = [];
        let match;
        while ((match = linkPattern.exec(htmlContent)) !== null) {
            foundLinks.push(match[1]);
        }
        return { url, depth: currentDepth, content: htmlContent, links: foundLinks };
    } catch (error) {
        return { url, depth: currentDepth, content: "", links: [] };
    }
}

async function crawl(startUrl, maxDepth, maxConcurrency) {
    const tasksQueue = [{ url: startUrl, depth: maxDepth, content: "", links: [] }];
    const results = [];

    while (tasksQueue.length > 0) {
        const currentBatch = tasksQueue.splice(0, Math.min(tasksQueue.length, maxConcurrency));
        const batchResults = await Promise.all(currentBatch.map(task => fetchPage(task.url, task.depth)));

        for (const pageData of batchResults) {
            if (pageData.depth > 0) {
                results.push(pageData);
                for (const link of pageData.links) {
                    tasksQueue.push({ url: link, depth: pageData.depth - 1, content: "", links: [] });
                }
            }
        }
    }

    return results;
}

module.exports = crawl;

