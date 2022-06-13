package org.anvilpowered.anvil.api.registry

object ConfigDefaults {

    val CORE: ConfigSchema = config {
        "server" by {
            "name" by AnvilKeys.SERVER_NAME describe "\nThe server name"
            "timezone" by AnvilKeys.TIME_ZONE describe "\nThe time zone"
            "proxy" by AnvilKeys.PROXY_MODE describe """
            Directory for extra data
            Please note that it is not recommended to change this value from the original
            """.trimIndent()
        }
    }

    val DATA_STORE_CORE: ConfigSchema = config {
        "datastore" by {
            "dataDirectory" by AnvilKeys.DATA_DIRECTORY describe """
                Directory for extra data
                Please note that it is not recommended to change this value from the original
            """.trimIndent()
            "dataStoreName" by AnvilKeys.DATA_STORE_NAME describe "\nDetermines which storage option to use"
        }
    }

    val DATA_STORE_SHARED: ConfigSchema = config {
        "datastore" by {
            "anvil" by {
                "useSharedCredentials" by AnvilKeys.USE_SHARED_CREDENTIALS describe """
                    Whether to use Anvil's shared credentials.
                    If enabled, the following datastore settings will be inherited from Anvil's config (Requires useSharedEnvironment)
                        - mongodb.authDb
                        - mongodb.connectionString
                        - mongodb.password
                        - mongodb.username
                        - mongodb.useAuth
                        - mongodb.useConnectionString
                        - mongodb.useSrv
                    Please note: If this is enabled, the values for above settings in this config file have no effect""".trimIndent()
                "useSharedEnvironment" by AnvilKeys.USE_SHARED_ENVIRONMENT describe """
                    Whether to use Anvil's shared environment.
                    If enabled, the following datastore settings will be inherited from Anvil's config
                        - mongodb.hostname
                        - mongodb.port
                    Please note: If this is enabled, the values for above settings in this config file have no effect""".trimIndent()
            }
        }
    }

    val MONGODB: ConfigSchema = config {
        "mongodb" by {
            "authDb" by AnvilKeys.MONGODB_AUTH_DB describe "\nMongoDB database to use for authentication"
            "connectionString" by AnvilKeys.MONGODB_CONNECTION_STRING describe """
                   (Advanced) You will probably not need to use this.
                   Custom MongoDB connection string that will used instead of the connection info and credentials below
                   Will only be used if useConnectionString=true
                   """.trimIndent()
            "dbname" by AnvilKeys.MONGODB_DBNAME describe "\nMongoDB database name"
            "hostname" by AnvilKeys.MONGODB_HOSTNAME describe "\nMongoDB hostname"
            "port" by AnvilKeys.MONGODB_PORT describe "\nMongoDB port"
            "useAuth" by AnvilKeys.MONGODB_USE_AUTH describe "\nWhether to use authentication (username/password) for MongoDB"
            "useSrv" by AnvilKeys.MONGODB_USE_SRV describe "\nWhether to interpret the MongoDB hostname as an SRV record"
        }
    }
}
