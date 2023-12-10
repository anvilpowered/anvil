import gradle.kotlin.dsl.accessors._0ac9a36cec4eeb1254edca678008b431.publishing

plugins {
    signing
}

extensions.configure<SigningExtension> {
    if (System.getenv("AGENT_NAME")?.contains("publishing") != true) {
        return@configure
    }
    useInMemoryPgpKeys(
        /* defaultKeyId = */ System.getenv("SIGNING_KEY_ID"),
        /* defaultSecretKey = */ System.getenv("SIGNING_KEY"),
        /* defaultPassword = */ System.getenv("SIGNING_PASSWORD"),
    )
    sign(publishing.publications)
}
