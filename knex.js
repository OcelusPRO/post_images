let config = require("./config.json")

const knex = require('knex')({
    client          : config.sql_client,
    connection: config.database,
    pool    :   { min: 0, max: 10 }
});

module.exports = knex;