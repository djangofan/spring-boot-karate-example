
function() {
    var env = karate.properties['test.env']

    karate.configure('connectTimeout', 30 * 1000)
    karate.configure('readTimeout', 30 * 1000)

    var config = {
        env: env,
        global: read("classpath:global.json")[env],
        jdbc: read("classpath:jdbc.json")[env]
    };

    for (p in config) {
        karate.log ("##### config: " + p, ": " + config[p])
    }
    return config;
}
