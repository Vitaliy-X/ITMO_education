const { PHASE_DEVELOPMENT_SERVER, PHASE_PRODUCTION_BUILD } = require('next/constants')

async function devRewrites() {
    return [
        {
            source: '/api/:path*',
            destination: 'http://localhost:8080/api/:path*'
        }
    ]
}

module.exports = (phase, { defaultConfig }) => {
    if (phase === PHASE_DEVELOPMENT_SERVER) {
        return {
            rewrites: devRewrites
        };
    }
    if (phase === PHASE_PRODUCTION_BUILD) {
        return {
            output: "standalone"
        };
    }
    return {};
}
