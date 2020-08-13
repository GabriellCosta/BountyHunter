plugins {
    kotlin("jvm")
    groovy
    maven
    `maven-publish`
}

group = "dev.tigrao"
version = "0.0.5"

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(localGroovy())

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("junit:junit:4.12")
}
