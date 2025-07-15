// Определение глобального метода fetch
global.fetch = require('node-fetch');

const crawl = require('./crawl');

// Пример запуска функции crawl:
(async () => {
    const startingUrl = 'https://auto.ru';
    const depth = 2;
    const concurrency = 2;

    const result = await crawl(startingUrl, depth, concurrency);
    console.log(result);
})();