plugins {
    kotlin("jvm")
    groovy
    maven
}

group = "dev.tigrao"
version = "0.0.6"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(gradleApi())
    implementation(localGroovy())

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("junit:junit:4.12")
}
